package com.dzaky.githubuser.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// Neobrutalist typography typically uses more chunky, geometric sans-serif fonts
val Typography = Typography(
    displayLarge = TextStyle(
        fontSize = 48.sp,
        fontWeight = FontWeight.Black,
        letterSpacing = (-1).sp,
        lineHeight = 56.sp
    ),
    displayMedium = TextStyle(
        fontSize = 40.sp,
        fontWeight = FontWeight.Black,
        letterSpacing = (-0.5).sp,
        lineHeight = 48.sp
    ),
    displaySmall = TextStyle(
        fontSize = 32.sp,
        fontWeight = FontWeight.Bold,
        lineHeight = 40.sp
    ),
    headlineLarge = TextStyle(
        fontSize = 28.sp,
        fontWeight = FontWeight.ExtraBold,
        letterSpacing = 0.sp,
        lineHeight = 36.sp
    ),
    headlineMedium = TextStyle(
        fontSize = 24.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 0.sp,
        lineHeight = 32.sp
    ),
    headlineSmall = TextStyle(
        fontSize = 20.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 0.sp,
        lineHeight = 28.sp
    ),
    titleLarge = TextStyle(
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 0.sp,
        lineHeight = 26.sp
    ),
    titleMedium = TextStyle(
        fontSize = 16.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 0.sp,
        lineHeight = 24.sp
    ),
    titleSmall = TextStyle(
        fontSize = 14.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 0.sp,
        lineHeight = 22.sp
    ),
    bodyLarge = TextStyle(
        fontSize = 16.sp,
        fontWeight = FontWeight.Medium,
        letterSpacing = 0.sp,
        lineHeight = 24.sp
    ),
    bodyMedium = TextStyle(
        fontSize = 14.sp,
        fontWeight = FontWeight.Medium,
        letterSpacing = 0.sp,
        lineHeight = 20.sp
    ),
    bodySmall = TextStyle(
        fontSize = 12.sp,
        fontWeight = FontWeight.Medium,
        letterSpacing = 0.sp,
        lineHeight = 16.sp
    ),
    labelLarge = TextStyle(
        fontSize = 14.sp,
        fontWeight = FontWeight.ExtraBold,
        letterSpacing = 0.sp,
        lineHeight = 20.sp
    ),
    labelMedium = TextStyle(
        fontSize = 12.sp,
        fontWeight = FontWeight.ExtraBold,
        letterSpacing = 0.sp,
        lineHeight = 16.sp
    ),
    labelSmall = TextStyle(
        fontSize = 10.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 0.sp,
        lineHeight = 14.sp
    )
)