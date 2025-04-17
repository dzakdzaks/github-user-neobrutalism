package com.dzaky.githubuser.ui.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.dzaky.githubuser.ui.theme.NeoBrutalYellow

/**
 * A reusable loading state component that shows a circular progress indicator with a message.
 *
 * @param message The message to display
 * @param centerInParent Whether to center the loading indicator in its parent
 * @param modifier Additional modifier to apply
 */
@Composable
fun LoadingState(
    message: String = "Loading...",
    centerInParent: Boolean = true,
    modifier: Modifier = Modifier
) {
    val contentModifier = if (centerInParent) {
        Modifier.fillMaxSize()
    } else {
        Modifier.fillMaxWidth()
    }

    Box(
        modifier = contentModifier.then(modifier),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(16.dp)
        ) {
            CircularProgressIndicator(
                color = NeoBrutalYellow,
                strokeWidth = 4.dp,
                modifier = Modifier.size(48.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = message,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

/**
 * A reusable error state component that shows an error message with an option to retry.
 *
 * @param message The error message to display
 * @param onRetry Optional callback for retry action
 * @param icon Optional icon to display with the error message
 * @param modifier Additional modifier to apply
 */
@Composable
fun ErrorState(
    message: String,
    onRetry: (() -> Unit)? = null,
    icon: ImageVector = Icons.Default.Warning,
    modifier: Modifier = Modifier
) {
    NeoBrutalErrorState(
        message = message,
        icon = icon,
        modifier = modifier.fillMaxWidth().padding(16.dp),
        onRetry = onRetry
    )
}