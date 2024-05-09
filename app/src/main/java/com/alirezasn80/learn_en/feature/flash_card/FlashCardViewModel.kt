package com.alirezasn80.learn_en.feature.flash_card

import com.alirezasn80.learn_en.utill.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class FlashCardViewModel @Inject constructor(
) : BaseViewModel<FlashCardState>(FlashCardState()) {

}