package com.alirezasn80.learn_en.feature.home

import android.content.Context
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
    private val appDB: AppDB
) : BaseViewModel<HomeState>(HomeState()) {


    init {
        getCategories()
    }


    fun setSelectedCategory(section: Section) = state.update { it.copy(selectedSection = section) }

    private fun getCategories() {
        viewModelScope.launch(Dispatchers.IO) {
            val items = appDB.categoryDao.getCategories()
            val categories = items.map { it.toCategoryModel(true) }
            state.update { it.copy(categories = categories) }
        }
    }

}