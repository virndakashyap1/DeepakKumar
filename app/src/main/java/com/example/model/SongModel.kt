package com.example.model

import java.util.UUID

enum class VoiceType(val displayName: String, val hindiName: String, val pitch: Float, val speed: Float) {
    MALE("AI Male", "मधुर पुरुष", 0.82f, 0.95f),
    FEMALE("AI Female", "मधुर महिला", 1.35f, 0.98f),
    DUET("AI Duet", "युगल गीत", 1.0f, 0.96f),
    CLONED("My Cloned Voice", "मेरी आवाज़ (Clone)", 1.0f, 0.95f)
}

enum class MusicGenre(val title: String, val hindiTitle: String, val bpm: Int, val scaleName: String) {
    ROMANTIC("Romantic Melody", "रोमांटिक मधुर", 82, "Raag Bhairavi"),
    SUFI("Sufi Mystical", "सूफी रूहानी", 95, "Raag Yaman"),
    BHAJAN("Devotional Bhajan", "भक्ति भजन", 88, "Raag Bilawal"),
    GHAZAL("Classical Ghazal", "शास्त्रीय गज़ल", 76, "Raag Darbari"),
    FOLK_DESI("Desi Folk / Dholak", "देसी लोकगीत", 112, "Raag Khamaj"),
    POP_DANCE("EDM / Party Beats", "पार्टी व डांस", 126, "Modern Minor")
}

enum class SongLanguage(val code: String, val label: String, val hindiLabel: String) {
    HINDI("hi", "Hindi", "हिंदी"),
    BHOJPURI("bho", "Bhojpuri", "भोजपुरी"),
    PUNJABI("pa", "Punjabi", "पंजाबी"),
    HARYANVI("bgc", "Haryanvi", "हरियाणवी"),
    ENGLISH("en", "English", "English"),
    BENGALI("bn", "Bengali", "বাংলা"),
    TAMIL("ta", "Tamil", "தமிழ்")
}

data class LyricStanza(
    val section: String, // "मुखड़ा (Mukhda)", "अंतरा १ (Antara 1)", "अंतरा २ (Antara 2)", "समर्पण (Outro)"
    val lines: List<String>,
    val singerVoice: VoiceType,
    val timestampSeconds: Int
)

data class Song(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val prompt: String,
    val lyrics: List<LyricStanza>,
    val voiceType: VoiceType,
    val genre: MusicGenre,
    val language: SongLanguage,
    val instruments: List<String>,
    val durationSeconds: Int,
    val audioFilePath: String? = null,
    val isFavorite: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

data class VoiceProfile(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val fundamentalPitchHz: Float, // e.g. 150 Hz
    val pitchCategory: String, // "Baritone", "Tenor", "Alto", "Soprano"
    val warmthFactor: Float, // 0.0 to 1.0
    val vibratoFactor: Float, // 0.0 to 1.0
    val sampleAudioPath: String? = null,
    val isCalibrated: Boolean = true,
    val createdDate: Long = System.currentTimeMillis()
)
