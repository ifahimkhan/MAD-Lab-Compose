package com.fahim.mad_lab_compose.voice

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.util.Log
import androidx.core.content.ContextCompat
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.Locale

class VoiceHelper(private val context: Context) : RecognitionListener, TextToSpeech.OnInitListener {

    private var speechRecognizer: SpeechRecognizer? = null
    private var textToSpeech: TextToSpeech? = null
    private var isListening = false

    private val _recognitionState = MutableStateFlow<RecognitionState>(RecognitionState.Idle)
    val recognitionState: StateFlow<RecognitionState> = _recognitionState

    private val _ttsState = MutableStateFlow<TTSState>(TTSState.Idle)
    val ttsState: StateFlow<TTSState> = _ttsState

    init {
        initializeTextToSpeech()
    }

    private fun initializeTextToSpeech() {
        textToSpeech = TextToSpeech(context, this)
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = textToSpeech?.setLanguage(Locale.US)
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                _ttsState.value = TTSState.Error("Text-to-speech language not supported")
            } else {
                _ttsState.value = TTSState.Ready
            }
        } else {
            _ttsState.value = TTSState.Error("Text-to-speech initialization failed")
        }
    }

    private val mainHandler = android.os.Handler(android.os.Looper.getMainLooper())

    fun startListening() {
        mainHandler.post {
            if (isListening) {
                stopListening()
            }

            // Check for microphone permission
            if (ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.RECORD_AUDIO
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                _recognitionState.value = RecognitionState.Error("Microphone permission required. Please grant permission.")
                return@post
            }

            if (!SpeechRecognizer.isRecognitionAvailable(context)) {
                _recognitionState.value = RecognitionState.Error("Speech recognition service not available on this device/emulator.")
                return@post
            }

            try {
                speechRecognizer?.destroy()
                speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context).apply {
                    setRecognitionListener(this@VoiceHelper)
                }

                val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                    putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                    putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.US)
                    putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
                    putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1)
                }

                speechRecognizer?.startListening(intent)
                isListening = true
                _recognitionState.value = RecognitionState.Listening
            } catch (e: Exception) {
                _recognitionState.value = RecognitionState.Error("Failed to start speech recognition: ${e.message}")
                Log.e("VoiceHelper", "Speech recognition error", e)
            }
        }
    }

    fun stopListening() {
        mainHandler.post {
            try {
                speechRecognizer?.stopListening()
                speechRecognizer?.cancel()
                speechRecognizer?.destroy()
            } catch (e: Exception) {
                Log.e("VoiceHelper", "Error stopping speech recognizer", e)
            }
            speechRecognizer = null
            isListening = false
            _recognitionState.value = RecognitionState.Idle
        }
    }

    fun speak(text: String) {
        if (textToSpeech == null) {
            initializeTextToSpeech()
        }

        textToSpeech?.apply {
            stop()
            val result = speak(text, TextToSpeech.QUEUE_ADD, null, "tts_utterance")
            if (result == TextToSpeech.ERROR) {
                _ttsState.value = TTSState.Error("Failed to speak text")
            } else {
                _ttsState.value = TTSState.Speaking
            }
        }
    }

    fun stopSpeaking() {
        textToSpeech?.stop()
        _ttsState.value = TTSState.Idle
    }

    override fun onReadyForSpeech(params: Bundle?) {
        _recognitionState.value = RecognitionState.Listening
    }

    override fun onBeginningOfSpeech() {
        _recognitionState.value = RecognitionState.Listening
    }

    override fun onRmsChanged(rmsdB: Float) {
        // Can be used for visual feedback if needed
    }

    override fun onBufferReceived(buffer: ByteArray?) {
        // Not used
    }

    override fun onEndOfSpeech() {
        isListening = false
    }

    override fun onError(error: Int) {
        val errorMessage = when (error) {
            SpeechRecognizer.ERROR_AUDIO -> "Audio recording error. Check emulator microphone settings."
            SpeechRecognizer.ERROR_CLIENT -> "Speech recognition client error."
            SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "Microphone permission is required."
            SpeechRecognizer.ERROR_NETWORK -> "Network error during speech recognition."
            SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "Network timeout during speech recognition."
            SpeechRecognizer.ERROR_NO_MATCH -> "No speech detected. Please speak clearly into your mic."
            SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> "Voice recognizer is busy. Please try again."
            SpeechRecognizer.ERROR_SERVER -> "Speech recognition server error."
            SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "No speech input heard. Tap mic and speak."
            else -> "Speech recognition error ($error)"
        }
        _recognitionState.value = RecognitionState.Error(errorMessage)
        isListening = false
    }

    override fun onResults(results: Bundle?) {
        val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
        if (!matches.isNullOrEmpty()) {
            _recognitionState.value = RecognitionState.Success(matches[0])
        } else {
            _recognitionState.value = RecognitionState.Error("No speech results")
        }
        isListening = false
    }

    override fun onPartialResults(partialResults: Bundle?) {
        val matches = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
        if (!matches.isNullOrEmpty()) {
            _recognitionState.value = RecognitionState.Partial(matches[0])
        }
    }

    override fun onEvent(eventType: Int, params: Bundle?) {
        // Not used
    }

    fun release() {
        stopListening()
        stopSpeaking()
        textToSpeech?.shutdown()
        textToSpeech = null
    }
}

sealed class RecognitionState {
    object Idle : RecognitionState()
    object Listening : RecognitionState()
    data class Partial(val text: String) : RecognitionState()
    data class Success(val text: String) : RecognitionState()
    data class Error(val message: String) : RecognitionState()
}

sealed class TTSState {
    object Idle : TTSState()
    object Ready : TTSState()
    object Speaking : TTSState()
    data class Error(val message: String) : TTSState()
}