package com.alirezasn80.learn_en.feature.home

import androidx.lifecycle.viewModelScope
import com.alirezasn80.learn_en.core.data.database.AppDB
import com.alirezasn80.learn_en.core.domain.entity.toCategoryModel
import com.alirezasn80.learn_en.utill.BaseViewModel
import com.alirezasn80.learn_en.utill.Progress
import dagger.hilt.android.lifecycle.HiltViewModel
import io.appmetrica.analytics.AppMetrica
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val dp: AppDB
) : BaseViewModel<HomeState>(HomeState()) {

    init {
        getCategories()
    }

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

}