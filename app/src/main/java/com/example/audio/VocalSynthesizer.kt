package com.example.audio

import android.content.Context
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import com.example.model.SongLanguage
import com.example.model.VoiceType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.File
import java.util.Locale
import java.util.UUID
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class VocalSynthesizer(private val context: Context) : TextToSpeech.OnInitListener {

    private var tts: TextToSpeech? = null
    private val _isInitialized = MutableStateFlow(false)
    val isInitialized: StateFlow<Boolean> = _isInitialized.asStateFlow()

    private var activeUtteranceCallback: ((String, Boolean) -> Unit)? = null

    init {
        tts = TextToSpeech(context.applicationContext, this)
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = tts?.setLanguage(Locale("hi", "IN"))
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                // Fallback to English or default locale
                tts?.setLanguage(Locale("en", "IN"))
            }
            tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                override fun onStart(utteranceId: String?) {
                    activeUtteranceCallback?.invoke(utteranceId ?: "", false)
                }

                override fun onDone(utteranceId: String?) {
                    activeUtteranceCallback?.invoke(utteranceId ?: "", true)
                }

                override fun onError(utteranceId: String?) {
                    activeUtteranceCallback?.invoke(utteranceId ?: "", true)
                }
            })
            _isInitialized.value = true
            Log.d("VocalSynthesizer", "TTS initialized successfully")
        } else {
            Log.e("VocalSynthesizer", "TTS Init failed with status: $status")
        }
    }

    fun applyVoiceConfig(voiceType: VoiceType, language: SongLanguage, customPitch: Float? = null) {
        val targetLocale = when (language) {
            SongLanguage.HINDI, SongLanguage.BHOJPURI -> Locale("hi", "IN")
            SongLanguage.PUNJABI -> Locale("pa", "IN")
            SongLanguage.BENGALI -> Locale("bn", "IN")
            SongLanguage.TAMIL -> Locale("ta", "IN")
            SongLanguage.ENGLISH -> Locale("en", "IN")
            SongLanguage.HARYANVI -> Locale("hi", "IN")
        }

        try {
            val res = tts?.setLanguage(targetLocale)
            if (res == TextToSpeech.LANG_MISSING_DATA || res == TextToSpeech.LANG_NOT_SUPPORTED) {
                tts?.language = Locale("hi", "IN")
            }
        } catch (e: Exception) {
            tts?.language = Locale.getDefault()
        }

        val pitch = customPitch ?: when (voiceType) {
            VoiceType.MALE -> 0.82f
            VoiceType.FEMALE -> 1.35f
            VoiceType.DUET -> 1.05f
            VoiceType.CLONED -> 1.0f
        }

        tts?.setPitch(pitch)
        tts?.setSpeechRate(voiceType.speed)
    }

    fun speakLine(
        text: String,
        voiceType: VoiceType,
        language: SongLanguage,
        utteranceId: String = UUID.randomUUID().toString(),
        onComplete: (() -> Unit)? = null
    ) {
        if (!_isInitialized.value) return
        applyVoiceConfig(voiceType, language)
        activeUtteranceCallback = { id, done ->
            if (id == utteranceId && done) {
                onComplete?.invoke()
            }
        }
        val params = Bundle()
        params.putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, utteranceId)
        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, params, utteranceId)
    }

    suspend fun synthesizeTextToFile(
        text: String,
        outputFile: File,
        voiceType: VoiceType,
        language: SongLanguage,
        customPitch: Float? = null
    ): Boolean = suspendCancellableCoroutine { continuation ->
        if (!_isInitialized.value) {
            continuation.resume(false)
            return@suspendCancellableCoroutine
        }

        applyVoiceConfig(voiceType, language, customPitch)
        val utteranceId = "synth_${UUID.randomUUID()}"
        val params = Bundle()
        params.putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, utteranceId)

        activeUtteranceCallback = { id, done ->
            if (id == utteranceId) {
                if (continuation.isActive) {
                    continuation.resume(done && outputFile.exists() && outputFile.length() > 0)
                }
            }
        }

        try {
            val status = tts?.synthesizeToFile(text, params, outputFile, utteranceId)
            if (status != TextToSpeech.SUCCESS) {
                if (continuation.isActive) continuation.resume(false)
            }
        } catch (e: Exception) {
            Log.e("VocalSynthesizer", "SynthesizeToFile failed", e)
            if (continuation.isActive) continuation.resume(false)
        }
    }

    fun stop() {
        tts?.stop()
    }

    fun release() {
        tts?.stop()
        tts?.shutdown()
    }
}
