package com.alirezasn80.learn_en.feature.content

import android.app.Application
import android.content.Intent
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.widget.Toast
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.alirezasn80.learn_en.R
import com.alirezasn80.learn_en.core.data.database.AppDB
import com.alirezasn80.learn_en.utill.Arg
import com.alirezasn80.learn_en.utill.BaseViewModel
import com.alirezasn80.learn_en.utill.Progress
import com.alirezasn80.learn_en.utill.debug
import com.alirezasn80.learn_en.utill.getString
import com.alirezasn80.learn_en.utill.showToast
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.Translator
import com.google.mlkit.nl.translate.TranslatorOptions
import dagger.hilt.android.lifecycle.HiltViewModel
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
    private lateinit var speechRecognizer: SpeechRecognizer
    private lateinit var speechIntent: Intent
    private val categoryId = savedStateHandle.getString(Arg.CATEGORY_ID)!!.toInt()
    private val contentId = savedStateHandle.getString(Arg.CONTENT_ID)!!.toInt()
    private val translator: Translator by lazy { initTranslator() }
    private var maxReadableIndex = 0
    private var readMode = "default"


    private val tts: TextToSpeech = TextToSpeech(application) { status ->
        if (status == TextToSpeech.SUCCESS) ttsSetting()
    }

    private fun ttsSetting() {
        tts.language = Locale.US
        tts.setPitch(1f)
        tts.setSpeechRate(1f)

        tts.setOnUtteranceProgressListener(object : UtteranceProgressListener() {

            override fun onStart(utteranceId: String?) {}

            override fun onDone(utteranceId: String?) {
                if (utteranceId == "text") {

                    when (readMode) {

                        "default" -> {
                            val nextIndex = state.value.readableIndex + 1
                            if (nextIndex <= maxReadableIndex) {
                                state.update { it.copy(readableIndex = nextIndex) }
                                readParagraph(true)
                            } else {
                                state.update { it.copy(readableIndex = 0, isPlay = false) }
                            }
                        }

                        "repeat" -> readParagraph(true)


                        "play_stop" -> readParagraph(false)

                    }

                }

            }

            override fun onError(utteranceId: String?) {}

        })
    }

    private fun initTranslator(): Translator {
        val options = TranslatorOptions.Builder()
            .setSourceLanguage(TranslateLanguage.ENGLISH)
            .setTargetLanguage(TranslateLanguage.PERSIAN)
            .build()
        return Translation.getClient(options)
    }

    private fun initSpeechToText() {
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(application)
        speechIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        speechIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        speechIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "fa-IR")
    }

    fun onForwardClick() {
        val nextIndex = state.value.readableIndex + 1
        if (nextIndex <= maxReadableIndex) {
            state.update { it.copy(readableIndex = nextIndex) }
            if (tts.isSpeaking) tts.stop()
            if (state.value.isPlay) readParagraph(true)
        }
    }

    fun onBackwardClick() {
        val previousIndex = state.value.readableIndex - 1
        if (previousIndex >= 0) {
            state.update { it.copy(readableIndex = previousIndex) }
            if (tts.isSpeaking) tts.stop()
            if (state.value.isPlay) readParagraph(true)
        }
    }


    init {
        checkDownloadTranslate()
        initSpeechToText()
        getContent()
    }

    private fun loadingStatus(value: Progress) {
        progress[""] = value
    }

    private fun getContent() {

        loadingStatus(Progress.Loading)

        viewModelScope.launch {
            val contentEntity = database.contentDao.getContent(categoryId, contentId)
            val title = contentEntity.title
            val contents = contentEntity.content.trim().split(".").filter { it.isNotBlank() }
            maxReadableIndex = contents.size - 1

            val paragraphs = mutableListOf<Paragraph>()

            contents.forEachIndexed { index, paragraph ->

                translator.translate(paragraph)
                    .addOnSuccessListener { translated ->
                        paragraphs.add(Paragraph(paragraph, translated))
                    }
                    .addOnFailureListener { exception ->
                        debug("failed translate" + exception.message)
                    }
                    .addOnCompleteListener {
                        if (index + 1 == contents.size) {
                            loadingStatus(Progress.Idle)
                            state.update { it.copy(title = title, paragraphs = paragraphs) }
                        }
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

    fun onMuteClick() {
        state.update { it.copy(isMute = !state.value.isMute) }
    }

    private fun speakText(value: String) {
        tts.speak(value, TextToSpeech.QUEUE_FLUSH, null, "word")
    }

    fun onWordClick(word: String) {
        translator.translate(word)
            .addOnSuccessListener {
                if (!state.value.isPlay && !state.value.isMute) speakText(word)
                Toast.makeText(application, it, Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { exception ->
                debug("failed translate" + exception.message)
            }
    }

    fun onSpeedClick(speed: Float) {
        tts.setSpeechRate(speed)
    }

    fun readParagraph(isPlay: Boolean) {
        state.update { it.copy(isPlay = isPlay) }
        if (isPlay) {
            val index = state.value.readableIndex
            tts.speak(state.value.paragraphs[index].text, TextToSpeech.QUEUE_FLUSH, null, "text")
        } else {
            if (tts.isSpeaking)
                tts.stop()
        }
    }

    override fun onCleared() {
        debug("cleared")
        super.onCleared()
        tts.stop()
        tts.shutdown()

    }

    fun onParagraphClick(index: Int) {
        state.update { it.copy(readableIndex = index) }
        if (tts.isSpeaking) tts.stop()
        if (state.value.isPlay) readParagraph(true)
    }

    fun onReadModeClick(mode: String) {
        readMode = mode
    }

    fun onBookmarkClick() {
        val isBookmark = !state.value.isBookmark
        if (isBookmark)
            addToBookmark()
        state.update { it.copy(isBookmark = isBookmark) }
    }

    private fun addToBookmark() {
        viewModelScope.launch {
            database.contentDao.addToBookmark(contentId, categoryId)
        }
        application.showToast(R.string.add_to_bookmark)
    }
}