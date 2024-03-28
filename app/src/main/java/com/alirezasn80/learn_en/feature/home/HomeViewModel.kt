package com.alirezasn80.learn_en.feature.home

import androidx.lifecycle.viewModelScope
import com.alirezasn80.learn_en.core.data.database.AppDB
import com.alirezasn80.learn_en.core.domain.entity.CategoryEntity
import com.alirezasn80.learn_en.core.domain.entity.ContentEntity
import com.alirezasn80.learn_en.utill.BaseViewModel
import com.alirezasn80.learn_en.utill.debug
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
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

    fun setSelectedCategory(category: Category) = state.update { it.copy(selectedCategory = category) }

    private fun getCategories() {
        viewModelScope.launch {
            val items = appDB.categoryDao.getCategories()
            debug("items : ${items.toString()}")
            state.update { it.copy(categories = items) }
        }
    }

}