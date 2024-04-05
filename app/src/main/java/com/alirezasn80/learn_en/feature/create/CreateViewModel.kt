package com.alirezasn80.learn_en.feature.create

import com.alirezasn80.learn_en.utill.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class CreateViewModel @Inject constructor(
) : BaseViewModel<CreateState>(CreateState()) {

    fun onTitleChange(value: String) = state.update { it.copy(title = value) }
    fun onContentChange(value: String) = state.update { it.copy(content = value) }

}