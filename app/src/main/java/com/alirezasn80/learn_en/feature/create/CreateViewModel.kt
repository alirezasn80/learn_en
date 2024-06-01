package com.alirezasn80.learn_en.feature.create

import android.app.Application
import android.content.Intent
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.viewModelScope
import com.alirezasn80.learn_en.R
import com.alirezasn80.learn_en.core.data.database.AppDB
import com.alirezasn80.learn_en.core.domain.entity.CategoryEntity
import com.alirezasn80.learn_en.core.domain.entity.BookEntity
import com.alirezasn80.learn_en.core.domain.entity.toCategoryModel
import com.alirezasn80.learn_en.utill.BaseViewModel
import com.alirezasn80.learn_en.utill.Destination
import com.alirezasn80.learn_en.utill.MessageState
import com.alirezasn80.learn_en.utill.Reload
import com.alirezasn80.learn_en.utill.debug
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.jsoup.Jsoup
import javax.inject.Inject

@HiltViewModel
class CreateViewModel @Inject constructor(
    private val context: Application,
    private val db: AppDB,
) : BaseViewModel<CreateState>(CreateState()) {
    private lateinit var speechRecognizer: SpeechRecognizer
    private lateinit var speechIntent: Intent
    //private val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)


    init {
        initSpeechToText()
        initCreatedCategories()
    }

    private fun initCreatedCategories() {
        viewModelScope.launch(Dispatchers.IO) {
            val createdCategories = db.categoryDao.getCategories("created").map { it.toCategoryModel() }
            state.update { it.copy(createdCategories = createdCategories) }
        }
    }

    private fun initSpeechToText() {
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context)
        speechIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        speechIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        speechIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-US")
    }

    fun onTitleChange(value: String) = state.update { it.copy(title = value) }

    fun onContentChange(value: TextFieldValue, endCursor: Boolean = false) =
        state.update {
            it.copy(
                content = if (endCursor)
                    value.copy(selection = TextRange(value.text.length))
                else
                    value
            )
        }


    private fun createUrl(from: String, to: String, text: String): String {
        return "https://translate.google.com/m?hl=en" +
                "&sl=$from" +
                "&tl=$to" +
                "&ie=UTF-8&prev=_m" +
                "&q=$text"

    }

    fun onTranslatedContent(value: String) {

        viewModelScope.launch(Dispatchers.IO) {
            //todo(check max 5,000 char)

            val url = createUrl("fa", "en", value)
            debug(url)
            try {
                val doc = Jsoup.connect(url).get()
                val element = doc.getElementsByClass("result-container")

                if (element.isNotEmpty()) {
                    val result = element[0].text()
                    val newResult = state.value.content.copy(state.value.content.text.plus(result))
                    onContentChange(newResult, true)
                } else {
                    debug("Empty Translation!")
                }

            } catch (e: Exception) {
                debug("Error in Translation : ${e.message}")
            }

        }

    }

    /*fun processImageUri(uri: Uri) {
        val image: InputImage
        try {
            image = InputImage.fromFilePath(context, uri)
            val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

            recognizer.process(image)
                .addOnSuccessListener { visionText ->
                    // Text recognition success
                    val newResult = state.value.content.copy(state.value.content.text.plus(visionText.text))
                    onContentChange(newResult, true)
                }
                .addOnFailureListener { e ->
                    // Handle the error
                }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }*/

    fun createStory(categoryId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            db.bookDao.insertContent(
                BookEntity(
                    categoryId = categoryId,
                    bookPath = state.value.content.text,
                    title = state.value.title,
                    isFavorite = 0,
                    translationPath = null
                )
            )
            setMessageByToast(R.string.saved, MessageState.Success)
            Reload.created = true
            setDestination(Destination.Back)
        }
    }

    fun createCategory(title: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val categoryId = db.categoryDao.insertCategory(CategoryEntity(title = title, type = "created"))
            createStory(categoryId.toInt())
        }
    }

}