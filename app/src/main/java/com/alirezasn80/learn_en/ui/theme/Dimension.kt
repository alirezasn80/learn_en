package com.alirezasn80.learn_en.ui.theme

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

data class Dimension(
    val extraSmall: Dp = 4.dp,
    val small: Dp = 8.dp,
    val medium: Dp = 16.dp,
    val large: Dp = 26.dp,
    val extraLarge: Dp = 64.dp,
)


val LocalSpacing = compositionLocalOf {
    Dimension(4.dp, 8.dp, 16.dp, 32.dp, 64.dp)
}


val dimension: Dimension
    @ReadOnlyComposable
    @Composable
    get() = LocalSpacing.current

@Composable
fun ColumnScope.ExtraSmallSpacer() = Spacer(modifier = Modifier.height(4.dp))

@Composable
fun ColumnScope.SmallSpacer() = Spacer(modifier = Modifier.height(8.dp))

@Composable
fun ColumnScope.MediumSpacer() = Spacer(modifier = Modifier.height(16.dp))

@Composable
fun ColumnScope.LargeSpacer() = Spacer(modifier = Modifier.height(32.dp))

@Composable
fun ColumnScope.ExtraLargeSpacer() = Spacer(modifier = Modifier.height(90.dp))

@Composable
fun RowScope.ExtraSmallSpacer() = Spacer(modifier = Modifier.width(4.dp))

@Composable
fun RowScope.SmallSpacer() = Spacer(modifier = Modifier.width(8.dp))

@Composable
fun RowScope.MediumSpacer() = Spacer(modifier = Modifier.width(16.dp))

@Composable
fun RowScope.LargeSpacer() = Spacer(modifier = Modifier.width(32.dp))

@Composable
fun RowScope.ExtraLargeSpacer() = Spacer(modifier = Modifier.width(90.dp))

//------------------------------------ Divider

@Composable
fun ColumnScope.Line(padding: Dp = 0.dp, thickness: Dp = 1.dp,color: Color = MaterialTheme.colorScheme.background) = Divider(
    modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = padding),
    color = color,
    thickness = thickness
)

@Composable
fun RowScope.Line(padding: Dp = 0.dp, thickness: Dp = 1.dp,color: Color = MaterialTheme.colorScheme.background) = Divider(
    modifier = Modifier
        .fillMaxHeight()
        .padding(horizontal = padding),
    color =color,
    thickness = thickness
)