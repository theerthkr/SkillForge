package com.theerthkr.skillforge.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color


private val LightColorScheme = lightColorScheme(
    primary = DarkTealPrimary,
    secondary = LightTealSecondary,
    background = CreamBackground,
    surface = CreamBackground,

    // Optional: Define contrasting text colors for readability
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = Color.Black,
    onSurface = Color.Black
)

@Composable
fun SkillForgeTheme(
    // Removed the darkTheme boolean parameter to strictly enforce light mode
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = Typography,
        content = content
    )
}