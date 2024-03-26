package com.alirezasn80.learn_en.ui.common

import androidx.compose.animation.ContentTransform
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith

object Animation {

    val SLIDE_IN_VERTICALLY = slideInVertically(
        initialOffsetY = { -it },
        animationSpec = tween(durationMillis = 500)
    )

    val SLIDE_OUT_VERTICALLY = slideOutVertically(
        targetOffsetY = { -it },
        animationSpec = tween(durationMillis = 500)
    )

    val SLIDE_IN_HORIZONTALLY = slideInHorizontally(
        initialOffsetX = { it },
        animationSpec = tween(durationMillis = 500)
    )

    val SLIDE_IN_HORIZONTALLY_REVERSE = slideInHorizontally(
        initialOffsetX = { -it },
        animationSpec = tween(durationMillis = 500)
    )

    val SLIDE_OUT_HORIZONTALLY = slideOutHorizontally(
        targetOffsetX = { it },
        animationSpec = tween(durationMillis = 50)
    )

    val SLIDE_OUT_HORIZONTALLY_REVERSE = slideOutHorizontally(
        targetOffsetX = { -it },
        animationSpec = tween(durationMillis = 50)
    )


}

@ExperimentalAnimationApi
fun numberSelectionAnimate(isIncrease: Boolean, duration: Int = 200): ContentTransform {
    return (
            slideInVertically(animationSpec = tween(durationMillis = duration)) { if (isIncrease) -it else it } +
                    fadeIn(animationSpec = tween(durationMillis = duration))
            ) togetherWith (
            slideOutVertically(animationSpec = tween(durationMillis = duration)) { if (isIncrease) it else -it } +
                    fadeOut(animationSpec = tween(durationMillis = duration))
            )
}




