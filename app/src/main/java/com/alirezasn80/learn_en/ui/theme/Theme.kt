package com.alirezasn80.learn_en.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xff5ea3de),
    secondary = BlueGrayDrawer,
    background = BlueGrayBackground,
    onBackground = PurpleOnBackground,
    onPrimary = Color.White,
    onSecondary = Color.White,
    surface = Color(0xff222e3a),
    onSurface = PurpleOnBackground
)

private val LightColorScheme = lightColorScheme(
    primary = OrangeTopBar,
    secondary = OrangeDrawer,
    background = Color(0xfffbfbfb),
    onBackground = BlackOnBackground,
    onPrimary = Color.White,
    onSecondary = Color.White,
    surface = Color.White,
    onSurface = BlackOnBackground
)

@Composable
fun Learn_enTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = DarkColorScheme
    /* when {
     dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
         val context = LocalContext.current
         if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
     }

     darkTheme -> DarkColorScheme
     else -> LightColorScheme
 }*/
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