package com.alirezasn80.learn_en.feature.splash

import android.app.Application
import android.os.Build
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alirezasn80.learn_en.core.data.datastore.AppDataStore
import com.alirezasn80.learn_en.utill.BaseViewModel
import com.alirezasn80.learn_en.utill.Destination
import com.alirezasn80.learn_en.utill.Key
import com.alirezasn80.learn_en.utill.User
import com.alirezasn80.learn_en.utill.isOnline
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.cafebazaar.poolakey.Connection
import ir.cafebazaar.poolakey.Payment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val payment: Payment,
    private val dataStore: AppDataStore,
    private val application: Application
) : BaseViewModel<SplashState>(SplashState()) {
    private var paymentConnection: Connection? = null

    init {

        viewModelScope.launch {
            delay(100) // should be here for offline mode
            val isVip = dataStore.isVIP(Key.IS_VIP)

            when (isVip) {

                true -> {
                    User.isVipUser = true
                    setDestination(Destination.Home)
                }

                false -> {

                    if (isOnline(application)) {
                        setDestination(Destination.Home)
                    } else {
                        setDestination(Destination.Offline)
                    }

                }

                null -> {

                    if (isOnline(application)) {
                        connectBazaar()
                    } else {
                        setDestination(Destination.Offline)
                    }

                }
            }
        }


    }

    private fun checkSubscribedProduct() {


        payment.getSubscribedProducts {

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
    }

    private fun checkPurchaseProduct() {

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

    }

    private fun connectBazaar() {
        paymentConnection = payment.connect {

            //Success Connection To Cafe Bazaar
            connectionSucceed {
                checkPurchaseProduct()
            }

            connectionFailed {
                checkOnBoarding()
            }

            disconnected {
                checkOnBoarding()
            }


        }

    }

    private fun checkOnBoarding() {
        viewModelScope.launch {
            dataStore.isVIP(Key.IS_VIP, false)
            val showOnBoarding = dataStore.showOnboarding(Key.ONBOARDING)
            if (showOnBoarding)
                setDestination(Destination.OnBoarding)
            else
                setDestination(Destination.Home)
        }

    }


}