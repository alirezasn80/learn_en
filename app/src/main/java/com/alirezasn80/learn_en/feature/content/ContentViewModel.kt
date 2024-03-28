package com.alirezasn80.learn_en.feature.content

import android.app.Application
import android.content.Intent
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.widget.Toast
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.alirezasn80.learn_en.core.data.database.AppDB
import com.alirezasn80.learn_en.utill.Arg
import com.alirezasn80.learn_en.utill.BaseViewModel
import com.alirezasn80.learn_en.utill.debug
import com.alirezasn80.learn_en.utill.getString
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.Translator
import com.google.mlkit.nl.translate.TranslatorOptions
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class ContentViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val database: AppDB,
    private val application: Application
) : BaseViewModel<ContentState>(ContentState()) {
    private val categoryId = savedStateHandle.getString(Arg.CATEGORY_ID)!!.toInt()
    private val contentId = savedStateHandle.getString(Arg.CONTENT_ID)!!.toInt()
    var readJob: Job? = null

    val translator: Translator by lazy {
        val options = TranslatorOptions.Builder()
            .setSourceLanguage(TranslateLanguage.ENGLISH)
            .setTargetLanguage(TranslateLanguage.PERSIAN)
            .build()
        Translation.getClient(options)
    }

    private val textToSpeech: TextToSpeech = TextToSpeech(application) { status ->
        if (status == TextToSpeech.SUCCESS) initLanguageAndSpeed()
    }

    private fun initLanguageAndSpeed() {
        textToSpeech.language = Locale.US
        textToSpeech.setPitch(1f) // Higher values will increase the pitch
        textToSpeech.setSpeechRate(1f) // Higher values will increase the speed rate
    }

    private fun initSpeechToText() {
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(application)
        speechIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        speechIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        speechIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "fa-IR")
    }

    private lateinit var speechRecognizer: SpeechRecognizer
    private lateinit var speechIntent: Intent

    init {
        checkDownloadTranslate()
        initSpeechToText()
        getContent()
    }

    private fun getContent() {
        viewModelScope.launch {
            val contentEntity = database.contentDao.getContent(categoryId, contentId)
            val title = contentEntity.title
            val contents = contentEntity.content.trim().split(".").filter { it.isNotBlank() }
            val paragraphs = mutableListOf<Paragraph>()

            contents.forEachIndexed { index, paragraph ->

                translator.translate(paragraph)
                    .addOnSuccessListener { translated ->
                        paragraphs.add(Paragraph(paragraph, translated))
                        debug("add")
                    }
                    .addOnFailureListener { exception ->
                        debug("failed translate" + exception.message)
                    }
                    .addOnCompleteListener {
                        if (index + 1 == contents.size)
                            state.update { it.copy(title = title, paragraphs = paragraphs) }
                    }
            }
        }
    }

    private fun checkDownloadTranslate() {
        // val conditions = DownloadConditions.Builder().requireWifi().build()

        translator.downloadModelIfNeeded()
            .addOnSuccessListener {
                debug("success can translate")
            }
            .addOnFailureListener { exception ->
                debug("failed can translate${exception.message}")
            }
    }

    fun onTranslateClick() {
        state.update { it.copy(isVisibleTranslate = !state.value.isVisibleTranslate) }
    }

    private fun speakText(value: String) {
        textToSpeech.speak(value, TextToSpeech.QUEUE_FLUSH, null, "")
    }

    fun onWordClick(word: String) {
        translator.translate(word)
            .addOnSuccessListener {
                speakText(word)
                Toast.makeText(application, it, Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { exception ->
                debug("failed translate" + exception.message)
            }
    }

    fun onSpeedClick(speed: Float) {
        textToSpeech.setSpeechRate(speed)
    }

    fun onPlayClick(isPlay: Boolean) {
        if (isPlay) {
            readJob = viewModelScope.launch {
                textToSpeech.speak(state.value.paragraphs[0].text, TextToSpeech.QUEUE_FLUSH, null, "")

            }
        } else {
            readJob?.cancel()
        }
    }

    override fun onCleared() {
        debug("cleared")
        super.onCleared()
        textToSpeech.stop()
        textToSpeech.shutdown()

    }
}