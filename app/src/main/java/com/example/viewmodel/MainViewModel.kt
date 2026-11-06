package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.audio.AudioPlayerEngine
import com.example.audio.SongComposerEngine
import com.example.audio.SongExportHelper
import com.example.audio.VocalSynthesizer
import com.example.audio.VoiceRecordAnalyzer
import com.example.data.SongRepository
import com.example.model.MusicGenre
import com.example.model.Song
import com.example.model.SongLanguage
import com.example.model.VoiceProfile
import com.example.model.VoiceType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class ScreenTab(val title: String, val hindiTitle: String) {
    STUDIO("Studio", "स्टूडियो"),
    VOICE_CLONE("Voice Clone", "आवाज़ क्लोन"),
    PLAYER("Player", "प्लेयर"),
    LIBRARY("Library", "लाइब्रेरी")
}

class MainViewModel(application: Application) : AndroidViewModel(application) {

    val repository = SongRepository(application, viewModelScope)
    val vocalSynthesizer = VocalSynthesizer(application)
    val playerEngine = AudioPlayerEngine(application)
    val recordAnalyzer = VoiceRecordAnalyzer(application)
    private val composerEngine = SongComposerEngine(application)

    // Navigation Tab
    private val _currentTab = MutableStateFlow(ScreenTab.STUDIO)
    val currentTab: StateFlow<ScreenTab> = _currentTab.asStateFlow()

    // Studio Input States
    val promptText = MutableStateFlow("")
    val selectedVoiceType = MutableStateFlow(VoiceType.MALE)
    val selectedGenre = MutableStateFlow(MusicGenre.ROMANTIC)
    val selectedLanguage = MutableStateFlow(SongLanguage.HINDI)
    val selectedInstruments = MutableStateFlow(
        listOf("बांसुरी (Flute)", "हारमोनियम (Harmonium)", "तबला (Tabla)")
    )

    // Generation Progress State
    private val _isGenerating = MutableStateFlow(false)
    val isGenerating: StateFlow<Boolean> = _isGenerating.asStateFlow()

    private val _generationStatus = MutableStateFlow("")
    val generationStatus: StateFlow<String> = _generationStatus.asStateFlow()

    // Voice Clone Lab States
    val activeVoiceProfile: StateFlow<VoiceProfile?> = repository.activeVoiceProfile
    val voiceProfiles: StateFlow<List<VoiceProfile>> = repository.voiceProfiles

    fun selectTab(tab: ScreenTab) {
        _currentTab.value = tab
    }

    fun toggleInstrument(instrument: String) {
        val current = selectedInstruments.value.toMutableList()
        if (current.contains(instrument)) {
            if (current.size > 1) current.remove(instrument)
        } else {
            current.add(instrument)
        }
        selectedInstruments.value = current
    }

    fun generateSong() {
        val prompt = promptText.value.trim().ifEmpty {
            "एक दिल को छू लेने वाला मधुर ${selectedGenre.value.hindiTitle} गाना"
        }

        _isGenerating.value = true
        _generationStatus.value = "AI रचना प्रारंभ हो रही है..."

        viewModelScope.launch {
            try {
                val newSong = composerEngine.composeSong(
                    prompt = prompt,
                    genre = selectedGenre.value,
                    voiceType = selectedVoiceType.value,
                    language = selectedLanguage.value,
                    selectedInstruments = selectedInstruments.value,
                    vocalSynthesizer = vocalSynthesizer,
                    clonedProfile = repository.activeVoiceProfile.value
                ) { status ->
                    _generationStatus.value = status
                }

                repository.addSong(newSong)
                _isGenerating.value = false
                // Play immediately and navigate to Player tab!
                playerEngine.playSong(newSong, viewModelScope, startPlaying = true)
                _currentTab.value = ScreenTab.PLAYER
            } catch (e: Exception) {
                _generationStatus.value = "त्रुटि: ${e.localizedMessage ?: "अज्ञात त्रुटि"}"
                _isGenerating.value = false
            }
        }
    }

    fun startVoiceRecording() {
        recordAnalyzer.startRecording(viewModelScope)
    }

    fun stopVoiceRecording(profileName: String) {
        val profile = recordAnalyzer.stopRecordingAndAnalyze(profileName)
        if (profile != null) {
            repository.addVoiceProfile(profile)
        }
    }

    fun testSingClonedVoice() {
        val profile = repository.activeVoiceProfile.value
        val pitch = if (profile != null) profile.fundamentalPitchHz / 165f else 1.0f
        vocalSynthesizer.speakLine(
            text = "सा रे गा मा पा धा नि सा... मेरी आवाज़ में आपका स्वागत है!",
            voiceType = VoiceType.CLONED,
            language = SongLanguage.HINDI
        )
    }

    fun playSong(song: Song) {
        playerEngine.playSong(song, viewModelScope, startPlaying = true)
        _currentTab.value = ScreenTab.PLAYER
    }

    fun downloadCurrentSong() {
        val song = playerEngine.currentSong.value ?: return
        SongExportHelper.downloadSong(getApplication(), song)
    }

    fun shareCurrentSong() {
        val song = playerEngine.currentSong.value ?: return
        SongExportHelper.shareSong(getApplication(), song)
    }

    fun shareSpecificSong(song: Song) {
        SongExportHelper.shareSong(getApplication(), song)
    }

    fun copyCurrentLyrics() {
        val song = playerEngine.currentSong.value ?: return
        SongExportHelper.copyLyrics(getApplication(), song)
    }

    override fun onCleared() {
        super.onCleared()
        playerEngine.release()
        vocalSynthesizer.release()
    }
}
