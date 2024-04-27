package com.alirezasn80.learn_en.feature.content

import android.app.Application
import android.content.Intent
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.alirezasn80.learn_en.R
import com.alirezasn80.learn_en.core.data.database.AppDB
import com.alirezasn80.learn_en.core.domain.entity.ContentEntity
import com.alirezasn80.learn_en.core.domain.entity.WordEntity
import com.alirezasn80.learn_en.core.domain.local.Define
import com.alirezasn80.learn_en.core.domain.local.SheetModel
import com.alirezasn80.learn_en.core.domain.local.Translation
import com.alirezasn80.learn_en.feature.home.TranslationConnection
import com.alirezasn80.learn_en.utill.Arg
import com.alirezasn80.learn_en.utill.BaseViewModel
import com.alirezasn80.learn_en.utill.Progress
import com.alirezasn80.learn_en.utill.Reload
import com.alirezasn80.learn_en.utill.debug
import com.alirezasn80.learn_en.utill.getString
import com.alirezasn80.learn_en.utill.isOnline
import com.alirezasn80.learn_en.utill.removeBlankLines
import com.alirezasn80.learn_en.utill.showToast
import com.alirezasn80.learn_en.utill.toBoolean
import com.alirezasn80.learn_en.utill.toLogicInt
import com.alirezasn80.learn_en.utill.toStringList
import dagger.hilt.android.lifecycle.HiltViewModel
import io.appmetrica.analytics.AppMetrica
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.json.JSONArray
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
    val isTrial = savedStateHandle.getString(Arg.IS_TRIAL)!! == "trial"
    private var maxReadableIndex = 0
    private var readMode = "default"
    private var title = ""

    private val tts: TextToSpeech = TextToSpeech(application) { status ->
        if (status == TextToSpeech.SUCCESS) ttsSetting()
    }

    init {
        initSpeechToText()
        getContent()
        getHighlights()
    }

    private fun getHighlights() {

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val highlights = database.wordDao.getHighlights()
                state.update { it.copy(highlights = highlights) }
            } catch (e: Exception) {
                errorException("Error in get highlights", e)
            }
        }

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

    private fun loading(value: Progress, key: String = "") = viewModelScope.launch(Dispatchers.Main) { progress[key] = value }

    private fun errorException(
        metricaMsg: String,
        e: Exception?,
        userMsg: Int = R.string.unknown_error,
        key: String = ""
    ) {
        AppMetrica.reportError(metricaMsg, e)
        setMessageByToast(userMsg)
        loading(Progress.Idle, key)
    }


    fun getContent() = viewModelScope.launch(Dispatchers.IO) {

        if (!isOnline(application)) {
            return@launch
        }


        debug("get content")
        loading(Progress.Loading)

        val contentEntity = try {
            database.contentDao.getContent(categoryId, contentId)
        } catch (e: Exception) {
            errorException("Error in get content entity", e)
            return@launch
        }

        state.update { it.copy(isBookmark = contentEntity.favorite.toBoolean()) }
        title = contentEntity.title

        val paragraphs = if (contentEntity.translation.isNullOrBlank()) {// remote
            debug("from remote")
            translateRemote(contentEntity)
        } else { // locale
            debug("from locale")
            processTranslateJson(contentEntity.translation)
        }
        debug("done")


        state.update {
            it.copy(
                isBookmark = contentEntity.favorite.toBoolean(),
                title = contentEntity.title,
                paragraphs = paragraphs
            )
        }

        loading(Progress.Idle)


    }

    private suspend fun translateRemote(
        contentEntity: ContentEntity,
        from: String = "en",
        to: String = "fa"
    ): List<Paragraph> {
        //todo(check max 5,000 char)
        var newContent = contentEntity.content.trimIndent().removeBlankLines()
        if (newContent.startsWith(title)) newContent = newContent.replace(title, "$title.\n")

        return try {
            val translated = TranslationConnection.translateHttpURLConnection(
                newContent,
                to,
                from
            )
            val result = processTranslateJson(translated)
            database.contentDao.updateContent(contentEntity.copy(translation = translated))
            result

        } catch (e: Exception) {
            errorException("Error in Translation", e)
            emptyList()
        }


    }

    private fun processTranslateJson(value: String): List<Paragraph> {
        return try {
            val allText = JSONArray(value).getJSONArray(0)
            val paragraphs = mutableListOf<Paragraph>()

            maxReadableIndex = allText.length() - 1


            for (i in 0 until allText.length()) {
                val json = allText.getJSONArray(i)
                val translate = json.get(0).toString().trim()
                val main = json.get(1).toString().trim()

                paragraphs.add(Paragraph(main, translate))
            }
            paragraphs
        } catch (e: Exception) {
            errorException("Error in Json translate process", e)
            emptyList()
        }

    }


    fun onTranslateClick() {
        state.update { it.copy(isVisibleTranslate = !state.value.isVisibleTranslate) }
    }

    fun onMuteClick() {
        state.update { it.copy(isMute = !state.value.isMute) }
    }

    fun speakText(value: String) {
        tts.speak(value, TextToSpeech.QUEUE_FLUSH, null, "word")
    }

    fun onWordClick(word: String) {

        viewModelScope.launch(Dispatchers.IO) {
            clearPrevSheet()
            loading(Progress.Loading, "sheet")

            try {
                val wordEntity = database.wordDao.getWordEntity(word)

                if (wordEntity == null) {
                    val translated = TranslationConnection.dictionaryHttpURLConnection(word)
                    if (translated.isNotEmpty()) {
                        database.wordDao.insertWord(WordEntity(word, translated, 0))
                        onWordClick(word)
                    }
                } else {
                    val sheetModel = createSheetModel(
                        mainWord = word,
                        isHighlight = wordEntity.isHighlight.toBoolean(),
                        jsonArray = JSONArray(wordEntity.definition)
                    )
                    state.update { it.copy(sheetModel = sheetModel) }
                    if (!state.value.isPlay && !state.value.isMute) speakText(word)
                }
            } catch (e: Exception) {
                debug("error : ${e.message}")
                errorException(
                    "Error in Word Click",
                    e,
                    key = "sheet"
                )
            }
        }
    }

    private fun createSheetModel(
        mainWord: String,
        isHighlight: Boolean,
        jsonArray: JSONArray
    ): SheetModel {
        val translationsModel = mutableListOf<Translation>()
        val definesModel = mutableListOf<Define>()
        val definition = jsonArray.getJSONArray(0).getJSONArray(0).getString(0)

        for (i in 0 until jsonArray.getJSONArray(1).length()) {
            val bdJsonArray = jsonArray.getJSONArray(1).getJSONArray(i)
            val type = bdJsonArray.getString(0)
            val totalSimilar = bdJsonArray.getJSONArray(2)

            for (j in 0 until totalSimilar.length()) {
                val similarTranslate = totalSimilar.getJSONArray(j).getString(0)
                val synonyms = totalSimilar.getJSONArray(j).getJSONArray(1)
                val define = Define(similarTranslate, synonyms.toStringList())
                definesModel.add(define)
            }

            val translation = Translation(type, definesModel)
            translationsModel.add(translation)
        }

        return SheetModel(
            mainWord = mainWord,
            define = definition,
            more = translationsModel,
            isHighlight = isHighlight

        )
    }

    private fun clearPrevSheet() {
        state.update { it.copy(sheetModel = null) }
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
        else {
            deleteFromBookmark()
            Reload.favorite = true
        }
        state.update { it.copy(isBookmark = isBookmark) }
    }

    private fun addToBookmark() {
        viewModelScope.launch(Dispatchers.IO) {
            database.contentDao.addToBookmark(contentId, categoryId)
        }
        application.showToast(R.string.add_to_bookmark)
    }

    private fun deleteFromBookmark() {
        viewModelScope.launch(Dispatchers.IO) {
            database.contentDao.deleteFromBookmark(contentId, categoryId)
        }
        application.showToast(R.string.delete_from_bookmark)
    }

    fun changeHighlightMode(word: String, isHighlight: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            database.wordDao.changeHighlightMode(isHighlight.toLogicInt(), word)
            state.update { it.copy(sheetModel = state.value.sheetModel?.copy(isHighlight = isHighlight)) }
            getHighlights()
        }
    }

}