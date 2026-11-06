package com.example.audio

import android.content.Context
import android.media.MediaPlayer
import android.media.PlaybackParams
import android.os.Build
import android.util.Log
import com.example.model.Song
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.io.File
import kotlin.math.sin
import kotlin.random.Random

class AudioPlayerEngine(private val context: Context) {

    private var mediaPlayer: MediaPlayer? = null
    private var trackerJob: Job? = null

    private val _currentSong = MutableStateFlow<Song?>(null)
    val currentSong: StateFlow<Song?> = _currentSong.asStateFlow()

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _currentPositionMs = MutableStateFlow(0)
    val currentPositionMs: StateFlow<Int> = _currentPositionMs.asStateFlow()

    private val _durationMs = MutableStateFlow(1)
    val durationMs: StateFlow<Int> = _durationMs.asStateFlow()

    private val _visualizerAmplitudes = MutableStateFlow(List(32) { 0.15f })
    val visualizerAmplitudes: StateFlow<List<Float>> = _visualizerAmplitudes.asStateFlow()

    private val _playbackSpeed = MutableStateFlow(1.0f)
    val playbackSpeed: StateFlow<Float> = _playbackSpeed.asStateFlow()

    private val _isLooping = MutableStateFlow(false)
    val isLooping: StateFlow<Boolean> = _isLooping.asStateFlow()

    private val _isKaraokeMode = MutableStateFlow(false)
    val isKaraokeMode: StateFlow<Boolean> = _isKaraokeMode.asStateFlow()

    fun playSong(song: Song, scope: CoroutineScope, startPlaying: Boolean = true) {
        _currentSong.value = song
        stop()

        val filePath = song.audioFilePath
        if (filePath != null && File(filePath).exists()) {
            try {
                mediaPlayer = MediaPlayer().apply {
                    setDataSource(filePath)
                    isLooping = _isLooping.value
                    prepare()
                    _durationMs.value = duration.coerceAtLeast(1000)
                    if (startPlaying) {
                        start()
                        _isPlaying.value = true
                    }
                    setOnCompletionListener {
                        _isPlaying.value = false
                        _currentPositionMs.value = 0
                    }
                }
                applySpeed(_playbackSpeed.value)
            } catch (e: Exception) {
                Log.e("AudioPlayerEngine", "Error preparing MediaPlayer for file: $filePath", e)
            }
        } else {
            // Virtual playback duration if audio file wasn't generated
            _durationMs.value = song.durationSeconds * 1000
            if (startPlaying) _isPlaying.value = true
        }

        startTracker(scope)
    }

    private fun startTracker(scope: CoroutineScope) {
        trackerJob?.cancel()
        trackerJob = scope.launch(Dispatchers.Main) {
            var wavePhase = 0.0
            while (isActive) {
                if (_isPlaying.value) {
                    if (mediaPlayer != null) {
                        try {
                            _currentPositionMs.value = mediaPlayer?.currentPosition ?: 0
                            _durationMs.value = (mediaPlayer?.duration ?: 1).coerceAtLeast(1000)
                        } catch (e: Exception) {
                            // ignore in case state changes
                        }
                    } else {
                        // Increment virtual position
                        val next = _currentPositionMs.value + 50
                        if (next >= _durationMs.value) {
                            if (_isLooping.value) {
                                _currentPositionMs.value = 0
                            } else {
                                _currentPositionMs.value = 0
                                _isPlaying.value = false
                            }
                        } else {
                            _currentPositionMs.value = next
                        }
                    }

                    // Generate dynamic visualizer bar amplitudes
                    wavePhase += 0.25
                    val bars = List(32) { index ->
                        val base = 0.2f + 0.5f * sin(wavePhase + index * 0.35).toFloat().coerceAtLeast(0f)
                        val noise = (Random.nextFloat() * 0.3f)
                        (base + noise).coerceIn(0.08f, 1.0f)
                    }
                    _visualizerAmplitudes.value = bars
                } else {
                    // Decay visualizer bars when paused
                    _visualizerAmplitudes.value = _visualizerAmplitudes.value.map { it * 0.85f }
                }
                delay(50)
            }
        }
    }

    fun togglePlayPause() {
        val mp = mediaPlayer
        if (mp != null) {
            if (mp.isPlaying) {
                mp.pause()
                _isPlaying.value = false
            } else {
                mp.start()
                _isPlaying.value = true
            }
        } else if (_currentSong.value != null) {
            _isPlaying.value = !_isPlaying.value
        }
    }

    fun seekTo(positionMs: Int) {
        val target = positionMs.coerceIn(0, _durationMs.value)
        _currentPositionMs.value = target
        try {
            mediaPlayer?.seekTo(target)
        } catch (e: Exception) {
            Log.e("AudioPlayerEngine", "Seek failed", e)
        }
    }

    fun seekRelative(offsetMs: Int) {
        seekTo(_currentPositionMs.value + offsetMs)
    }

    fun setSpeed(speed: Float) {
        _playbackSpeed.value = speed
        applySpeed(speed)
    }

    private fun applySpeed(speed: Float) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            try {
                mediaPlayer?.let { mp ->
                    if (mp.isPlaying) {
                        mp.playbackParams = PlaybackParams().apply { this.speed = speed }
                    }
                }
            } catch (e: Exception) {
                Log.e("AudioPlayerEngine", "Failed setting speed", e)
            }
        }
    }

    fun toggleLoop() {
        val next = !_isLooping.value
        _isLooping.value = next
        mediaPlayer?.isLooping = next
    }

    fun toggleKaraoke() {
        _isKaraokeMode.value = !_isKaraokeMode.value
    }

    fun stop() {
        try {
            mediaPlayer?.stop()
            mediaPlayer?.release()
        } catch (e: Exception) {
            // ignore
        }
        mediaPlayer = null
        _isPlaying.value = false
        _currentPositionMs.value = 0
    }

    fun release() {
        stop()
        trackerJob?.cancel()
    }
}
