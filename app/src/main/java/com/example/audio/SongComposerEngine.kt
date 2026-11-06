package com.example.audio

import android.content.Context
import com.example.model.LyricStanza
import com.example.model.MusicGenre
import com.example.model.Song
import com.example.model.SongLanguage
import com.example.model.VoiceProfile
import com.example.model.VoiceType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.util.UUID

class SongComposerEngine(private val context: Context) {

    private val synthEngine = AudioSynthEngine

    suspend fun composeSong(
        prompt: String,
        genre: MusicGenre,
        voiceType: VoiceType,
        language: SongLanguage,
        selectedInstruments: List<String>,
        vocalSynthesizer: VocalSynthesizer,
        clonedProfile: VoiceProfile? = null,
        onProgressUpdate: (String) -> Unit
    ): Song = withContext(Dispatchers.IO) {

        onProgressUpdate("AI द्वारा काव्य व बोल (Lyrics) तैयार किए जा रहे हैं...")
        val (title, lyrics) = generateStructuredLyrics(prompt, genre, language, voiceType)

        val totalDurationSec = 38 // Full demo composition duration

        onProgressUpdate("संगीत व धुनों (Instrumental) की रिकॉर्डिंग हो रही है...")
        val instrumentalPcm = synthEngine.synthesizeInstrumental(genre, totalDurationSec, selectedInstruments)

        onProgressUpdate("AI मधुर आवाज़ में गायन (Vocal Synthesis) हो रहा है...")
        val allLyricsText = lyrics.flatMap { it.lines }.joinToString(separator = ". ")
        val vocalTempFile = File(context.cacheDir, "temp_vocals_${UUID.randomUUID()}.wav")

        val pitchToUse = if (voiceType == VoiceType.CLONED && clonedProfile != null) {
            (clonedProfile.fundamentalPitchHz / 165f).coerceIn(0.7f, 1.4f)
        } else null

        val vocalSuccess = vocalSynthesizer.synthesizeTextToFile(
            text = allLyricsText,
            outputFile = vocalTempFile,
            voiceType = voiceType,
            language = language,
            customPitch = pitchToUse
        )

        onProgressUpdate("ऑडियो मास्टरिंग एवं मिक्सिंग जारी है...")
        val songFile = File(context.filesDir, "song_${UUID.randomUUID()}.wav")

        val finalPcm = if (vocalSuccess && vocalTempFile.exists()) {
            val vocalPcm = WavFileHelper.readPcmFromWav(vocalTempFile)
            if (vocalPcm != null && vocalPcm.isNotEmpty()) {
                WavFileHelper.mixPcmTracks(instrumentalPcm, vocalPcm, weight1 = 0.55f, weight2 = 0.85f)
            } else {
                instrumentalPcm
            }
        } else {
            instrumentalPcm
        }

        WavFileHelper.writeWavFile(songFile, finalPcm, AudioSynthEngine.SAMPLE_RATE, 1)

        onProgressUpdate("गाना पूर्णतः तैयार है! 🎶")

        Song(
            id = UUID.randomUUID().toString(),
            title = title,
            prompt = prompt,
            lyrics = lyrics,
            voiceType = voiceType,
            genre = genre,
            language = language,
            instruments = selectedInstruments,
            durationSeconds = totalDurationSec,
            audioFilePath = songFile.absolutePath,
            isFavorite = false,
            createdAt = System.currentTimeMillis()
        )
    }

    private fun generateStructuredLyrics(
        prompt: String,
        genre: MusicGenre,
        language: SongLanguage,
        voiceType: VoiceType
    ): Pair<String, List<LyricStanza>> {
        val pLower = prompt.lowercase()

        val isRomantic = pLower.contains("प्यार") || pLower.contains("इश्क") || pLower.contains("रोमांटिक") ||
                pLower.contains("love") || pLower.contains("दिल") || genre == MusicGenre.ROMANTIC
        val isRain = pLower.contains("बारिश") || pLower.contains("बरसात") || pLower.contains("सावन") || pLower.contains("rain")
        val isBhajan = pLower.contains("भजन") || pLower.contains("कृष्ण") || pLower.contains("राम") ||
                pLower.contains("भगवान") || pLower.contains("भक्ति") || genre == MusicGenre.BHAJAN
        val isParty = pLower.contains("पार्टी") || pLower.contains("party") || pLower.contains("dance") ||
                pLower.contains("नाच") || pLower.contains("ढोलक") || genre == MusicGenre.POP_DANCE || genre == MusicGenre.FOLK_DESI
        val isSufi = pLower.contains("सूफी") || pLower.contains("sufi") || pLower.contains("रूह") || genre == MusicGenre.SUFI

        val title: String
        val stanzas: List<LyricStanza>

        when {
            language == SongLanguage.PUNJABI -> {
                title = if (isParty) "नाचेंगे सारे भांगड़ा" else "दिल दियां गल्लां"
                stanzas = listOf(
                    LyricStanza(
                        section = "मुखड़ा (Mukhda)",
                        lines = listOf("तेरी अक्खां विच जादू कोई साड्डा दिल ले गया", "रंग तेरे इश्क़ दा साड्डे ते चढ़ गया"),
                        singerVoice = if (voiceType == VoiceType.DUET) VoiceType.MALE else voiceType,
                        timestampSeconds = 0
                    ),
                    LyricStanza(
                        section = "अंतरा (Antara)",
                        lines = listOf("ढोल वज्दा ते नचदी दुनिया सारी वे", "तेरे नाल जुड़ गई मेरी प्रीता प्यारी वे"),
                        singerVoice = if (voiceType == VoiceType.DUET) VoiceType.FEMALE else voiceType,
                        timestampSeconds = 12
                    ),
                    LyricStanza(
                        section = "समर्पण (Outro)",
                        lines = listOf("बस तेरा ही नाम मेरे दिल विच रह गया", "सच्ची मुच्ची यार साड्डा दिल तू ही ले गया"),
                        singerVoice = voiceType,
                        timestampSeconds = 24
                    )
                )
            }

            language == SongLanguage.BHOJPURI -> {
                title = if (isParty) "कमरिया करे लपालप" else "तोहरा नैना के बाण"
                stanzas = listOf(
                    LyricStanza(
                        section = "मुखड़ा (Mukhda)",
                        lines = listOf("तोहरे नैना से जबसे लड़ल बा नजरिया", "मनवा में बाजेला मधुर बाँसुरिया"),
                        singerVoice = if (voiceType == VoiceType.DUET) VoiceType.MALE else voiceType,
                        timestampSeconds = 0
                    ),
                    LyricStanza(
                        section = "अंतरा (Antara)",
                        lines = listOf("ढोलक के थाप प सजनवा मुस्काये", "पूरब के पुरवइया में खुशबू समाये"),
                        singerVoice = if (voiceType == VoiceType.DUET) VoiceType.FEMALE else voiceType,
                        timestampSeconds = 12
                    ),
                    LyricStanza(
                        section = "समर्पण (Outro)",
                        lines = listOf("सदा रहब संग तोहरे जिया के करार", "तोहरे से बा हमरा सच्चा दुलार"),
                        singerVoice = voiceType,
                        timestampSeconds = 24
                    )
                )
            }

            language == SongLanguage.ENGLISH -> {
                title = if (isParty) "Midnight Neon Pulse" else "Echoes of Your Love"
                stanzas = listOf(
                    LyricStanza(
                        section = "Chorus",
                        lines = listOf("Whispers in the moonlight, calling out your name", "Every gentle heartbeat dancing in the flame"),
                        singerVoice = if (voiceType == VoiceType.DUET) VoiceType.MALE else voiceType,
                        timestampSeconds = 0
                    ),
                    LyricStanza(
                        section = "Verse",
                        lines = listOf("Lost inside the melody, flowing like a stream", "Holding onto memories of a golden dream"),
                        singerVoice = if (voiceType == VoiceType.DUET) VoiceType.FEMALE else voiceType,
                        timestampSeconds = 12
                    ),
                    LyricStanza(
                        section = "Outro",
                        lines = listOf("Through the night we wander, singing sweet and free", "Forever intertwined in our harmony"),
                        singerVoice = voiceType,
                        timestampSeconds = 24
                    )
                )
            }

            isBhajan -> {
                title = "श्री कृष्ण की मुरली धुन"
                stanzas = listOf(
                    LyricStanza(
                        section = "मुखड़ा (Mukhda)",
                        lines = listOf("अधर धरत मुरली बाजे रे", "मनमोहन गिरधारी साजे रे"),
                        singerVoice = if (voiceType == VoiceType.DUET) VoiceType.MALE else voiceType,
                        timestampSeconds = 0
                    ),
                    LyricStanza(
                        section = "अंतरा (Antara)",
                        lines = listOf("यमुना तट पर धेनु चरावे", "मधुर मधुर सुर मन हरषावे"),
                        singerVoice = if (voiceType == VoiceType.DUET) VoiceType.FEMALE else voiceType,
                        timestampSeconds = 12
                    ),
                    LyricStanza(
                        section = "समर्पण (Outro)",
                        lines = listOf("राधे कृष्ण की जय जय गाओ", "भव सागर से मुक्ति पाओ"),
                        singerVoice = voiceType,
                        timestampSeconds = 24
                    )
                )
            }

            isRain || isRomantic -> {
                title = if (isRain) "बरसात की भीगी रातें" else "तेरे इश्क का अफसाना"
                stanzas = listOf(
                    LyricStanza(
                        section = "मुखड़ा (Mukhda)",
                        lines = listOf("रिमझिम बरसे सावन की बूँदें, यादें तेरी जगाए", "भीगी भीगी इन रातों में दिल तुझको ही बुलाए"),
                        singerVoice = if (voiceType == VoiceType.DUET) VoiceType.MALE else voiceType,
                        timestampSeconds = 0
                    ),
                    LyricStanza(
                        section = "अंतरा (Antara)",
                        lines = listOf("हवाएं छूके जब भी गुजरती हैं इस दिल को", "तेरी खुशबू का अहसास करा जाती हैं हर पल को"),
                        singerVoice = if (voiceType == VoiceType.DUET) VoiceType.FEMALE else voiceType,
                        timestampSeconds = 12
                    ),
                    LyricStanza(
                        section = "समर्पण (Outro)",
                        lines = listOf("तू ही मेरी मंजिल है, तू ही मेरी सुबह और शाम", "हर सांस पे लिखा है बस तेरा ही मधुर नाम"),
                        singerVoice = voiceType,
                        timestampSeconds = 24
                    )
                )
            }

            isSufi -> {
                title = "रूहानी सफर की दास्तान"
                stanzas = listOf(
                    LyricStanza(
                        section = "मुखड़ा (Mukhda)",
                        lines = listOf("रूह से रूह का यह रिश्ता कैसा", "इश्क का दरिया बहता बहता जैसा"),
                        singerVoice = if (voiceType == VoiceType.DUET) VoiceType.MALE else voiceType,
                        timestampSeconds = 0
                    ),
                    LyricStanza(
                        section = "अंतरा (Antara)",
                        lines = listOf("खामोशियों में भी तेरी सदा है", "तू ही मेरी हर दर्द की दवा है"),
                        singerVoice = if (voiceType == VoiceType.DUET) VoiceType.FEMALE else voiceType,
                        timestampSeconds = 12
                    ),
                    LyricStanza(
                        section = "समर्पण (Outro)",
                        lines = listOf("सजदा करूं मैं तेरी रूहानियत को", "तूने संवारा मेरी ज़िंदगानी को"),
                        singerVoice = voiceType,
                        timestampSeconds = 24
                    )
                )
            }

            else -> {
                title = "मधुर संगीत की तरंग"
                stanzas = listOf(
                    LyricStanza(
                        section = "मुखड़ा (Mukhda)",
                        lines = listOf("गूंजे जब ये मधुर सुर सरगम", "खिले हर दिल में खुशियों का मौसम"),
                        singerVoice = if (voiceType == VoiceType.DUET) VoiceType.MALE else voiceType,
                        timestampSeconds = 0
                    ),
                    LyricStanza(
                        section = "अंतरा (Antara)",
                        lines = listOf("सितार की झंकार में डूबा जहां सारा", "सुरमयी शाम का हर एक नजारा"),
                        singerVoice = if (voiceType == VoiceType.DUET) VoiceType.FEMALE else voiceType,
                        timestampSeconds = 12
                    ),
                    LyricStanza(
                        section = "समर्पण (Outro)",
                        lines = listOf("संगीत ही जीवन का सच्चा सहारा है", "धुन में बसा यह ब्रह्मांड सारा है"),
                        singerVoice = voiceType,
                        timestampSeconds = 24
                    )
                )
            }
        }

        return Pair(title, stanzas)
    }
}
