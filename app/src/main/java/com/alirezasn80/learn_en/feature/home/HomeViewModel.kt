package com.alirezasn80.learn_en.feature.home

import androidx.lifecycle.viewModelScope
import com.alirezasn80.learn_en.core.data.database.AppDB
import com.alirezasn80.learn_en.core.domain.entity.toCategoryModel
import com.alirezasn80.learn_en.utill.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
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
        if (section == state.value.selectedSection) return

        if (section.key == "favorite") {
            getFavorites(section)
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            val categories = dp.categoryDao.getCategories(section.key).map { it.toCategoryModel(true) }
            state.update { it.copy(selectedSection = section, categories = categories, favorites = emptyList()) }
        }

    }

    private fun getFavorites(section: Section) {
        viewModelScope.launch(Dispatchers.IO) {
            val favorites = dp.contentDao.getFavorites()
            state.update { it.copy(favorites = favorites, selectedSection = section) }
        }
    }

    private fun getCategories() {
        viewModelScope.launch(Dispatchers.IO) {
            val items = dp.categoryDao.getCategories()
            val categories = items.map { it.toCategoryModel(true) }
            state.update { it.copy(categories = categories) }
        }
    }

}