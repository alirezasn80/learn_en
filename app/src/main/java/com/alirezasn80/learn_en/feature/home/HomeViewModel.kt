package com.alirezasn80.learn_en.feature.home

import androidx.lifecycle.viewModelScope
import com.alirezasn80.learn_en.core.data.database.AppDB
import com.alirezasn80.learn_en.core.domain.entity.CategoryEntity
import com.alirezasn80.learn_en.core.domain.entity.ContentEntity
import com.alirezasn80.learn_en.utill.BaseViewModel
import com.alirezasn80.learn_en.utill.debug
import com.alirezasn80.learn_en.utill.withDuration
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val appDB: AppDB
) : BaseViewModel<HomeState>(HomeState()) {

    init {
        getCategories()
        translate2()
    }

    fun translate(text: String, sourceLang: String, targetLang: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val encodedText = URLEncoder.encode(text, "UTF-8")
            val languagePair = URLEncoder.encode("$sourceLang|$targetLang", "UTF-8")
            val urlString = "http://api.mymemory.translated.net/get?q=$encodedText&langpair=$languagePair"
            debug(urlString)
            val url = URL(urlString)
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"

            val responseCode = connection.responseCode
            if (responseCode == HttpURLConnection.HTTP_OK) {
                val response = connection.inputStream.bufferedReader().use { it.readText() }
                // Parse the JSON response to extract the translated text
                // ...
                debug(response)
            } else {
                debug(":(")
            }

        }

    }


    /*
    *
    * "https://translate.google.com/m?hl=en" +
                            "&sl=$fromLanguage" +
                            "&tl=$toLanguage" +
                            "&ie=UTF-8&prev=_m" +
                            "&q=$text"
    *
    * */


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

    fun setSelectedCategory(category: Category) = state.update { it.copy(selectedCategory = category) }

    private fun getCategories() {
        viewModelScope.launch {
            val items = appDB.categoryDao.getCategories()
            state.update { it.copy(categories = items) }
        }
    }

}