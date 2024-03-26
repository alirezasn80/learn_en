package com.alirezasn80.learn_en.feature.home

import com.alirezasn80.learn_en.utill.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
) : BaseViewModel<HomeState>(HomeState()) {


}