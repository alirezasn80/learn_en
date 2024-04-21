package com.alirezasn80.learn_en.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp


@Composable
fun BlurLayout() {
    Box(
        Modifier
            .fillMaxSize()
            .background(Color.Black.copy(0.75f))
            .blur(0.dp, edgeTreatment = BlurredEdgeTreatment.Unbounded)
            .clickable(enabled = false) {}
            .shadow(1000.dp),
    )
}

@Composable
fun LoadingBlurLayout() {
    Box(
        Modifier
            .fillMaxSize()
            .background(Color.Black.copy(0.75f))
            .blur(0.dp, edgeTreatment = BlurredEdgeTreatment.Unbounded)
            .clickable(enabled = false) {}
            .shadow(1000.dp),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}