package com.ggomodoro.core.designsystem.theme

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

// ===== Light Color Scheme =====
val ToyLightColorScheme = lightColorScheme(
    primary = ToyYellow,
    onPrimary = ToyBlack,
    primaryContainer = ToyYellowContainerLight,
    onPrimaryContainer = ToyBlack,

    secondary = ToyRed,
    onSecondary = ToyWhite,
    secondaryContainer = ToyRedContainerLight,
    onSecondaryContainer = ToyBlack,

    // Optional: tertiary (we keep it close to brand so UI stays consistent)
    tertiary = ToyYellow,
    onTertiary = ToyBlack,
    tertiaryContainer = ToyYellowContainerLight,
    onTertiaryContainer = ToyBlack,

    background = ToyBgLight,
    onBackground = ToyBlack,

    surface = ToySurfaceLight,
    onSurface = ToyBlack,
    surfaceVariant = ToySurfaceVarLight,
    onSurfaceVariant = ToyBlack,

    error = ToyError,
    onError = ToyWhite,
    errorContainer = ToyErrorContainerLight,
    onErrorContainer = ToyBlack,

    outline = ToyOutlineLight,
    outlineVariant = ToyOutlineLight.copy(alpha = 0.55f),

    inverseSurface = ToyInverseSurfaceLight,
    inverseOnSurface = ToyInverseOnSurfaceLight,
    inversePrimary = ToyYellow,

    surfaceTint = ToyYellow,
    scrim = Color(0x99000000)
)

// ===== Dark Color Scheme =====
val ToyDarkColorScheme = darkColorScheme(
    primary = ToyYellowDark,
    onPrimary = ToyBlack,
    primaryContainer = ToyYellowContainerDark,
    onPrimaryContainer = ToyWhite,

    secondary = ToyRedDark,
    onSecondary = ToyWhite,
    secondaryContainer = ToyRedContainerDark,
    onSecondaryContainer = ToyWhite,

    tertiary = ToyYellowDark,
    onTertiary = ToyBlack,
    tertiaryContainer = ToyYellowContainerDark,
    onTertiaryContainer = ToyWhite,

    background = ToyBgDark,
    onBackground = ToyWhite,

    surface = ToySurfaceDark,
    onSurface = ToyWhite,
    surfaceVariant = ToySurfaceVarDark,
    onSurfaceVariant = ToyWhite.copy(alpha = 0.90f),

    error = ToyError,
    onError = ToyWhite,
    errorContainer = ToyErrorContainerDark,
    onErrorContainer = ToyWhite,

    outline = ToyOutlineDark,
    outlineVariant = ToyOutlineDark.copy(alpha = 0.60f),

    inverseSurface = ToyInverseSurfaceDark,
    inverseOnSurface = ToyInverseOnSurfaceDark,
    inversePrimary = ToyYellowDark,

    surfaceTint = ToyYellowDark,
    scrim = Color(0xCC000000)
)
@Composable
fun GgomodoroTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false, // Disable dynamic color to stick to pastel theme
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> ToyDarkColorScheme
        else -> ToyLightColorScheme
    }
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb() // Match background
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
