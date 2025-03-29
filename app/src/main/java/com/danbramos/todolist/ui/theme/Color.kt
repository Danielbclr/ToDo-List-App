package com.danbramos.todolist.ui.theme

import androidx.compose.ui.graphics.Color

// Light Theme Colors
val LightModeBg = Color(0xFFFFF4D1) // Pale orange/off-white background
val LightModeSurface = Color(0xFFFFEDB5) // Slightly more saturated version for surface

// Dark Theme Colors
val DarkModeBg = Color(0xFF0B091F) // Very dark blue background
val DarkModeSurface = Color(0xFF181548) // Slightly lighter dark blue for surface

// Light theme priority colors (yellow → orange → red gradient)
val lowPriorityColorLight = Color(0xE6FFB400) // Yellow
val medPriorityColorLight = Color(0xE6FF6700) // Light orange
val highPriorityColorLight = Color(0xE6D11C00) // Orange
val topPriorityColorLight = Color(0xE6760000) // Dark orange-red

// Dark theme priority colors (turquoise → blue gradient)
val lowPriorityColorDark = Color(0xFF181548)  // Light turquoise
val medPriorityColorDark = Color(0xFF482E6F)  // Medium blue
val highPriorityColorDark = Color(0xFF813A7E) // Dark blue
val topPriorityColorDark = Color(0xFFBE5381) // Very dark blue

// FAB Colors
val fabBackgroundLight = Color(0xE6FF6700) // Orange
val fabContentLight = Color(0xFFFFF4D1)

val fabBackgroundDark = Color(0xFF5C5C99) // Medium blue
val fabContentDark = Color.White

// Priority Chip Colors
val priorityChipSelectedLight = Color(0xFFE65100) // Dark orange for light theme
val priorityChipUnselectedLight = Color(0xFFFFECB3) // Light orange/yellow background
val priorityChipTextSelectedLight = Color.White
val priorityChipTextUnselectedLight = Color(0xFF5D4037) // Brown text

val priorityChipSelectedDark = Color(0xFF1A237E) // Deep blue for dark theme
val priorityChipUnselectedDark = Color(0xFF263238) // Dark blue-gray background
val priorityChipTextSelectedDark = Color.White
val priorityChipTextUnselectedDark = Color(0xFFB0BEC5) // Light blue-gray text