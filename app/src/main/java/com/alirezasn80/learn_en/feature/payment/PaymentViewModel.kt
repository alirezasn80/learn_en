package com.alirezasn80.learn_en.feature.payment

import android.app.Activity
import android.app.Application
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.alirezasn80.learn_en.R
import com.alirezasn80.learn_en.core.data.datastore.AppDataStore
import com.alirezasn80.learn_en.utill.Arg
import com.alirezasn80.learn_en.utill.BaseViewModel
import com.alirezasn80.learn_en.utill.Key
import com.alirezasn80.learn_en.utill.Myket
import com.alirezasn80.learn_en.utill.User
import com.alirezasn80.learn_en.utill.debug
import com.alirezasn80.learn_en.utill.getString
import com.alirezasn80.learn_en.utill.showToast
import dagger.hilt.android.lifecycle.HiltViewModel
import io.appmetrica.analytics.AppMetrica
import ir.myket.billingclient.IabHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class PaymentViewModel @Inject constructor(
    private val payment: IabHelper,
    private val application: Application,
    private val dataStore: AppDataStore,
    stateHandle: SavedStateHandle,
) : BaseViewModel<PaymentState>(PaymentState()) {
    //private var paymentConnection: Connection? = null
    private val key = stateHandle.getString(Arg.Key) ?: ""

    init {
        AppMetrica.reportEvent("O.P", mapOf(key to "Open"))
    }

    private fun connectMyket(activity: Activity) {
        payment.startSetup { result ->
            if (result.isSuccess) {
                Myket.alreadyConnection = true
                debug("success connect to myket")
                buyProduct(activity, "vip")

            } else {
                debug("failed connect to myket")
                setMessageByToast(R.string.error_connect_bazaar)
            }
        }

    }


    fun buyProduct(activity: Activity, productId: String) {


        // showLoading()

        viewModelScope.launch(Dispatchers.IO) {

            if (Myket.alreadyConnection) {

                try {
                    payment.launchPurchaseFlow(
                        activity,
                        productId
                    ) { result, info ->
                        debug("result  : ${result.toString()}")
                        debug("info  : ${info.toString()}")

                        if (result.isSuccess) {
                            AppMetrica.reportEvent("S.P", mapOf(key to productId))
                            viewModelScope.launch {
                                dataStore.isVIP(Key.IS_VIP, true)
                                User.isVipUser = true
                                state.update { it.copy(successPayment = true) }
                            }
                        } else {
                            launch(Dispatchers.Main) { application.showToast(R.string.error_payment_page_bazaar) }
                        }
                    }
                } catch (e: Exception) {
                    debug("error : ${e.message}")
                }

            } else {
                connectMyket(activity)
            }
        }
    }

    fun showLoading() = state.update { it.copy(isLoading = true) }

    fun hideLoading() = state.update { it.copy(isLoading = false) }


}
