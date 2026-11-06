package com.example.data

import android.content.Context
import com.example.audio.AudioSynthEngine
import com.example.audio.WavFileHelper
import com.example.model.LyricStanza
import com.example.model.MusicGenre
import com.example.model.Song
import com.example.model.SongLanguage
import com.example.model.VoiceProfile
import com.example.model.VoiceType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File
import java.util.UUID

class SongRepository(private val context: Context, scope: CoroutineScope) {

    private val _songs = MutableStateFlow<List<Song>>(emptyList())
    val songs: StateFlow<List<Song>> = _songs.asStateFlow()

    private val _voiceProfiles = MutableStateFlow<List<VoiceProfile>>(emptyList())
    val voiceProfiles: StateFlow<List<VoiceProfile>> = _voiceProfiles.asStateFlow()

    private val _activeVoiceProfile = MutableStateFlow<VoiceProfile?>(null)
    val activeVoiceProfile: StateFlow<VoiceProfile?> = _activeVoiceProfile.asStateFlow()

    init {
        // Initialize default starter voice profile
        val defaultProfile = VoiceProfile(
            name = "मेरी आवाज़ (My Voice Model #1)",
            fundamentalPitchHz = 165f,
            pitchCategory = "Baritone (मधुर बैरीटोन)",
            warmthFactor = 0.75f,
            vibratoFactor = 0.40f,
            isCalibrated = true
        )
        _voiceProfiles.value = listOf(defaultProfile)
        _activeVoiceProfile.value = defaultProfile

        // Bootstrap pre-loaded songs and synthesize their audio files asynchronously
        scope.launch(Dispatchers.IO) {
            val starterSongs = createStarterSongs()
            _songs.value = starterSongs
        }
    }

    private fun createStarterSongs(): List<Song> {
        val song1File = File(context.filesDir, "sample_sawan_raat.wav")
        if (!song1File.exists()) {
            val pcm = AudioSynthEngine.synthesizeInstrumental(MusicGenre.ROMANTIC, 30, listOf("बांसुरी (Flute)", "हारमोनियम (Harmonium)", "तबला (Tabla)"))
            WavFileHelper.writeWavFile(song1File, pcm, AudioSynthEngine.SAMPLE_RATE, 1)
        }

        val song2File = File(context.filesDir, "sample_krishna_dhun.wav")
        if (!song2File.exists()) {
            val pcm = AudioSynthEngine.synthesizeInstrumental(MusicGenre.BHAJAN, 30, listOf("बांसुरी (Flute)", "सितार (Sitar)", "तानपुरा (Tanpura)"))
            WavFileHelper.writeWavFile(song2File, pcm, AudioSynthEngine.SAMPLE_RATE, 1)
        }

        val song3File = File(context.filesDir, "sample_sufi_safar.wav")
        if (!song3File.exists()) {
            val pcm = AudioSynthEngine.synthesizeInstrumental(MusicGenre.SUFI, 28, listOf("हारमोनियम (Harmonium)", "सितार (Sitar)", "ढोलक (Dholak)"))
            WavFileHelper.writeWavFile(song3File, pcm, AudioSynthEngine.SAMPLE_RATE, 1)
        }

        return listOf(
            Song(
                id = "sample_1",
                title = "बरसात की भीगी रातें",
                prompt = "रिमझिम बारिश में पुरानी यादों का एक खूबसूरत रोमांटिक गाना",
                lyrics = listOf(
                    LyricStanza(
                        section = "मुखड़ा (Mukhda)",
                        lines = listOf("रिमझिम बरसे सावन की बूँदें, यादें तेरी जगाए", "भीगी भीगी इन रातों में दिल तुझको ही बुलाए"),
                        singerVoice = VoiceType.MALE,
                        timestampSeconds = 0
                    ),
                    LyricStanza(
                        section = "अंतरा (Antara)",
                        lines = listOf("हवाएं छूके जब भी गुजरती हैं इस दिल को", "तेरी खुशबू का अहसास करा जाती हैं हर पल को"),
                        singerVoice = VoiceType.MALE,
                        timestampSeconds = 12
                    ),
                    LyricStanza(
                        section = "समर्पण (Outro)",
                        lines = listOf("तू ही मेरी मंजिल है, तू ही मेरी सुबह और शाम", "हर सांस पे लिखा है बस तेरा ही मधुर नाम"),
                        singerVoice = VoiceType.MALE,
                        timestampSeconds = 22
                    )
                ),
                voiceType = VoiceType.MALE,
                genre = MusicGenre.ROMANTIC,
                language = SongLanguage.HINDI,
                instruments = listOf("बांसुरी (Flute)", "हारमोनियम (Harmonium)", "तबला (Tabla)"),
                durationSeconds = 30,
                audioFilePath = song1File.absolutePath,
                isFavorite = true
            ),
            Song(
                id = "sample_2",
                title = "श्री कृष्ण की मुरली धुन",
                prompt = "वृंदावन में बजती कृष्ण कन्हैया की मधुर मुरली का युगल भजन",
                lyrics = listOf(
                    LyricStanza(
                        section = "मुखड़ा (Mukhda)",
                        lines = listOf("अधर धरत मुरली बाजे रे", "मनमोहन गिरधारी साजे रे"),
                        singerVoice = VoiceType.MALE,
                        timestampSeconds = 0
                    ),
                    LyricStanza(
                        section = "अंतरा (Antara)",
                        lines = listOf("यमुना तट पर धेनु चरावे", "मधुर मधुर सुर मन हरषावे"),
                        singerVoice = VoiceType.FEMALE,
                        timestampSeconds = 12
                    ),
                    LyricStanza(
                        section = "समर्पण (Outro)",
                        lines = listOf("राधे कृष्ण की जय जय गाओ", "भव सागर से मुक्ति पाओ"),
                        singerVoice = VoiceType.DUET,
                        timestampSeconds = 22
                    )
                ),
                voiceType = VoiceType.DUET,
                genre = MusicGenre.BHAJAN,
                language = SongLanguage.HINDI,
                instruments = listOf("बांसुरी (Flute)", "सितार (Sitar)", "तानपुरा (Tanpura)"),
                durationSeconds = 30,
                audioFilePath = song2File.absolutePath,
                isFavorite = true
            ),
            Song(
                id = "sample_3",
                title = "रूहानी सफर की दास्तान",
                prompt = "सूफी अंदाज में दिल को छू लेने वाली शास्त्रीय महिला गज़ल",
                lyrics = listOf(
                    LyricStanza(
                        section = "मुखड़ा (Mukhda)",
                        lines = listOf("रूह से रूह का यह रिश्ता कैसा", "इश्क का दरिया बहता बहता जैसा"),
                        singerVoice = VoiceType.FEMALE,
                        timestampSeconds = 0
                    ),
                    LyricStanza(
                        section = "अंतरा (Antara)",
                        lines = listOf("खामोशियों में भी तेरी सदा है", "तू ही मेरी हर दर्द की दवा है"),
                        singerVoice = VoiceType.FEMALE,
                        timestampSeconds = 11
                    ),
                    LyricStanza(
                        section = "समर्पण (Outro)",
                        lines = listOf("सजदा करूं मैं तेरी रूहानियत को", "तूने संवारा मेरी ज़िंदगानी को"),
                        singerVoice = VoiceType.FEMALE,
                        timestampSeconds = 20
                    )
                ),
                voiceType = VoiceType.FEMALE,
                genre = MusicGenre.SUFI,
                language = SongLanguage.HINDI,
                instruments = listOf("हारमोनियम (Harmonium)", "सितार (Sitar)", "ढोलक (Dholak)"),
                durationSeconds = 28,
                audioFilePath = song3File.absolutePath,
                isFavorite = false
            )
        )
    }

    fun addSong(song: Song) {
        _songs.value = listOf(song) + _songs.value
    }

    fun deleteSong(songId: String) {
        _songs.value = _songs.value.filter { it.id != songId }
    }

    fun toggleFavorite(songId: String) {
        _songs.value = _songs.value.map {
            if (it.id == songId) it.copy(isFavorite = !it.isFavorite) else it
        }
    }

    fun addVoiceProfile(profile: VoiceProfile) {
        _voiceProfiles.value = listOf(profile) + _voiceProfiles.value
        _activeVoiceProfile.value = profile
    }

    fun selectVoiceProfile(profileId: String) {
        _activeVoiceProfile.value = _voiceProfiles.value.find { it.id == profileId }
    }
}
