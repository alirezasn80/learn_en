package com.alirezasn80.learn_en.feature.payment

import android.app.Application
import android.widget.Toast
import androidx.activity.result.ActivityResultRegistry
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alirezasn80.learn_en.R
import com.alirezasn80.learn_en.core.data.datastore.AppDataStore
import com.alirezasn80.learn_en.utill.Arg
import com.alirezasn80.learn_en.utill.BaseViewModel
import com.alirezasn80.learn_en.utill.Key
import com.alirezasn80.learn_en.utill.MessageState
import com.alirezasn80.learn_en.utill.ProductId
import com.alirezasn80.learn_en.utill.Progress
import com.alirezasn80.learn_en.utill.User
import com.alirezasn80.learn_en.utill.debug
import com.alirezasn80.learn_en.utill.getString
import com.alirezasn80.learn_en.utill.showToast
import dagger.hilt.android.lifecycle.HiltViewModel
import io.appmetrica.analytics.AppMetrica
import ir.cafebazaar.poolakey.Connection
import ir.cafebazaar.poolakey.Payment
import ir.cafebazaar.poolakey.request.PurchaseRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class PaymentViewModel @Inject constructor(
    private val payment: Payment,
    private val application: Application,
    private val dataStore: AppDataStore,
    stateHandle: SavedStateHandle,
) : BaseViewModel<PaymentState>(PaymentState()) {
    private var paymentConnection: Connection? = null
    private val key = stateHandle.getString(Arg.Key) ?: ""

    init {
        AppMetrica.reportEvent("O.P", mapOf(key to "Open"))
    }


    fun buySubscribe(registry: ActivityResultRegistry?, productId: String) {
        if (registry == null) return

        showLoading()

        viewModelScope.launch(Dispatchers.IO) {
            paymentConnection = payment.connect {

                //Success Connection To Cafe Bazaar
                connectionSucceed {

                    // Request Open Payment Page
                    payment.subscribeProduct(
                        registry = registry,
                        request = PurchaseRequest(productId = productId)
                    ) {


                        // Success Open Payment Page
                        purchaseFlowBegan {
                            hideLoading()
                        }

                        // Error Open Payment Page
                        failedToBeginFlow {
                            hideLoading()
                            launch(Dispatchers.Main) { application.showToast(R.string.error_payment_page_bazaar) }
                        }

                        // Success Payment
                        purchaseSucceed {
                            AppMetrica.reportEvent("S.P", mapOf(key to productId))
                            saveExpireDate(productId.toMonth())
                            User.isVipUser = true
                            state.update { it.copy(successPayment = true) }
                        }


                        // Cancel Payment
                        purchaseCanceled {
                            hideLoading()
                        }


                        //Failed Payment
                        purchaseFailed {
                            hideLoading()
                            launch(Dispatchers.Main) { application.showToast(R.string.error_process_payment_bazaar) }
                        }

                    }

                }

                //Failed Connection To Cafe Bazaar
                connectionFailed {
                    hideLoading()
                    launch(Dispatchers.Main) { application.showToast(R.string.error_connect_bazaar, Toast.LENGTH_LONG) }
                }

                // Finish Connect to CafeBazaar
                disconnected { hideLoading() }
            }

        }
    }

    private fun saveExpireDate(month: Int?) {
        if (month == null) return

        viewModelScope.launch(Dispatchers.IO) {
            // Get the current date
            val currentDate = Date()

            // Get the date 3 months from now
            val calendar = Calendar.getInstance()
            calendar.time = currentDate
            calendar.add(Calendar.MONTH, month)
            val expireDate = calendar.time

            dataStore.setExpireDate(Key.EXPIRE_DATE, expireDate.time)
        }

    }

    fun buyProduct(registry: ActivityResultRegistry?, productId: String) {
        if (registry == null) return

        showLoading()

        viewModelScope.launch(Dispatchers.IO) {
            paymentConnection = payment.connect {

                //Success Connection To Cafe Bazaar
                connectionSucceed {

                    // Request Open Payment Page
                    payment.purchaseProduct(
                        registry = registry,
                        request = PurchaseRequest(productId = productId)
                    ) {


                        // Success Open Payment Page
                        purchaseFlowBegan {
                            hideLoading()
                        }

                        // Error Open Payment Page
                        failedToBeginFlow {
                            hideLoading()
                            launch(Dispatchers.Main) { application.showToast(R.string.error_payment_page_bazaar) }
                        }

                        // Success Payment
                        purchaseSucceed {
                            AppMetrica.reportEvent("S.P", mapOf(key to productId))
                            viewModelScope.launch {
                                dataStore.isVIP(Key.IS_VIP, true)
                                User.isVipUser = true
                                state.update { it.copy(successPayment = true) }
                            }

                        }


                        // Cancel Payment
                        purchaseCanceled {
                            AppMetrica.reportEvent("C.P", mapOf(key to productId))
                            hideLoading()
                        }


                        //Failed Payment
                        purchaseFailed {
                            hideLoading()
                            launch(Dispatchers.Main) { application.showToast(R.string.error_process_payment_bazaar) }
                        }

                    }

                }

                //Failed Connection To Cafe Bazaar
                connectionFailed {
                    hideLoading()
                    launch(Dispatchers.Main) { application.showToast(R.string.error_connect_bazaar, Toast.LENGTH_LONG) }
                }

                // Finish Connect to CafeBazaar
                disconnected { hideLoading() }
            }

        }
    }

    fun showLoading() = state.update { it.copy(isLoading = true) }

    fun hideLoading() = state.update { it.copy(isLoading = false) }

    fun checkSubscribeStatus() {

        state.update { it.copy(isLoading = true) }

        viewModelScope.launch(Dispatchers.IO) {

            payment.connect {

                //Success Connection To Cafe Bazaar
                connectionSucceed {
                    debug("conection success")
                    payment.getSubscribedProducts {

                        querySucceed { purchasedProducts ->
                            debug("query success")
                            state.update { it.copy(isLoading = false) }



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
                            state.update { it.copy(isLoading = false) }

                            setMessageBySnackbar(R.string.problem_connection_bazaar)
                        }
                    }
                }

                connectionFailed {
                    state.update { it.copy(isLoading = false) }
                    setMessageBySnackbar(R.string.problem_connection_bazaar)
                }


            }
        }
    }

    override fun onCleared() {
        paymentConnection?.disconnect()
        super.onCleared()
    }

}

fun String.toMonth(): Int? {
    return when (this) {
        ProductId.MONTH1 -> 1
        ProductId.MONTH3 -> 3
        ProductId.MONTH12 -> 12
        "TEST" -> 1
        else -> null
    }
}
