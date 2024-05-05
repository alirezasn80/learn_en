package com.alirezasn80.learn_en.ui.theme

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alirezasn80.learn_en.core.data.datastore.AppDataStore
import com.alirezasn80.learn_en.utill.Key
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ThemeViewModel @Inject constructor(
    private val dataStore: AppDataStore
) : ViewModel() {

    val isDarkTheme = mutableStateOf(false)

    init {
        viewModelScope.launch {
            val isDark = dataStore.isDarkTheme(Key.IS_DARK_THEME)
            isDarkTheme.value = isDark
        }
    }


    fun toggleTheme() {
        viewModelScope.launch {
            isDarkTheme.value = !isDarkTheme.value
            dataStore.isDarkTheme(Key.IS_DARK_THEME, isDarkTheme.value)
        }

    }

}