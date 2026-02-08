package com.jeripurnama.pentaword.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = Gold,
    secondary = GoldDark,
    tertiary = LightGray,
    background = BackgroundDark,
    surface = SurfaceDark,
    onPrimary = TextBlack,
    onSecondary = TextBlack,
    onTertiary = TextBlack,
    onBackground = White,
    onSurface = White
)

private val LightColorScheme = lightColorScheme(
    primary = Gold,
    secondary = GoldDark,
    tertiary = DarkGray,
    background = Background,
    surface = White,
    onPrimary = TextBlack,
    onSecondary = TextBlack,
    onTertiary = White,
    onBackground = TextBlack,
    onSurface = TextBlack
)

@Composable
fun PentawordTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
