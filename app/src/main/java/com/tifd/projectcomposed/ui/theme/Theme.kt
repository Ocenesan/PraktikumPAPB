package com.tifd.projectcomposed.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40,
    onPrimary = Color.White,
    onSecondary = Color.White,
    error = Red80,
    surface = Color.Black,
    onSurface = Color.White,
    background = PurpleGrey40,
    onBackground = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = Pink80,
    secondary = PurpleGrey80,
    tertiary = Purple80,
    onPrimary = Color.Black,
    onSecondary = Color.White,
    error = Red80,
    surface = Color.White,
    onSurface = Color.Black,
    background = Pink80,
    onBackground = Color.Black
)

@Composable
fun ProjectComposeDTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}