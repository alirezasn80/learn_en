package com.alirezasn80.learn_en.feature.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alirezasn80.learn_en.core.data.datastore.AppDataStore
import com.alirezasn80.learn_en.utill.Key
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnBoardingViewModel @Inject constructor(
    private val dataStore: AppDataStore
) : ViewModel() {

    fun hideOnBoarding() {
        viewModelScope.launch {
            dataStore.showOnboarding(Key.ONBOARDING, false)
        }
    }

}