package com.alirezasn80.learn_en.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF5B92B0),
    secondary = Color(0xFF5EB0DC),
    background = Color(0xFF1d2733),
    onBackground = Color(0xFFF9FCFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    surface = Color(0xFF242e38),
    onSurface = Color(0xFFF9FCFE)
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF5B92B0),
    secondary = Color(0xFF5EB0DC),
    background = Color(0xFFF1F1F1),
    onBackground = Color(0xFF171717),
    onPrimary = Color.White,
    onSecondary = Color.White,
    surface = Color.White,
    onSurface = Color(0xFF171717)
)

@Composable
fun Learn_enTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        /* dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
             val context = LocalContext.current
             if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
         }*/

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}