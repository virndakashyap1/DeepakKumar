package com.example.audio

import com.example.model.MusicGenre
import java.io.File
import kotlin.math.PI
import kotlin.math.exp
import kotlin.math.sin

object AudioSynthEngine {

    const val SAMPLE_RATE = 22050

    // Indian Solfege Frequencies (C4 = 261.63 Hz)
    private val SA = 261.63
    private val RE_KOMAL = 277.18
    private val RE = 293.66
    private val GA_KOMAL = 311.13
    private val GA = 329.63
    private val MA = 349.23
    private val MA_TEEVA = 369.99
    private val PA = 392.00
    private val DHA_KOMAL = 415.30
    private val DHA = 440.00
    private val NI_KOMAL = 466.16
    private val NI = 493.88
    private val SA_HIGH = 523.25

    /**
     * Synthesizes a full musical instrumental backing track according to the chosen genre and duration.
     */
    fun synthesizeInstrumental(
        genre: MusicGenre,
        durationSeconds: Int,
        instruments: List<String>
    ): ShortArray {
        val totalSamples = SAMPLE_RATE * durationSeconds
        val output = ShortArray(totalSamples)

        // Scale notes selection
        val scaleNotes = when (genre) {
            MusicGenre.ROMANTIC -> listOf(SA, RE_KOMAL, GA_KOMAL, MA, PA, DHA_KOMAL, NI_KOMAL, SA_HIGH) // Bhairavi
            MusicGenre.SUFI -> listOf(SA, RE, GA, MA_TEEVA, PA, DHA, NI, SA_HIGH) // Yaman
            MusicGenre.BHAJAN -> listOf(SA, RE, GA, MA, PA, DHA, NI, SA_HIGH) // Bilawal / Shankara
            MusicGenre.GHAZAL -> listOf(SA, RE_KOMAL, GA_KOMAL, MA, PA, DHA_KOMAL, NI_KOMAL, SA) // Darbari
            MusicGenre.FOLK_DESI -> listOf(SA, RE, GA, MA, PA, DHA, NI_KOMAL, SA_HIGH) // Khamaj
            MusicGenre.POP_DANCE -> listOf(SA, RE, GA_KOMAL, MA, PA, DHA_KOMAL, NI_KOMAL, SA_HIGH) // Minor
        }

        val beatDurationSec = 60.0 / genre.bpm
        val samplesPerBeat = (SAMPLE_RATE * beatDurationSec).toInt()

        val hasHarmonium = instruments.any { it.contains("Harmonium", ignoreCase = true) || it.contains("हारमोनियम", ignoreCase = true) }
        val hasFlute = instruments.any { it.contains("Flute", ignoreCase = true) || it.contains("बांसुरी", ignoreCase = true) }
        val hasSitar = instruments.any { it.contains("Sitar", ignoreCase = true) || it.contains("सितार", ignoreCase = true) }
        val hasTabla = instruments.any { it.contains("Tabla", ignoreCase = true) || it.contains("ढोलक", ignoreCase = true) || it.contains("Beats", ignoreCase = true) }
        val hasGuitar = instruments.any { it.contains("Guitar", ignoreCase = true) || it.contains("गिटार", ignoreCase = true) }

        // Generate audio frame by frame
        for (i in 0 until totalSamples) {
            val t = i.toDouble() / SAMPLE_RATE
            var sampleAcc = 0.0

            // 1. Tanpura / Drone layer (Constant Indian drone background)
            val droneSa = sin(2.0 * PI * (SA / 2.0) * t) * 0.12
            val dronePa = sin(2.0 * PI * (PA / 2.0) * t) * 0.08
            val droneOvertone = sin(2.0 * PI * SA * t) * 0.05
            sampleAcc += (droneSa + dronePa + droneOvertone)

            // 2. Chord / Arpeggio progression based on beats
            val currentBeat = (t / beatDurationSec).toInt()
            val noteIndex = (currentBeat % scaleNotes.size)
            val baseFreq = scaleNotes[noteIndex]

            // 3. Harmonium Reed / Pad sound (rich harmonics with subtle chorus)
            if (hasHarmonium || (!hasSitar && !hasFlute && !hasGuitar)) {
                val reed1 = sin(2.0 * PI * baseFreq * t)
                val reed2 = sin(2.0 * PI * (baseFreq * 2.0) * t) * 0.5
                val reed3 = sin(2.0 * PI * (baseFreq * 3.0) * t) * 0.25
                val tremolo = 0.85 + 0.15 * sin(2.0 * PI * 5.0 * t) // Harmonium bellow pulse
                sampleAcc += (reed1 + reed2 + reed3) * 0.18 * tremolo
            }

            // 4. Flute / बांसुरी (sweet breathy sine with gentle vibrato)
            if (hasFlute) {
                val vibrato = sin(2.0 * PI * 4.5 * t) * 3.0
                val fluteFreq = baseFreq * 2.0 + vibrato
                val breath = (sin(2.0 * PI * 3800.0 * t) * 0.02)
                val fluteLead = sin(2.0 * PI * fluteFreq * t) * 0.22 + breath
                sampleAcc += fluteLead
            }

            // 5. Sitar / सितार (bright plucks with resonant sympathetic strings)
            if (hasSitar) {
                val notePhase = (i % samplesPerBeat).toDouble() / samplesPerBeat
                val pluckEnvelope = exp(-notePhase * 4.0) // sharp attack, exponential decay
                val sitarFreq = baseFreq * 1.5
                val sitarPluck = (sin(2.0 * PI * sitarFreq * t) +
                        0.5 * sin(2.0 * PI * (sitarFreq * 2.0) * t) +
                        0.3 * sin(2.0 * PI * (sitarFreq * 3.0) * t)) * pluckEnvelope * 0.25
                sampleAcc += sitarPluck
            }

            // 6. Acoustic Guitar (Strummed arpeggio)
            if (hasGuitar) {
                val subBeat = (i % (samplesPerBeat / 2)).toDouble() / (samplesPerBeat / 2)
                val guitarEnv = exp(-subBeat * 5.0)
                val gFreq = baseFreq * (if ((i / (samplesPerBeat / 2)) % 2 == 0) 1.0 else 1.25)
                val guitarStrum = (sin(2.0 * PI * gFreq * t) + 0.4 * sin(2.0 * PI * gFreq * 2 * t)) * guitarEnv * 0.20
                sampleAcc += guitarStrum
            }

            // 7. Tabla / Percussion Beats (Dha, Dhin, Tin, Ta synthesis)
            if (hasTabla || genre == MusicGenre.POP_DANCE || genre == MusicGenre.FOLK_DESI) {
                val beatPhase = (i % samplesPerBeat).toDouble() / samplesPerBeat
                val isDownbeat = (currentBeat % 4 == 0)
                val isOffbeat = (currentBeat % 2 == 1)

                // Bass Bayan / Dholak base (Dha / Ghe thump)
                if (beatPhase < 0.25 && (isDownbeat || isOffbeat)) {
                    val bassEnv = exp(-beatPhase * 16.0)
                    val bassPitchDrop = 90.0 - (beatPhase * 40.0)
                    val bayanThump = sin(2.0 * PI * bassPitchDrop * t) * bassEnv * 0.35
                    sampleAcc += bayanThump
                }

                // Dayan / Chatti (High resonant "Tin" / "Na" stroke)
                val subBeatPhase = (i % (samplesPerBeat / 2)).toDouble() / (samplesPerBeat / 2)
                if (subBeatPhase < 0.15) {
                    val rimEnv = exp(-subBeatPhase * 25.0)
                    val dayanRing = sin(2.0 * PI * 440.0 * t) * rimEnv * 0.22
                    sampleAcc += dayanRing
                }
            }

            // Master Volume normalization & soft clipping
            val scaled = (sampleAcc * 18000.0).coerceIn(-32700.0, 32700.0).toInt().toShort()
            output[i] = scaled
        }

        return output
    }

    /**
     * Synthesizes and writes the instrumental audio file to destination.
     */
    fun generateInstrumentalFile(
        outputFile: File,
        genre: MusicGenre,
        durationSeconds: Int,
        instruments: List<String>
    ): File {
        val pcm = synthesizeInstrumental(genre, durationSeconds, instruments)
        WavFileHelper.writeWavFile(outputFile, pcm, SAMPLE_RATE, 1)
        return outputFile
    }
}
