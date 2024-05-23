package com.alirezasn80.learn_en.feature.stories

import android.app.Application
import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.alirezasn80.learn_en.core.data.database.AppDB
import com.alirezasn80.learn_en.core.data.datastore.AppDataStore
import com.alirezasn80.learn_en.core.data.service.ApiService
import com.alirezasn80.learn_en.core.domain.entity.toItems
import com.alirezasn80.learn_en.utill.Arg
import com.alirezasn80.learn_en.utill.BaseViewModel
import com.alirezasn80.learn_en.utill.Key
import com.alirezasn80.learn_en.utill.debug
import com.alirezasn80.learn_en.utill.getString
import com.alirezasn80.learn_en.utill.toPart
import com.alirezasn80.learn_en.utill.toRB
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import java.io.File
import javax.inject.Inject


@HiltViewModel
class StoriesViewModel @Inject constructor(
    savedstate: SavedStateHandle,
    private val database: AppDB,
    private val dataStore: AppDataStore,
    private val apiService: ApiService,
    private val application: Application,
) : BaseViewModel<StoriesState>(StoriesState()) {
    val categoryId = savedstate.getString(Arg.CATEGORY_ID)!!.toInt()
    private val title = savedstate.getString(Arg.TITLE)!!


    init {
        state.update { it.copy(title = title) }
        getStories()
        getLastReadCategory()
        // addBook()
    }

    private fun addBook() {

        viewModelScope.launch(Dispatchers.IO) {
            val story = database.contentDao.getStoriesByCategory(categoryId).first()//todo()
            try {
                val result = apiService.addBook(
                    categoryId = "4".toRB(),
                    name = story.title.toRB(),
                    cover = null,
                    file = createTXT(application, story.title, story.content).toPart("file")
                )
                debug(result)
            } catch (e: Exception) {
                debug("Error!")
            }


        }
    }

    private fun createTXT(context: Context, title: String, body: String): File {
        val file = File(context.cacheDir, title)
        file.createNewFile()
        file.writeText(body)
        return file
    }

    private fun getStories() {
        viewModelScope.launch(Dispatchers.IO) {
            val items = database.contentDao.getItems(categoryId)

            state.update { it.copy(items = items) }
        }

    }

    fun saveAsLastRead(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            dataStore.setLastReadStory(Key.LAST_READ_STORY, id)
            state.update { it.copy(isLastReadStory = id) }
        }
    }

    private fun getLastReadCategory() {
        viewModelScope.launch(Dispatchers.IO) {
            val id = dataStore.getLastReadStory(Key.LAST_READ_STORY)
            state.update { it.copy(isLastReadStory = id) }
        }
    }


}