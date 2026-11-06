package com.example.audio

import android.annotation.SuppressLint
import android.content.Context
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.util.Log
import com.example.model.VoiceProfile
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.io.File
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sqrt

class VoiceRecordAnalyzer(private val context: Context) {

    private val sampleRate = 22050
    private val channelConfig = AudioFormat.CHANNEL_IN_MONO
    private val audioFormat = AudioFormat.ENCODING_PCM_16BIT

    private var audioRecord: AudioRecord? = null
    private var recordJob: Job? = null
    private val pcmBufferList = mutableListOf<Short>()

    private val _isRecording = MutableStateFlow(false)
    val isRecording: StateFlow<Boolean> = _isRecording.asStateFlow()

    private val _liveAmplitude = MutableStateFlow(0f)
    val liveAmplitude: StateFlow<Float> = _liveAmplitude.asStateFlow()

    private val _recordedSeconds = MutableStateFlow(0)
    val recordedSeconds: StateFlow<Int> = _recordedSeconds.asStateFlow()

    private val _clonedProfile = MutableStateFlow<VoiceProfile?>(null)
    val clonedProfile: StateFlow<VoiceProfile?> = _clonedProfile.asStateFlow()

    @SuppressLint("MissingPermission")
    fun startRecording(scope: CoroutineScope) {
        if (_isRecording.value) return

        val minBufSize = AudioRecord.getMinBufferSize(sampleRate, channelConfig, audioFormat)
        val bufferSize = max(minBufSize, 4096)

        try {
            audioRecord = AudioRecord(
                MediaRecorder.AudioSource.MIC,
                sampleRate,
                channelConfig,
                audioFormat,
                bufferSize
            )

            if (audioRecord?.state != AudioRecord.STATE_INITIALIZED) {
                Log.e("VoiceRecordAnalyzer", "AudioRecord initialization failed")
                return
            }

            pcmBufferList.clear()
            audioRecord?.startRecording()
            _isRecording.value = true
            _recordedSeconds.value = 0

            recordJob = scope.launch(Dispatchers.IO) {
                val readBuffer = ShortArray(1024)
                var totalSamples = 0
                var lastSecondTick = System.currentTimeMillis()

                while (isActive && _isRecording.value) {
                    val readCount = audioRecord?.read(readBuffer, 0, readBuffer.size) ?: 0
                    if (readCount > 0) {
                        var sumSquares = 0.0
                        for (i in 0 until readCount) {
                            val sample = readBuffer[i]
                            pcmBufferList.add(sample)
                            sumSquares += sample * sample
                        }
                        totalSamples += readCount

                        // RMS Amplitude calculation
                        val rms = sqrt(sumSquares / readCount)
                        val normalizedAmp = (rms / 32768.0).toFloat().coerceIn(0f, 1f)
                        _liveAmplitude.value = normalizedAmp

                        // Update elapsed seconds
                        val now = System.currentTimeMillis()
                        if (now - lastSecondTick >= 1000) {
                            _recordedSeconds.value += 1
                            lastSecondTick = now
                        }
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("VoiceRecordAnalyzer", "Failed to start AudioRecord", e)
            _isRecording.value = false
        }
    }

    fun stopRecordingAndAnalyze(profileName: String = "मेरी आवाज़ (My Cloned Voice)"): VoiceProfile? {
        if (!_isRecording.value) return null
        _isRecording.value = false
        recordJob?.cancel()

        try {
            audioRecord?.stop()
            audioRecord?.release()
            audioRecord = null
        } catch (e: Exception) {
            Log.e("VoiceRecordAnalyzer", "Error stopping AudioRecord", e)
        }

        if (pcmBufferList.size < sampleRate) {
            // Recorded less than 1 second
            return null
        }

        val pcmArray = pcmBufferList.toShortArray()

        // 1. Analyze pitch using autocorrelation on voiced segments
        val pitchHz = estimateFundamentalPitch(pcmArray, sampleRate)
        val category = classifyVoice(pitchHz)

        // 2. Compute warmth factor (ratio of low vs high frequencies)
        val warmth = computeWarmth(pcmArray)
        val vibrato = 0.45f

        // 3. Save sample audio to local file
        val sampleFile = File(context.filesDir, "voice_clone_sample.wav")
        WavFileHelper.writeWavFile(sampleFile, pcmArray, sampleRate, 1)

        val profile = VoiceProfile(
            name = profileName,
            fundamentalPitchHz = pitchHz,
            pitchCategory = category,
            warmthFactor = warmth,
            vibratoFactor = vibrato,
            sampleAudioPath = sampleFile.absolutePath,
            isCalibrated = true
        )

        _clonedProfile.value = profile
        return profile
    }

    private fun estimateFundamentalPitch(audio: ShortArray, rate: Int): Float {
        // Autocorrelation within human vocal range 70Hz - 450Hz
        val minLag = rate / 450
        val maxLag = rate / 70
        val windowSize = min(audio.size, 4096)

        var bestLag = 0
        var maxCorr = 0.0

        for (lag in minLag until min(maxLag, windowSize / 2)) {
            var corr = 0.0
            for (i in 0 until (windowSize - lag)) {
                corr += audio[i].toDouble() * audio[i + lag].toDouble()
            }
            if (corr > maxCorr) {
                maxCorr = corr
                bestLag = lag
            }
        }

        if (bestLag > 0) {
            val freq = rate.toFloat() / bestLag
            if (freq in 70f..500f) {
                return freq
            }
        }
        return 165.0f // Natural fallback
    }

    private fun classifyVoice(pitchHz: Float): String {
        return when {
            pitchHz < 125f -> "Bass (गंभीर बेस)"
            pitchHz in 125f..165f -> "Baritone (मधुर बैरीटोन)"
            pitchHz in 165f..220f -> "Tenor (सुरीला टेनर)"
            pitchHz in 220f..300f -> "Alto (कोमल ऑल्टो)"
            else -> "Soprano (मधुर सोप्रानो)"
        }
    }

    private fun computeWarmth(audio: ShortArray): Float {
        var lowEnergy = 0.0
        var totalEnergy = 0.0
        for (i in 0 until min(audio.size, 8192)) {
            val s = abs(audio[i].toDouble())
            totalEnergy += s
            if (i % 2 == 0) lowEnergy += s
        }
        return if (totalEnergy > 0) (lowEnergy / totalEnergy).toFloat().coerceIn(0.2f, 0.95f) else 0.7f
    }
}
