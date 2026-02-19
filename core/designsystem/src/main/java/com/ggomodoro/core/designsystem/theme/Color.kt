package com.ggomodoro.core.designsystem.theme

import androidx.compose.ui.graphics.Color

val ToyYellow = Color(0xFFFFC83D)
val ToyYellowDark = Color(0xFFFFC83D)

val ToyRed = Color(0xFFE53935)
val ToyRedDark = Color(0xFFE53935)

val ToyBlack = Color(0xFF2D2D2D)
val ToyWhite = Color(0xFFFFFFFF)

// Light neutrals (warm paper / cream)
val ToyBgLight = Color(0xFFFFF7EF)          // background (paper)
val ToySurfaceLight = Color(0xFFFFF1D6)     // surface (cream)
val ToySurfaceVarLight = Color(0xFFF3E6C6)  // surfaceVariant (deeper cream)
val ToyOutlineLight = Color(0xFF7A6F5A)     // outline (warm gray-brown)

// Dark neutrals (warm charcoal)
val ToyBgDark = Color(0xFF141210)           // background (warm black)
val ToySurfaceDark = Color(0xFF1C1913)      // surface
val ToySurfaceVarDark = Color(0xFF2A251C)   // surfaceVariant
val ToyOutlineDark = Color(0xFFBEB4A2)      // outline (warm light gray)

// Containers (to support Material components like FilledTonalButton, chips, etc.)
val ToyYellowContainerLight = Color(0xFFFFE6A6)
val ToyYellowContainerDark = Color(0xFF5C4500)

val ToyRedContainerLight = Color(0xFFFFDAD6)
val ToyRedContainerDark = Color(0xFF7A1B18)

// Error (Material3 expects separate roles; we align it with brand red but still give containers)
val ToyError = ToyRed
val ToyErrorContainerLight = Color(0xFFFFDAD6)
val ToyErrorContainerDark = Color(0xFF7A1B18)

// Inverse (for bottom sheets / inverse surfaces)
val ToyInverseSurfaceLight = Color(0xFF1A1816)
val ToyInverseSurfaceDark = Color(0xFFFFF2E6)
val ToyInverseOnSurfaceLight = Color(0xFFFFF2E6)
val ToyInverseOnSurfaceDark = Color(0xFF1A1816)