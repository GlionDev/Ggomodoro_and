package com.ggomodoro.core.designsystem.theme

import androidx.compose.ui.graphics.Color

val PastelRed = Color(0xFFFFB3B3)
val PastelRedDark = Color(0xFFE57373)
val PastelYellow = Color(0xFFFFF9C4)
val PastelYellowDark = Color(0xFFFFF176)
val White = Color(0xFFFFFFFF)
val Black = Color(0xFF000000)
val Gray900 = Color(0xFF212121)
val Gray100 = Color(0xFFF5F5F5)

// Light Theme Colors
val LightPrimary = PastelRed
val LightOnPrimary = Black
val LightSecondary = PastelYellow
val LightOnSecondary = Black
val LightBackground = White
val LightOnBackground = Black
val LightSurface = Gray100
val LightOnSurface = Black

// Dark Theme Colors (Pastel themes can look good in dark mode too, maybe inverted or muted)
// For now, let's keep it simple, maybe a dark red/yellow?
val DarkPrimary = PastelRedDark
val DarkOnPrimary = White
val DarkSecondary = PastelYellowDark
val DarkOnSecondary = Black
val DarkBackground = Gray900
val DarkOnBackground = White
val DarkSurface = Color(0xFF303030)
val DarkOnSurface = White
