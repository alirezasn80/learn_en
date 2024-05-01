package com.alirezasn80.learn_en.ui.common

import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp

fun Modifier.shimmerEffect(
    backgroundHex: Long = 0xffffffff,
    shimmerHex: Long = 0xFF2D3B4D,
    enable: Boolean = true,
): Modifier = composed {

    if (enable) {
        var size by remember { mutableStateOf(IntSize.Zero) }
        val transition = rememberInfiniteTransition(label = "")
        val startOffsetX by transition.animateFloat(
            initialValue = -2 * size.width.toFloat(),
            targetValue = 2 * size.width.toFloat(),
            animationSpec = infiniteRepeatable(
                animation = tween(1500)
            ), label = "animate"
        )
        background(
            brush = Brush.linearGradient(
                colors = listOf(
                    Color(backgroundHex),
                    Color(shimmerHex),
                    Color(backgroundHex),
                ),
                start = Offset(startOffsetX, 0f),
                end = Offset(startOffsetX + size.width.toFloat(), size.height.toFloat())
            )
        ).onGloballyPositioned {
            size = it.size
        }
    } else
        Modifier

}

@Composable
fun ShimmerLayout(
    width: Any,
    height: Any,
    radius: Dp? = null,
    backgroundHex: Long = 0xffe4e4e4,
    shimmerHex: Long = 0xFFF3F2F2,
) {
    val withFloat: Float? = if (width is Float) width else null
    val withInt: Dp? = if (width is Dp) width else null
    val heightFloat: Float? = if (height is Float) height else null
    val heightInt: Dp? = if (height is Dp) height else null

    val modifier = Modifier
        .fillMaxWidth(withFloat ?: 0f)
        .width(withInt ?: 0.dp)
        .fillMaxHeight(heightFloat ?: 0f)
        .height(heightInt ?: 0.dp)
        .clip(RoundedCornerShape((if (radius == null && heightInt != null) (heightInt + 5.dp) else radius)!!))
        .shimmerEffect(backgroundHex, shimmerHex)

    Box(modifier = modifier)

}