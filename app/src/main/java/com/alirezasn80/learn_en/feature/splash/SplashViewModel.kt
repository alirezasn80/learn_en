package com.alirezasn80.learn_en.feature.splash

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alirezasn80.learn_en.core.data.datastore.AppDataStore
import com.alirezasn80.learn_en.utill.Destination
import com.alirezasn80.learn_en.utill.Key
import com.alirezasn80.learn_en.utill.User
import com.alirezasn80.learn_en.utill.debug
import com.alirezasn80.learn_en.utill.isOnline
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val dataStore: AppDataStore,
    private val application: Application,

    ) : ViewModel() {
    val destination = MutableStateFlow<Destination?>(null)


    init {
        viewModelScope.launch(Dispatchers.IO) {
            delay(500)

            if (!isOnline(application)) {
                setDestination(Destination.Offline)
                return@launch
            }

            val isVip = dataStore.isVIP(Key.IS_VIP)
            debug("is vip : $isVip")

            when (isVip) {

                true -> {
                    User.isVipUser = true
                    setDestination(Destination.Home)
                }

                false, null -> {
                    User.isVipUser = false//if (DEBUG) true else false
                    checkOnBoarding()
                }

            }
        }
    }


    private fun setDestination(dest: Destination?) {
        destination.update { dest }
    }

    private suspend fun checkOnBoarding() {
        dataStore.isVIP(Key.IS_VIP, false)
        val showOnBoarding = dataStore.showOnboarding(Key.ONBOARDING)

        if (showOnBoarding) {
            setDestination(Destination.OnBoarding)
        } else {
            setDestination(Destination.Home)
        }


    }
}