package moe.pyropix.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable

private val LightColors = lightColorScheme(
    primary = Brand,
    onPrimary = SurfaceLight,
    secondary = Secondary,
    onSecondary = SurfaceLight,
    surface = SurfaceLight,
    onSurface = OnSurfaceLight,
    background = SurfaceLight,
    onBackground = OnSurfaceLight
)

private val DarkColors = darkColorScheme(
    primary = Brand,
    onPrimary = SurfaceDark,
    secondary = SecondaryDark,
    onSecondary = SurfaceDark,
    surface = SurfaceDark,
    onSurface = OnSurfaceDark,
    background = SurfaceDark,
    onBackground = OnSurfaceDark
)

@Composable
fun PyroTheme(
    dark: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (dark) DarkColors else LightColors,
        typography = PyroTypo,
        content = content
    )
}
