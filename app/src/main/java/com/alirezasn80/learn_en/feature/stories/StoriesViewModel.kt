package com.alirezasn80.learn_en.feature.stories

import android.app.Application
import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.alirezasn80.learn_en.core.data.database.AppDB
import com.alirezasn80.learn_en.core.data.datastore.AppDataStore
import com.alirezasn80.learn_en.core.data.service.ApiService
import com.alirezasn80.learn_en.feature.stories.model.Book
import com.alirezasn80.learn_en.utill.Arg
import com.alirezasn80.learn_en.utill.BaseViewModel
import com.alirezasn80.learn_en.utill.CustomPager
import com.alirezasn80.learn_en.utill.Key
import com.alirezasn80.learn_en.utill.Progress
import com.alirezasn80.learn_en.utill.debug
import com.alirezasn80.learn_en.utill.getString
import com.alirezasn80.learn_en.utill.toPart
import com.alirezasn80.learn_en.utill.toRB
import dagger.hilt.android.lifecycle.HiltViewModel
import io.appmetrica.analytics.AppMetrica
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
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
    var books: Flow<PagingData<Book>>? = null


    init {
        state.update { it.copy(title = title) }
        getStoriesRemote()
        //getStories()
        getLastReadCategory()
        // addBook()
    }

    private fun getStoriesRemote() {
        progress(Progress.Loading)


        books = CustomPager(
            request = { page ->
                val bookResponse = apiService.getBooks(categoryId, page)
                val items = bookResponse.books
                setKeys(items.isEmpty())
                progress(Progress.Idle)
                items
            },
            handleException = {
                AppMetrica.reportError("Error Get Stories Remote", it)
                debug("Error Get Stories Remote:${it.message}")
                progress(Progress.Idle)

            }
        ).build().cachedIn(viewModelScope)


    }


    private fun progress(_progress: Progress) {
        progress[""] = _progress
    }

    fun addBook(key: String) {

        viewModelScope.launch(Dispatchers.IO) {
            val stories = database.contentDao.getStoriesByCategory(categoryId)

            try {
                stories.forEach {
                    val result = apiService.addBook(
                        categoryId = key.toRB(),
                        name = it.title.toRB(),
                        cover = null,
                        file = createTXT(application, it.title, it.content).toPart("file")
                    )
                    debug(result.success.toString())
                }
                debug("Finish All (${stories.size})")

            } catch (e: Exception) {
                debug("Error! ${e.message}")
            }


        }
    }

    private fun createTXT(context: Context, title: String, body: String): File {
        val file = File(context.cacheDir, "$title.txt")
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