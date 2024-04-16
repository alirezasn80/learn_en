package com.alirezasn80.learn_en.feature.payment

sealed interface Status {

    data object Loading : Status

    data object Connected : Status

    data object Idle : Status

    data object Failed : Status
}


data class PaymentState(
    val isLoading: Boolean = false,
    val successPayment: Boolean = false,
)
