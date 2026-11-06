package com.example.audio

import java.io.File
import java.io.FileOutputStream
import java.io.RandomAccessFile
import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.math.max
import kotlin.math.min

object WavFileHelper {

    fun writeWavFile(
        outputFile: File,
        pcmData: ShortArray,
        sampleRate: Int = 22050,
        channels: Int = 1
    ) {
        val totalAudioLen = pcmData.size * 2L
        val totalDataLen = totalAudioLen + 36
        val byteRate = sampleRate * channels * 2

        val header = ByteArray(44)
        // RIFF chunk
        header[0] = 'R'.code.toByte()
        header[1] = 'I'.code.toByte()
        header[2] = 'F'.code.toByte()
        header[3] = 'F'.code.toByte()
        header[4] = (totalDataLen and 0xff).toByte()
        header[5] = ((totalDataLen shr 8) and 0xff).toByte()
        header[6] = ((totalDataLen shr 16) and 0xff).toByte()
        header[7] = ((totalDataLen shr 24) and 0xff).toByte()
        // WAVE
        header[8] = 'W'.code.toByte()
        header[9] = 'A'.code.toByte()
        header[10] = 'V'.code.toByte()
        header[11] = 'E'.code.toByte()
        // 'fmt ' chunk
        header[12] = 'f'.code.toByte()
        header[13] = 'm'.code.toByte()
        header[14] = 't'.code.toByte()
        header[15] = ' '.code.toByte()
        header[16] = 16 // 4 bytes: size of 'fmt ' chunk
        header[17] = 0
        header[18] = 0
        header[19] = 0
        header[20] = 1 // format = 1 (PCM)
        header[21] = 0
        header[22] = channels.toByte()
        header[23] = 0
        header[24] = (sampleRate and 0xff).toByte()
        header[25] = ((sampleRate shr 8) and 0xff).toByte()
        header[26] = ((sampleRate shr 16) and 0xff).toByte()
        header[27] = ((sampleRate shr 24) and 0xff).toByte()
        header[28] = (byteRate and 0xff).toByte()
        header[29] = ((byteRate shr 8) and 0xff).toByte()
        header[30] = ((byteRate shr 16) and 0xff).toByte()
        header[31] = ((byteRate shr 24) and 0xff).toByte()
        header[32] = (channels * 2).toByte() // block align
        header[33] = 0
        header[34] = 16 // bits per sample
        header[35] = 0
        // 'data' chunk
        header[36] = 'd'.code.toByte()
        header[37] = 'a'.code.toByte()
        header[38] = 't'.code.toByte()
        header[39] = 'a'.code.toByte()
        header[40] = (totalAudioLen and 0xff).toByte()
        header[41] = ((totalAudioLen shr 8) and 0xff).toByte()
        header[42] = ((totalAudioLen shr 16) and 0xff).toByte()
        header[43] = ((totalAudioLen shr 24) and 0xff).toByte()

        val byteBuffer = ByteBuffer.allocate(pcmData.size * 2).order(ByteOrder.LITTLE_ENDIAN)
        for (sample in pcmData) {
            byteBuffer.putShort(sample)
        }

        FileOutputStream(outputFile).use { fos ->
            fos.write(header)
            fos.write(byteBuffer.array())
        }
    }

    fun mixPcmTracks(track1: ShortArray, track2: ShortArray, weight1: Float = 0.6f, weight2: Float = 0.5f): ShortArray {
        val maxLen = max(track1.size, track2.size)
        val mixed = ShortArray(maxLen)
        for (i in 0 until maxLen) {
            val s1 = if (i < track1.size) track1[i] * weight1 else 0f
            val s2 = if (i < track2.size) track2[i] * weight2 else 0f
            val sum = s1 + s2
            val clamped = min(32767f, max(-32768f, sum)).toInt().toShort()
            mixed[i] = clamped
        }
        return mixed
    }

    fun readPcmFromWav(wavFile: File): ShortArray? {
        if (!wavFile.exists() || wavFile.length() < 44) return null
        return try {
            val bytes = wavFile.readBytes()
            val pcmBytes = bytes.copyOfRange(44, bytes.size)
            val shorts = ShortArray(pcmBytes.size / 2)
            ByteBuffer.wrap(pcmBytes).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().get(shorts)
            shorts
        } catch (e: Exception) {
            null
        }
    }
}
