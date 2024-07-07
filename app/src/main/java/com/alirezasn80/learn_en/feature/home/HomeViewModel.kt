package com.alirezasn80.learn_en.feature.home

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.alirezasn80.learn_en.R
import com.alirezasn80.learn_en.core.data.database.AppDB
import com.alirezasn80.learn_en.core.data.datastore.AppDataStore
import com.alirezasn80.learn_en.core.data.service.ApiService
import com.alirezasn80.learn_en.core.domain.entity.toBook
import com.alirezasn80.learn_en.core.domain.entity.toCategory
import com.alirezasn80.learn_en.utill.BaseViewModel
import com.alirezasn80.learn_en.utill.Key
import com.alirezasn80.learn_en.utill.Myket
import com.alirezasn80.learn_en.utill.Progress
import com.alirezasn80.learn_en.utill.Reload
import com.alirezasn80.learn_en.utill.User
import com.alirezasn80.learn_en.utill.debug
import dagger.hilt.android.lifecycle.HiltViewModel
import io.appmetrica.analytics.AppMetrica
import ir.myket.billingclient.IabHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.jsoup.Jsoup
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val dp: AppDB,
    private val dataStore: AppDataStore,
    private val payment: IabHelper,
    private val apiService: ApiService,
    private val application: Application,
) : BaseViewModel<HomeState>(HomeState()) {



    init {
        checkUpdate()
        openAppCounter()
        getCommentStatus()
        getCategoriesRemote()
        //getCategories()
        getLastReadCategory()
    }

    private fun getCategoriesRemote() {
        progress(Progress.Loading)
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val categories = apiService.getCategories()
                state.update { it.copy(categories = categories) }
            } catch (e: Exception) {
                AppMetrica.reportError("Error Get Categories Remote", e)
                debug("Error Get Categories Remote:${e.message}")
                setMessageBySnackbar(R.string.unknown_error)
            } finally {
                progress(Progress.Idle)
            }
        }
    }

    private fun getLastReadCategory() {
        viewModelScope.launch(Dispatchers.IO) {
            val id = dataStore.getLastReadCategory(Key.LAST_READ_CATEGORY)
            state.update { it.copy(lastReadCategory = id) }
        }
    }

    fun hideNotificationAlert() = state.update { it.copy(showNotificationAlert = false) }


    private fun checkUpdate() {
        /*val mMyketHelper = MyketSupportHelper(application)

        mMyketHelper.startSetup { result ->
            if (result.isSuccess) {
                mMyketHelper.getAppUpdateStateAsync { result2, update ->
                    if (update != null && update.isUpdateAvailable) {
                        state.update { it.copy(needUpdate = true) }

                    }
                }


            }
        }*/
    }



    private fun openAppCounter() {

        viewModelScope.launch {
            // Calculate number of user open application
            var count = dataStore.getOpenAppCounter(Key.COUNTER)
            count++
            dataStore.setOpenAppCounter(Key.COUNTER, count)
            state.update { it.copy(openAppCount = count) }
        }

    }

    private fun getCommentStatus() {
        viewModelScope.launch {
            val commentStatus = dataStore.getCommentStatus(Key.COMMENT)
            state.update { it.copy(showComment = commentStatus == null) }
        }
    }

    fun hideNeedUpdate() = state.update { it.copy(needUpdate = false) }

    fun hideCommentItem(status: String) {
        viewModelScope.launch {
            dataStore.setCommentStatus(Key.COMMENT, status)
            state.update { it.copy(showComment = false) }
        }
    }

    fun setDialogKey(key: HomeDialogKey) {
        state.update { it.copy(dialogKey = key) }
    }

    fun resetOpenAppCounter() {
        viewModelScope.launch {
            dataStore.setOpenAppCounter(Key.COUNTER, 0)
            state.update { it.copy(openAppCount = 0) }
        }
    }


    fun setSelectedTab(tab: Tab) {
        state.update { it.copy(selectedTab = tab) }

        /*if (tab is Tab.Favorite) {
            if (state.value.favorites.isEmpty() || Reload.favorite)
                getFavoriteBooks()

            return
        }*/

        if (tab is Tab.Local) {
            if (state.value.localCategories.isEmpty() || Reload.local)
                getLocalCategories()

            return
        }
    }

    private fun getLocalCategories() {
        progress(Progress.Loading)

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val categories = dp.categoryDao.getLocalCategories().map { it.toCategory() }
                state.update { it.copy(localCategories = categories, favorites = emptyList()) }
            } catch (e: Exception) {
                AppMetrica.reportError("error reload data in home", e)
            } finally {
                progress(Progress.Idle)
            }

        }
    }

    fun reloadData() {
        when (state.value.selectedTab) {
            Tab.Default -> Unit
            //  Tab.Favorite -> getFavoriteBooks()
            Tab.Local -> getLocalCategories()
        }
    }

    private fun progress(value: Progress) {
        viewModelScope.launch(Dispatchers.Main) {
            progress[""] = value
        }
    }

    private fun getFavoriteBooks() {
        debug("get Favorite Books")
        progress(Progress.Loading)

        viewModelScope.launch(Dispatchers.IO) {

            try {
                val favoriteBooks = dp.bookDao.getFavoriteBooks().map { it.toBook() }
                state.update { it.copy(favorites = favoriteBooks) }
            } catch (e: Exception) {
                AppMetrica.reportError("Error Get Favorite Books", e)
                debug("Error Get Favorite Books:${e.message}")
            } finally {
                progress(Progress.Idle)
            }

        }

    }

    private fun checkPurchaseProduct() {

        try {
            state.update { it.copy(loadingBazaar = true) }

            payment.queryInventoryAsync(
                true, listOf("vip")
            ) { result, inv ->

                if (result.isSuccess) {
                    val purchase = inv.getPurchase("vip")
                    debug("purchase : ${purchase.toString()}")

                    if (purchase == null) {
                        setMessageByToast(R.string.no_vip_now)
                    } else {
                        viewModelScope.launch {
                            dataStore.isVIP(Key.IS_VIP, true)
                            User.isVipUser = true
                            setMessageByToast(R.string.you_now_vip)
                        }
                    }


                } else {
                    setMessageByToast(R.string.error_connect_bazaar)
                }

            }
        } catch (e: Exception) {
            setMessageByToast(R.string.error_connect_bazaar)
        } finally {
            state.update { it.copy(loadingBazaar = false) }

        }

    }

    fun connectMyket() {
        if (Myket.alreadyConnection)
            checkPurchaseProduct()
        else
            payment.startSetup { result ->
                if (result.isSuccess) {
                    Myket.alreadyConnection = true
                    debug("success connect to myket")
                    checkPurchaseProduct()

                } else {
                    debug("failed connect to myket")
                    setMessageByToast(R.string.error_connect_bazaar)
                }
            }

    }


    fun saveAsLastRead(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            dataStore.setLastReadCategory(Key.LAST_READ_CATEGORY, id)
            state.update { it.copy(lastReadCategory = id) }
        }
    }

    //--------------------------------------------------------
    private fun getCategories() {
        /*loading(Progress.Loading)

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val items = dp.categoryDao.getCategories("default")
                val categories = items.map { it.toCategoryModel(true) }
                state.update { it.copy(categories = categories) }
            } catch (e: Exception) {
                AppMetrica.reportError("error getCategories in home", e)
            } finally {
                loading(Progress.Idle)
            }

        }*/
    }

    private fun getCreatedCategories(key: String) {
        /*loading(Progress.Loading)
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val categories = dp.categoryDao.getCategories(key).map { it.toCategoryModel(true) }
                state.update { it.copy( categories = categories) }
            } catch (e: Exception) {
                AppMetrica.reportError("error set selected level in home", e)
            } finally {
                loading(Progress.Idle)
            }
        }
        */
    }

    private fun googleCrawl() {
        viewModelScope.launch(Dispatchers.IO) {
            //.getJsonFromUrl("https://www.google.com/search?tbm=isch&q=Dota2")

            val searchQuery = "flower" // جستجوی شما
            val url = "https://www.google.com/search?q=flower&sclient=img&udm=2"
            val client = OkHttpClient()

            val request = Request.Builder()
                .url(url)
                .addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/103.0.5060.114 Safari/537.36")
                .build()

// ارسال درخواست و دریافت پاسخ
            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    // Handle the error
                    debug("failure")

                }

                override fun onResponse(call: Call, response: Response) {
                    if (!response.isSuccessful) {
                        // Handle the error
                        debug("error")

                    } else {
                        // data-docid="hPkStLsNjw6jTM"

                        debug("success")
                        val responseBody = response.body?.string()
                        val soup = Jsoup.parse(responseBody)
                            .select("div.main")
                            .select("div.GyAeWb.gIatYd")
                            .select("div.s6JM6d")
                            .select("div.eqAnXb")
                            .select("div#search")
                        //.select("[data-hveid=CAEQEg]")//dynamic
                        //have continue
                        // debug(soup.toString())
                        debug(extractDocIds(soup.toString()).toString())
                        val key = extractDocIds(soup.toString()).first()
                        val newUrl = "https://www.google.com/search?q=flower&sclient=img&udm=2#vhid=${key}&vssid=mosaic"
                        debug(newUrl)
                        val newResult = Jsoup.connect(newUrl).get().toString()
                        debug(newResult)
                    }
                }
            })

        }
    }

    private fun extractDocIds(input: String): List<String> {
        val regex = """data-docid="(\w+)"""".toRegex()
        return regex.findAll(input).map { it.groupValues[1] }.toList()
    }

}