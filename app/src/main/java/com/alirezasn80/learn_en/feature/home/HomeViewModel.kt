package com.alirezasn80.learn_en.feature.home

import androidx.lifecycle.viewModelScope
import com.alirezasn80.learn_en.R
import com.alirezasn80.learn_en.core.data.database.AppDB
import com.alirezasn80.learn_en.core.data.datastore.AppDataStore
import com.alirezasn80.learn_en.core.domain.entity.toCategoryModel
import com.alirezasn80.learn_en.feature.payment.toMonth
import com.alirezasn80.learn_en.utill.BaseViewModel
import com.alirezasn80.learn_en.utill.Key
import com.alirezasn80.learn_en.utill.MessageState
import com.alirezasn80.learn_en.utill.Progress
import com.alirezasn80.learn_en.utill.User
import com.alirezasn80.learn_en.utill.debug
import dagger.hilt.android.lifecycle.HiltViewModel
import io.appmetrica.analytics.AppMetrica
import ir.cafebazaar.poolakey.Connection
import ir.cafebazaar.poolakey.Payment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val dp: AppDB,
    private val dataStore: AppDataStore,
    private val payment: Payment
) : BaseViewModel<HomeState>(HomeState()) {
    private var bazaarConnection: Connection? = null

    // private var connection: CheckUpdateApp? = null

    init {
        checkUpdate()
        openAppCounter()
        getCommentStatus()
        getCategories()
    }

    fun hideNotificationAlert() = state.update { it.copy(showNotificationAlert = false) }

    private fun checkUpdate() {
        /*connection = CheckUpdateApp(object : CheckUpdateAppListener {
            override fun needUpdate(value: Boolean) {
                state.update { it.copy(needUpdate = value) }
                connection?.let {
                    application.unbindService(it);
                    connection = null
                }

            }
        })
        val i = Intent("com.farsitel.bazaar.service.UpdateCheckService.BIND")
        i.setPackage("com.farsitel.bazaar")
        application.bindService(i, connection!!, Context.BIND_AUTO_CREATE)*/
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

    //---------------------------------------------------------------------------------------------

    fun setSelectedLevel(section: Section) {
        if (section == state.value.selectedSection)
            return

        if (section.key == "favorite") {
            getFavorites(section)
            return
        }
        loading(Progress.Loading)
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val categories = dp.categoryDao.getCategories(section.key).map { it.toCategoryModel(true) }
                state.update { it.copy(selectedSection = section, categories = categories, favorites = emptyList()) }
            } catch (e: Exception) {
                AppMetrica.reportError("error set selected level in home", e)
            } finally {
                loading(Progress.Idle)
            }
        }

    }

    fun reloadData() {
        if (state.value.selectedSection.key == "favorite") {
            getFavorites(state.value.selectedSection)
            return
        }

        loading(Progress.Loading)
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val categories = dp.categoryDao.getCategories(state.value.selectedSection.key).map { it.toCategoryModel(true) }
                state.update { it.copy(categories = categories, favorites = emptyList()) }
            } catch (e: Exception) {
                AppMetrica.reportError("error reload data in home", e)
            } finally {
                loading(Progress.Idle)
            }

        }

    }

    fun loading(value: Progress) {
        viewModelScope.launch(Dispatchers.Main) {
            progress[""] = value
        }
    }

    fun getFavorites(section: Section) {
        loading(Progress.Loading)

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val favorites = dp.contentDao.getFavorites()
                state.update { it.copy(favorites = favorites, selectedSection = section) }
            } catch (e: Exception) {
                AppMetrica.reportError("error get favorites in home", e)
            } finally {
                loading(Progress.Idle)
            }

        }
    }

    private fun getCategories() {
        loading(Progress.Loading)

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

        }
    }

    fun checkSubscribeStatus() {
        progress["bazaar"] = Progress.Loading

        //todo()
        viewModelScope.launch(Dispatchers.IO) {

            bazaarConnection = payment.connect {

                //Success Connection To Cafe Bazaar
                connectionSucceed {
                    debug("conection success")
                    payment.getSubscribedProducts {

                        querySucceed { purchasedProducts ->
                            debug("query success")
                            progress["bazaar"] = Progress.Idle


                            viewModelScope.launch {
                                if (purchasedProducts.isEmpty()) {
                                    debug("is empty")
                                    setMessageBySnackbar(R.string.no_any_subscribe, MessageState.Error)
                                    User.isVipUser = false
                                    dataStore.setExpireDate(Key.EXPIRE_DATE, -1L)
                                } else {
                                    debug("else")
                                    User.isVipUser = true
                                    val purchaseTime = purchasedProducts[0].purchaseTime
                                    val dateOfPurchase = Date(purchaseTime)
                                    val calendar = Calendar.getInstance()
                                    calendar.time = dateOfPurchase
                                    calendar.add(Calendar.MONTH, purchasedProducts[0].productId.toMonth()!!)
                                    val expireDate = calendar.time
                                    dataStore.setExpireDate(Key.EXPIRE_DATE, expireDate.time)
                                    setMessageBySnackbar(R.string.you_now_vip, MessageState.Success)

                                }
                            }


                        }

                        queryFailed {
                            progress["bazaar"] = Progress.Idle
                            setMessageBySnackbar(R.string.problem_connection_bazaar)
                        }
                    }
                }

                connectionFailed {
                    progress["bazaar"] = Progress.Idle

                    setMessageBySnackbar(R.string.problem_connection_bazaar)
                }


            }
        }
    }

}