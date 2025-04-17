package com.dzaky.githubuser.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.unit.dp

// Enhanced properties for richer neobrutalism style
data class NeoBrutalProperties(
    val borderWidth: Int = 3,
    val shadowOffset: Int = 4,
    val cornerRadius: Int = 6,  // Slightly increased for a softer look
    val itemSpacing: Int = 16,
    val contentPadding: Int = 12
)

// Creating a CompositionLocal for the neobrutalist properties
val LocalNeoBrutalProperties = compositionLocalOf { NeoBrutalProperties() }

private val ColorScheme = lightColorScheme(
    primary = NeoBrutalYellow,
    secondary = NeoBrutalPink,
    tertiary = NeoBrutalBlue,
    background = OffWhite,
    surface = White,
    onPrimary = Black,
    onSecondary = Black,
    onTertiary = Black,
    onBackground = Black,
    onSurface = Black,
    error = NeoBrutalRed,
    onError = White,
    // Additional colors for a richer palette
    surfaceVariant = LightYellow,
    secondaryContainer = LightPink,
    tertiaryContainer = LightBlue
)

@Composable
fun GithubUserTheme(
    content: @Composable () -> Unit
) {
    // Provide neobrutalist properties
    CompositionLocalProvider(
        LocalNeoBrutalProperties provides NeoBrutalProperties()
    ) {
        MaterialTheme(
            colorScheme = ColorScheme,
            typography = Typography,
            shapes = MaterialTheme.shapes.copy(
                small = RoundedCornerShape(6.dp),
                medium = RoundedCornerShape(6.dp),
                large = RoundedCornerShape(6.dp)
            ),
            content = content
        )
    }
}