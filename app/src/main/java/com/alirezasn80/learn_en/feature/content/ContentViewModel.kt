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
import com.alirezasn80.learn_en.core.domain.local.Define
import com.alirezasn80.learn_en.core.domain.local.SheetModel
import com.alirezasn80.learn_en.core.domain.local.Translation
import com.alirezasn80.learn_en.feature.home.TranslationTasks
import com.alirezasn80.learn_en.utill.Arg
import com.alirezasn80.learn_en.utill.BaseViewModel
import com.alirezasn80.learn_en.utill.Progress
import com.alirezasn80.learn_en.utill.debug
import com.alirezasn80.learn_en.utill.getString
import com.alirezasn80.learn_en.utill.removeBlankLines
import com.alirezasn80.learn_en.utill.showToast
import com.alirezasn80.learn_en.utill.toStringList
import com.alirezasn80.learn_en.utill.withDuration
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.jsoup.Jsoup
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
    private var maxReadableIndex = 0
    private var readMode = "default"
    private var title = ""

    private val tts: TextToSpeech = TextToSpeech(application) { status ->
        if (status == TextToSpeech.SUCCESS) ttsSetting()
    }

    init {
        initSpeechToText()
        getContent()
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


    private fun loadingStatus(value: Progress, key: String = "") {
        viewModelScope.launch(Dispatchers.Main) {
            progress[key] = value
        }

    }

    private fun getContent() {

        loadingStatus(Progress.Loading)

        viewModelScope.launch(Dispatchers.IO) {
            val contentEntity = database.contentDao.getContent(categoryId, contentId)
            title = contentEntity.title
            val content = contentEntity.content.trimIndent().removeBlankLines()
            translate(content)
        }
    }

    fun cleanAndSeparateText(text: String): List<String> {
        // Replace any sequence of whitespace characters with a single space
        val cleanedText = text.replace("\\s+".toRegex(), " ")

        // Split the text into paragraphs using two or more newline characters
        return cleanedText.split("\\r?\\n{2,}".toRegex())
    }

    private fun createUrl(from: String, to: String, text: String): String {
        return "https://translate.google.com/m?hl=en" +
                "&sl=$from" +
                "&tl=$to" +
                "&ie=UTF-8&prev=_m" +
                "&q=$text"

    }


    private fun translate(text: String, from: String = "en", to: String = "fa") {
        loadingStatus(Progress.Loading)
        viewModelScope.launch(Dispatchers.IO) {
            //todo(check max 5,000 char)

            var newContent = text
            //.replace("\"", "").replace(".", "*").replace("!", "*")
            if (newContent.startsWith(title)) newContent = newContent.replace(title, "$title*")

            debug(newContent)//1111111111111111111111111111111111111111111111111111111

            val url = createUrl(from, to, newContent)

            try {
                val doc = Jsoup.connect(url).get()
                val element = doc.getElementsByClass("result-container")

                if (element.isNotEmpty()) {
                    val result = element[0].text()

                    //debug(result)//2222222222222222222222222222222222222222222222222222222

                    val translations = result.split("*").map { it.trim() }
                    val contents = newContent.split("*").map { it.trim() }


                    TranslationTasks(newContent) {
                        debug(JSONArray(it).toString(1))
                        debug("main : ${contents.size}, translate : ${it.split("*").map { it.trim() }.size}")
                    }

                    if (translations.size == contents.size) {
                        val paragraphs = mutableListOf<Paragraph>()
                        maxReadableIndex = contents.size - 1

                        translations.forEachIndexed { index, translated ->
                            val main = try {
                                contents[index]
                            } catch (e: Exception) {
                                ""
                            }

                            paragraphs.add(Paragraph(main, translated))
                        }
                        state.update { it.copy(paragraphs = paragraphs, title = title) }
                        loadingStatus(Progress.Idle)

                    } else {
                        debug("Index are different: maxChar(${newContent.length}) content(${contents.size}),translation(${translations.size})")
                        loadingStatus(Progress.Idle)
                    }

                } else {
                    debug("Empty Translation!")
                    loadingStatus(Progress.Idle)
                }

            } catch (e: Exception) {
                debug("Error in Translation : ${e.message}")
                loadingStatus(Progress.Idle)
            }

        }
    }


    fun translate2() {
        val fromLanguage = "en"
        val toLanguage = "fa"
        val text = "kind"
        viewModelScope.launch(Dispatchers.IO) {
            withDuration {
                val url = "https://translate.google.com/m?hl=en" +
                        "&sl=$fromLanguage" +
                        "&tl=$toLanguage" +
                        "&ie=UTF-8&prev=_m" +
                        "&q=$text"

                val url2 = "https://translate.google.com/translate_a/single?&client=gtx&m?hl=en" +
                        "&sl=$fromLanguage" +
                        "&tl=$toLanguage" +
                        "&ie=UTF-8&prev=_m" +
                        "&q=$text" +
                        "&dt=bd"


                //simple translate :  https://translate.google.com/m?sl=en&tl=fa&hl=en&q=size&dt=a
                //dictionary :  https://translate.google.com/translate_a/single?&client=gtx&sl=en&tl=fa&q=kind&dt=at&dt=bd&dt=md&dt=ss&dt=ex
                TranslationTasks(text, toLanguage, fromLanguage) { debug("translate1 : $it") }

                val doc = Jsoup.connect(url)
                    .timeout(6000)
                    .get()

                withContext(Dispatchers.Main) {
                    val element = doc.getElementsByClass("result-container")
                    val textIs: String
                    if (element.isNotEmpty()) {
                        textIs = element[0].text()
                        debug("translate2 : $textIs")
                    } else {
                        debug("is empty")
                    }
                }
            }

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
        clearPrevSheet()
        loadingStatus(Progress.Loading, "sheet")

        TranslationTasks(word) { json ->
            val sheetModel = createSheetModel(JSONArray(json))
            state.update { it.copy(sheetModel = sheetModel) }
            loadingStatus(Progress.Idle, "sheet")
            if (!state.value.isPlay && !state.value.isMute) speakText(word)
        }
    }

    private fun createSheetModel(jsonArray: JSONArray): SheetModel {
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

        return SheetModel(definition, translationsModel)
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
        else
            deleteFromBookmark()
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

    //--------------------------------------------
}