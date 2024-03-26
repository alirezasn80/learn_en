package com.alirezasn80.learn_en.utill

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun rememberWindowSize(): WindowSize {
    val configuration = LocalConfiguration.current
    return WindowSize(
        widthType = when (configuration.screenWidthDp) {
            in 0..600 -> WindowSize.WindowType.Compact
            in 600..840 -> WindowSize.WindowType.Medium
            else -> WindowSize.WindowType.Expanded
        },
        heightType = when (configuration.screenHeightDp) {
            in 0..480 -> WindowSize.WindowType.Compact
            in 480..900 -> WindowSize.WindowType.Medium
            else -> WindowSize.WindowType.Expanded
        },
        width = configuration.screenWidthDp.dp,
        height = configuration.screenHeightDp.dp
    )
}

data class WindowSize(
    val widthType: WindowType,
    val heightType: WindowType,
    val width: Dp,
    val height: Dp
) {
    sealed class WindowType {
        object Compact : WindowType()
        object Medium : WindowType()
        object Expanded : WindowType()
    }
}
