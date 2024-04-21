package com.alirezasn80.learn_en.feature.splash

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alirezasn80.learn_en.R
import com.alirezasn80.learn_en.app.navigation.NavigationState
import com.alirezasn80.learn_en.core.data.datastore.AppDataStore
import com.alirezasn80.learn_en.utill.Key
import com.alirezasn80.learn_en.utill.User
import com.alirezasn80.learn_en.utill.debug
import com.alirezasn80.learn_en.utill.isOnline
import com.alirezasn80.learn_en.utill.showToast
import dagger.hilt.android.lifecycle.HiltViewModel
import io.appmetrica.analytics.AppMetrica
import ir.cafebazaar.poolakey.Connection
import ir.cafebazaar.poolakey.Payment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val payment: Payment,
    private val dataStore: AppDataStore,
    private val application: Application
) : ViewModel() {
    private var paymentConnection: Connection? = null

    fun checkStatus(navigationState: NavigationState) {
        viewModelScope.launch(Dispatchers.IO) {

            if (!isOnline(application)) {
                withContext(Dispatchers.Main){ navigationState.navToOffline() }

            } else {
                val expireDate = dataStore.getExpireDate(Key.EXPIRE_DATE)
                val isVip = if (expireDate == null || expireDate == -1L) null else Date().before(Date(expireDate))

                debug("is vip : ${isVip}")

                when (isVip) {

                    true -> {
                        passiveCheckSubscribe()
                        User.isVipUser = true
                        withContext(Dispatchers.Main) { navigationState.navToHome() }

                    }

                    false -> checkSubscribe { viewModelScope.launch(Dispatchers.Main) { navigationState.navToHome() } }


                    null -> {
                        checkOnBoarding(navigationState)
                    }
                }
            }
        }
    }

    /*private fun checkPurchaseProduct() {

        payment.getPurchasedProducts {

            querySucceed { purchasedProducts ->
                if (purchasedProducts.isEmpty()) {
                    checkOnBoarding()
                } else {
                    viewModelScope.launch {
                        dataStore.isVIP(Key.IS_VIP, true)
                        User.isVipUser = true
                        setDestination(Destination.Home)
                    }

                }
            }

            queryFailed {
                checkOnBoarding()
            }
        }

    }*/

    private fun checkSubscribe(navToHome: () -> Unit) {

        paymentConnection = payment.connect {

            //Success Connection To Cafe Bazaar
            connectionSucceed {
                payment.getSubscribedProducts {

                    querySucceed { purchasedProducts ->
                        viewModelScope.launch {
                            if (purchasedProducts.isEmpty()) {
                                User.isVipUser = false
                                dataStore.setExpireDate(Key.EXPIRE_DATE, -1L)
                                withContext(Dispatchers.Main) {
                                    application.showToast(R.string.your_subscribe_is_finished)
                                    navToHome()
                                }
                            } else {
                                viewModelScope.launch {
                                    AppMetrica.reportError("local vip is false but bazaar is true!", "SubscribeError")
                                    User.isVipUser = true
                                    navToHome()
                                }
                            }
                        }


                    }

                    queryFailed {

                        navToHome()
                    }
                }
            }

            connectionFailed {
                navToHome()
            }

            disconnected {
                navToHome()
            }


        }

    }

    private suspend fun passiveCheckSubscribe() {
        debug("here")
        GlobalScope.launch(Dispatchers.IO) {
            paymentConnection = payment.connect {
                debug("connect")


                //Success Connection To Cafe Bazaar
                connectionSucceed {
                    payment.getSubscribedProducts {

                        querySucceed { purchasedProducts ->

                            if (purchasedProducts.isEmpty()) {
                                User.isVipUser = false
                                debug("set it as false")
                                GlobalScope.launch(Dispatchers.IO) { dataStore.setExpireDate(Key.EXPIRE_DATE, -1L) }

                            } else {
                                debug("set it as true")

                                User.isVipUser = true
                            }


                        }

                        queryFailed {}
                    }
                }

                connectionFailed {}

                disconnected {}


            }
        }

    }

    private fun checkOnBoarding(navigationState: NavigationState) {

        viewModelScope.launch(Dispatchers.IO) {
            val showOnBoarding = dataStore.showOnboarding(Key.ONBOARDING)

            if (showOnBoarding) {
                withContext(Dispatchers.Main) { navigationState.navToOnBoarding() }

            } else {
                withContext(Dispatchers.Main) {
                    navigationState.navToHome()
                }
            }
        }


    }


}