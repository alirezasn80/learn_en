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
import com.alirezasn80.learn_en.core.data.datastore.AppDataStore
import com.alirezasn80.learn_en.core.data.service.ApiService
import com.alirezasn80.learn_en.core.domain.entity.WordEntity
import com.alirezasn80.learn_en.core.domain.entity.toBookEntity
import com.alirezasn80.learn_en.core.domain.local.Define
import com.alirezasn80.learn_en.core.domain.local.Desc
import com.alirezasn80.learn_en.core.domain.local.SheetModel
import com.alirezasn80.learn_en.core.domain.local.Synonym
import com.alirezasn80.learn_en.feature.stories.model.Book
import com.alirezasn80.learn_en.utill.GoogleTranslate
import com.alirezasn80.learn_en.utill.Arg
import com.alirezasn80.learn_en.utill.BaseViewModel
import com.alirezasn80.learn_en.utill.DictCategory
import com.alirezasn80.learn_en.utill.Key
import com.alirezasn80.learn_en.utill.LoadingKey
import com.alirezasn80.learn_en.utill.Progress
import com.alirezasn80.learn_en.utill.Reload
import com.alirezasn80.learn_en.utill.serverBookPath
import com.alirezasn80.learn_en.utill.debug
import com.alirezasn80.learn_en.utill.getParam
import com.alirezasn80.learn_en.utill.getString
import com.alirezasn80.learn_en.utill.isOnline
import com.alirezasn80.learn_en.utill.localBookPath
import com.alirezasn80.learn_en.utill.removeBlankLines
import com.alirezasn80.learn_en.utill.showToast
import com.alirezasn80.learn_en.utill.toBoolean
import com.alirezasn80.learn_en.utill.toLogicInt
import com.alirezasn80.learn_en.utill.toStringList
import dagger.hilt.android.lifecycle.HiltViewModel
import io.appmetrica.analytics.AppMetrica
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.json.JSONArray
import java.io.BufferedReader
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.util.Locale
import javax.inject.Inject


@HiltViewModel
class ContentViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val database: AppDB,
    private val application: Application,
    private val dataStore: AppDataStore,
    private val apiService: ApiService,
) : BaseViewModel<ContentState>(ContentState()) {
    private var job: Job? = null
    private lateinit var speechRecognizer: SpeechRecognizer
    private lateinit var speechIntent: Intent
    private val book = getParam(Arg.FILE_URL)!! as Book
    val isTrial = savedStateHandle.getString(Arg.IS_TRIAL)!! == "trial"
    private var maxReadableIndex = 0
    private var readMode = "default"


    private val tts: TextToSpeech = TextToSpeech(application) { status ->
        if (status == TextToSpeech.SUCCESS) ttsSetting()
    }

    private val wordTS: TextToSpeech = TextToSpeech(application) { status ->
        if (status == TextToSpeech.SUCCESS) wordSetting()
    }

    init {
        initDefaultFont()
        initSpeechToText()
        readBook()
        getHighlights()
    }

    private fun initDefaultFont() {
        viewModelScope.launch(Dispatchers.IO) {
            val fontSize = dataStore.getDefaultFontSize(Key.DEFAULT_FONT_SIZE)
            val fontFamily = dataStore.getDefaultFontFamily(Key.DEFAULT_FONT_FAMILY)
            state.update {
                it.copy(
                    selectedFontFamily = fontFamily,
                    selectedFontSize = fontSize
                )
            }
        }
    }

    fun setSelectedDictCategory(category: DictCategory) {
        state.update { it.copy(selectedCategory = category) }
    }

    private fun getHighlights() {

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val highlights = database.wordDao.getFlashcards().map { it.word }
                state.update { it.copy(highlights = highlights) }
            } catch (e: Exception) {
                errorException("Error in get highlights", e)
            }
        }

    }

    private fun wordSetting() {
        wordTS.language = Locale.US
        wordTS.setPitch(1f)
        wordTS.setSpeechRate(1f)
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

    fun onSelectedFontFamilyClick(fontId: Int) {
        state.update { it.copy(selectedFontFamily = fontId) }
        job?.cancel()
        job = viewModelScope.launch(Dispatchers.IO) {
            delay(500)
            dataStore.setDefaultFontSize(Key.DEFAULT_FONT_FAMILY, fontId)
        }
    }

    fun onSelectedSize(fontSize: Int) {
        state.update { it.copy(selectedFontSize = fontSize) }
        job?.cancel()
        job = viewModelScope.launch(Dispatchers.IO) {
            delay(500)
            dataStore.setDefaultFontSize(Key.DEFAULT_FONT_SIZE, fontSize)
        }
    }

    fun loading(value: Progress, key: String = "") =
        viewModelScope.launch(Dispatchers.Main) { progress[key] = value }

    private fun errorException(
        metricaMsg: String,
        e: Exception?,
        userMsg: Int = R.string.unknown_error,
        key: String = ""
    ) {
        AppMetrica.reportError(metricaMsg, e)
        setMessageBySnackbar(userMsg)
        loading(Progress.Idle, key)
    }


    /*fun readBookMain() = viewModelScope.launch(Dispatchers.IO) {

        if (!isOnline(application)) {
            return@launch
        }

        loading(Progress.Loading)

        val bookFile = File(application.bookPath(bookId.toString(), "main"))
        if (bookFile.exists()) {

        } else {
            download(book, bookId)
        }

        val contentEntity = try {
            database.contentDao.getContent(categoryId, bookId)
        } catch (e: Exception) {
            errorException("Error in get content entity", e)
            return@launch
        }

        state.update { it.copy(isBookmark = contentEntity.favorite.toBoolean()) }
        title = contentEntity.title

        val paragraphs = if (contentEntity.translation.isNullOrBlank()) {// remote

            translateRemoteMain(contentEntity)
        } else { // locale
            processTranslateJson(contentEntity.translation)
        }


        state.update {
            it.copy(
                isBookmark = contentEntity.favorite.toBoolean(),
                title = contentEntity.title,
                paragraphs = paragraphs
            )
        }

        loading(Progress.Idle)


    }*/

    fun readBook() {
        if (!isOnline(application)) return


        viewModelScope.launch(Dispatchers.IO) {
            loading(Progress.Loading)

            val path = if (book.fileUrl == "local")
                application.localBookPath(book.bookId.toString())
            else
                application.serverBookPath(book.bookId.toString())

            val bookFile = File(path)

            if (bookFile.exists()) {
                val textBook = readFile(bookFile)
                val paragraphs = translate(textBook)

                state.update {
                    it.copy(
                        //    isBookmark = contentEntity.favorite.toBoolean(),
                        title = book.name,
                        paragraphs = paragraphs
                    )
                }

                loading(Progress.Idle)

            } else {
                download(book.fileUrl, book.bookId)
            }

        }
    }


    private fun download(url: String, id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val responseBody = apiService.downloadFile(url)
                val bookFile = File(application.serverBookPath(id.toString()))
                writeFile(bookFile, responseBody.byteStream())
                readBook()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun readFile(file: File): String {
        val bufferedReader: BufferedReader = file.bufferedReader()
        val inputString = bufferedReader.use { it.readText() }
        return inputString
    }

    private fun writeFile(file: File, inputStream: InputStream) {

        try {
            val fileOutputStream = FileOutputStream(file)

            //write and save data
            val bytes = ByteArray(1024)
            var read = inputStream.read(bytes)
            while (read != -1) {
                fileOutputStream.write(bytes, 0, read)
                read = inputStream.read(bytes)
            }
            fileOutputStream.close()
            inputStream.close()
            fileOutputStream.flush()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }


    /*private suspend fun translateRemoteMain(
        contentEntity: ContentEntity,
        from: String = "en",
        to: String = "fa"
    ): List<Paragraph> {
        //todo(check max 5,000 char)
        var newContent = contentEntity.content.trimIndent().removeBlankLines()
        if (newContent.startsWith(book.name)) newContent = newContent.replace(book.name, "${book.name}.\n")

        return try {
            val translated = GoogleTranslate.getJsonTranslate(
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


    }*/

    private fun translate(
        text: String,
        from: String = "en",
        to: String = "fa"
    ): List<Paragraph> {
        //todo(check max 5,000 char)
        var newContent = text.trimIndent().removeBlankLines()
        if (newContent.startsWith(book.name)) newContent = newContent.replace(book.name, "${book.name}.\n")

        return try {
            val translated = GoogleTranslate.getJsonTranslate(
                newContent,
                to,
                from
            )
            val result = processTranslateJson(translated)
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
        if (state.value.isPlay)
            readParagraph(false)

        clearPrevSheet()
        executeDictionary(word)

    }

    private fun executeDictionary(word: String) {
        viewModelScope.launch(Dispatchers.IO) {

            try {
                val wordEntity = database.wordDao.getWordEntity(word)

                if (wordEntity == null) {
                    if (isOnline(application)) {
                        val translated = GoogleTranslate.getJsonDictionary(word)
                        if (translated.isNotEmpty()) {
                            database.wordDao.insertWord(WordEntity(word, translated, 0))
                            executeDictionary(word)
                        }
                    } else {
                        setMessageBySnackbar(R.string.check_connection)
                    }
                } else {

                    //val dictImages = database.wordImgDao.getDictImages(word)
                    val sheetModel = createSheetModel(
                        mainWord = word,
                        isHighlight = wordEntity.isHighlight.toBoolean(),
                        jsonArray = JSONArray(wordEntity.definition)
                    )

                    state.update { it.copy(sheetModel = sheetModel) }

                    /*if (dictImages.isEmpty()) {
                        getRelatedImages(word)
                    } else {
                        loading(Progress.Idle, LoadingKey.IMG)
                    }*/

                    if (!state.value.isPlay && !state.value.isMute) speakText(word)


                    loading(Progress.Idle, LoadingKey.DICT)
                }
            } catch (e: Exception) {
                //loading(Progress.Idle, LoadingKey.IMG)
                errorException(
                    metricaMsg = "Error in Word Click",
                    e = e,
                    key = LoadingKey.DICT,
                    userMsg = R.string.no_found_data
                )
            }
        }

    }

    // not used because some images not related the word
    /*private fun getRelatedImages(word: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val url = "https://images.search.yahoo.com/search/images?p=$word"
                val document = Jsoup.connect(url).get()
                val images = document
                    .select("section#maindoc")
                    .select("section#mdoc")
                    .select("section#main")
                    .select("div#main-algo")
                    .select("div#res-cont")
                    .select("section#results")
                    .select("div.sres-cntr")
                    .select("ul#sres")
                    .tagName("li")
                    .select("a.redesign-img.round-img")
                    .select("img")
                    .map {
                        it.absUrl("src")
                    }
                    .filter { it != null && it.isNotBlank() && it.startsWith("http") }
                    .take(20)

                images.forEach { imageUrl ->
                   // database.wordImgDao.insertWordImg(WordImgEntity(word = word, url = imageUrl))
                }

                state.update { it.copy(sheetModel = state.value.sheetModel?.copy(images = images)) }
                loading(Progress.Idle, LoadingKey.IMG)

            } catch (e: Exception) {
                loading(Progress.Idle, LoadingKey.IMG)
            }

        }
    }*/

    private fun createSheetModel(
        mainWord: String,
        isHighlight: Boolean,
        jsonArray: JSONArray
    ): SheetModel {
        val synonymsModel = mutableListOf<Synonym>()
        val descriptionModel = mutableListOf<Desc>()
        val examples = mutableListOf<String>()


        // Definition------------------------------
        val definition = try {
            jsonArray.getJSONArray(0).getJSONArray(0).getString(0)
        } catch (e: Exception) {
            mainWord
        }


        // Synonyms ------------------------------
        val bdJson: JSONArray? = try {
            jsonArray.getJSONArray(1)
        } catch (e: Exception) {
            null
        }

        if (bdJson != null)
            for (i in 0 until bdJson.length()) {
                val array = bdJson.getJSONArray(i)
                val type = array.getString(0)
                val totalSimilar = array.getJSONArray(2)

                val defines: MutableList<Define> = mutableListOf()

                for (j in 0 until totalSimilar.length()) {
                    val similarTranslate = totalSimilar.getJSONArray(j).getString(0)
                    val synonyms = totalSimilar.getJSONArray(j).getJSONArray(1)
                    val define = Define(similarTranslate, synonyms.toStringList())
                    defines.add(define)
                }

                val synonym = Synonym(type, defines)
                synonymsModel.add(synonym)
                //definesModel.clear()
            }


        //Description of Word ------------------------------
        val mdJson = try {
            jsonArray.getJSONArray(12)
        } catch (e: Exception) {
            null
        }

        if (mdJson != null)
            for (i in 0 until mdJson.length()) {
                val array = mdJson.getJSONArray(i)
                val type = array.getString(0)
                val descriptions = array.getJSONArray(1)

                val texts = mutableListOf<String>()
                for (j in 0 until descriptions.length()) {
                    val desc = descriptions.getJSONArray(j).getString(0)
                    texts.add(desc)
                }
                descriptionModel.add(Desc(type, texts))
            }


        //Example of Word ---------------------------------------
        val exJson = try {
            jsonArray.getJSONArray(13).getJSONArray(0)
        } catch (e: Exception) {
            null
        }

        if (exJson != null)
            for (i in 0 until exJson.length()) {
                val array = exJson.getJSONArray(i)
                val example = array.getString(0)
                examples.add(example)
            }

        debug(jsonArray.toString())

        //Create Model ----------------------------------------------

        return SheetModel(
            word = mainWord,
            define = definition,
            synonyms = synonymsModel,
            isHighlight = isHighlight,
            descriptions = descriptionModel,
            examples = examples
        )
    }

    private fun clearPrevSheet() {
        state.update { it.copy(sheetModel = null, selectedCategory = DictCategory.Meaning) }
    }

    fun setReadSpeed(speed: Float) {
        tts.setSpeechRate(speed)
    }

    fun wordSpeak(
        value: String,
        speed: Float
    ) {
        wordTS.setSpeechRate(speed)
        wordTS.speak(value, TextToSpeech.QUEUE_FLUSH, null, null)

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


    // todo(crash it)
    private fun addToBookmark() {
        viewModelScope.launch(Dispatchers.IO) {
            database.bookDao.addToFavorite(book.toBookEntity(1))
        }
        application.showToast(R.string.add_to_bookmark)
    }

    private fun deleteFromBookmark() {
        viewModelScope.launch(Dispatchers.IO) {
            database.bookDao.deleteFromFavorite(book.bookId, book.categoryId)
        }
        application.showToast(R.string.delete_from_bookmark)
    }

    fun changeHighlightMode(word: String, isHighlight: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            database.wordDao.updateFlashcardByWord(isHighlight.toLogicInt(), word)
            state.update { it.copy(sheetModel = state.value.sheetModel?.copy(isHighlight = isHighlight)) }
            getHighlights()
        }
    }

}