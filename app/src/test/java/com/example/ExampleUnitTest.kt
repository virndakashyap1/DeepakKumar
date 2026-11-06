package com.example

import com.example.audio.AudioSynthEngine
import com.example.audio.WavFileHelper
import com.example.model.MusicGenre
import com.example.model.SongLanguage
import com.example.model.VoiceType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class ExampleUnitTest {

    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun `test audio synthesis produces correct sample count`() {
        val samples = AudioSynthEngine.synthesizeInstrumental(
            genre = MusicGenre.ROMANTIC,
            durationSeconds = 2,
            instruments = listOf("बांसुरी", "हारमोनियम", "तबला")
        )
        assertNotNull(samples)
        assertEquals(AudioSynthEngine.SAMPLE_RATE * 2, samples.size)
        // Verify audio contains non-zero signal
        val hasAudio = samples.any { it != 0.toShort() }
        assertTrue(hasAudio)
    }

    @Test
    fun `test wav file writing and reading`() {
        val tempFile = File.createTempFile("test_audio", ".wav")
        tempFile.deleteOnExit()

        val sampleData = shortArrayOf(0, 1000, 2000, 1000, 0, -1000, -2000, -1000)
        WavFileHelper.writeWavFile(tempFile, sampleData, 22050, 1)

        assertTrue(tempFile.exists())
        assertTrue(tempFile.length() > 44) // WAV header is 44 bytes

        val readPcm = WavFileHelper.readPcmFromWav(tempFile)
        assertNotNull(readPcm)
        assertEquals(sampleData.size, readPcm!!.size)
    }

    @Test
    fun `test voice and genre enum properties`() {
        assertEquals("AI Male", VoiceType.MALE.displayName)
        assertEquals("मधुर पुरुष", VoiceType.MALE.hindiName)
        assertEquals(82, MusicGenre.ROMANTIC.bpm)
        assertEquals("Hindi", SongLanguage.HINDI.label)
    }
}
