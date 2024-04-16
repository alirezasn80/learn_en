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
import com.alirezasn80.learn_en.utill.Key
import com.alirezasn80.learn_en.utill.User
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
import javax.inject.Inject

@HiltViewModel
class PaymentViewModel @Inject constructor(
    private val payment: Payment,
    private val application: Application,
    private val dataStore: AppDataStore,
    stateHandle: SavedStateHandle,
) : ViewModel() {
    val state = MutableStateFlow(PaymentState())
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

    override fun onCleared() {
        paymentConnection?.disconnect()
        super.onCleared()
    }

}
