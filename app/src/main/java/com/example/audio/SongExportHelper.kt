package com.example.audio

import android.content.ClipData
import android.content.ClipboardManager
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.core.content.FileProvider
import com.example.model.Song
import java.io.File
import java.io.FileInputStream

object SongExportHelper {

    fun downloadSong(context: Context, song: Song): String? {
        val srcPath = song.audioFilePath ?: return null
        val srcFile = File(srcPath)
        if (!srcFile.exists()) return null

        val fileName = "${song.title.replace(" ", "_")}_MadhurSangeet.wav"

        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val resolver = context.contentResolver
                val contentValues = ContentValues().apply {
                    put(MediaStore.Audio.Media.DISPLAY_NAME, fileName)
                    put(MediaStore.Audio.Media.MIME_TYPE, "audio/wav")
                    put(MediaStore.Audio.Media.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS + "/AI_Madhur_Sangeet")
                    put(MediaStore.Audio.Media.IS_PENDING, 1)
                }

                val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
                if (uri != null) {
                    resolver.openOutputStream(uri).use { output ->
                        FileInputStream(srcFile).use { input ->
                            input.copyTo(output!!)
                        }
                    }
                    contentValues.clear()
                    contentValues.put(MediaStore.Audio.Media.IS_PENDING, 0)
                    resolver.update(uri, contentValues, null, null)
                    Toast.makeText(context, "गाना डाउनलोड हो गया: $fileName", Toast.LENGTH_LONG).show()
                    fileName
                } else {
                    null
                }
            } else {
                @Suppress("DEPRECATION")
                val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                val destFile = File(downloadsDir, fileName)
                srcFile.copyTo(destFile, overwrite = true)
                Toast.makeText(context, "गाना डाउनलोड हो गया: ${destFile.absolutePath}", Toast.LENGTH_LONG).show()
                destFile.absolutePath
            }
        } catch (e: Exception) {
            Toast.makeText(context, "डाउनलोड त्रुटि: ${e.message}", Toast.LENGTH_SHORT).show()
            null
        }
    }

    fun shareSong(context: Context, song: Song) {
        val srcPath = song.audioFilePath ?: return
        val file = File(srcPath)
        if (!file.exists()) {
            Toast.makeText(context, "ऑडियो फाइल उपलब्ध नहीं है", Toast.LENGTH_SHORT).show()
            return
        }

        try {
            val contentUri: Uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )

            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "audio/wav"
                putExtra(Intent.EXTRA_STREAM, contentUri)
                putExtra(Intent.EXTRA_SUBJECT, song.title)
                putExtra(Intent.EXTRA_TEXT, "🎶 'AI MADHUR SANGEET' द्वारा निर्मित गीत: ${song.title}\n\nबोल:\n" +
                        song.lyrics.joinToString("\n\n") { stanza ->
                            "${stanza.section}:\n" + stanza.lines.joinToString("\n")
                        })
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            context.startActivity(Intent.createChooser(shareIntent, "गाना शेयर करें"))
        } catch (e: Exception) {
            Toast.makeText(context, "शेयर करने में त्रुटि: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    fun copyLyrics(context: Context, song: Song) {
        val lyricsText = "🎵 ${song.title} 🎵\nआवाज़: ${song.voiceType.hindiName} | राग/शैली: ${song.genre.hindiTitle}\n\n" +
                song.lyrics.joinToString("\n\n") { stanza ->
                    "${stanza.section} [${stanza.singerVoice.displayName}]:\n" + stanza.lines.joinToString("\n")
                }

        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("Song Lyrics", lyricsText)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(context, "गाने के बोल (Lyrics) कॉपी कर लिए गए!", Toast.LENGTH_SHORT).show()
    }
}
