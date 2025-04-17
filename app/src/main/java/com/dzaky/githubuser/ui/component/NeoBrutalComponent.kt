package com.dzaky.githubuser.ui.theme

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

/**
 * A Neobrutalist Card with chunky border, offset shadow
 */
@Composable
fun NeoBrutalCard(
    modifier: Modifier = Modifier,
    backgroundColor: Color = MaterialTheme.colorScheme.surface,
    borderColor: Color = MaterialTheme.colorScheme.onSurface,
    content: @Composable () -> Unit
) {
    val properties = LocalNeoBrutalProperties.current

    Box(modifier = modifier) {
        // Create a nested Box to hold both the shadow and content
        Box {
            // Shadow/offset effect - now tied to the content size
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .offset(
                        x = properties.shadowOffset.dp,
                        y = properties.shadowOffset.dp
                    )
                    .background(
                        color = borderColor,
                        shape = RoundedCornerShape(properties.cornerRadius.dp)
                    )
            )

            // Main content with border
            Box(
                modifier = Modifier
                    .background(
                        color = backgroundColor,
                        shape = RoundedCornerShape(properties.cornerRadius.dp)
                    )
                    .border(
                        width = properties.borderWidth.dp,
                        color = borderColor,
                        shape = RoundedCornerShape(properties.cornerRadius.dp)
                    )
                    .padding(properties.contentPadding.dp)
            ) {
                content()
            }
        }
    }
}

/**
 * A Neobrutalist Button with chunky border and interactive press animation
 */
@Composable
fun NeoBrutalButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    backgroundColor: Color = MaterialTheme.colorScheme.primary,
    borderColor: Color = MaterialTheme.colorScheme.onPrimary,
    contentColor: Color = MaterialTheme.colorScheme.onPrimary,
    content: @Composable () -> Unit
) {
    val properties = LocalNeoBrutalProperties.current
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    // Animate shadow offset based on press state
    val offsetX by animateDpAsState(
        targetValue = if (isPressed) 0.dp else properties.shadowOffset.dp,
        animationSpec = tween(durationMillis = 100),
        label = "offsetX"
    )

    val offsetY by animateDpAsState(
        targetValue = if (isPressed) 0.dp else properties.shadowOffset.dp,
        animationSpec = tween(durationMillis = 100),
        label = "offsetY"
    )

    Box(modifier = modifier) {
        // The important change: wrap both the shadow and the button content in their own Box
        Box(modifier = Modifier) {
            // Shadow/offset effect
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .offset(x = offsetX, y = offsetY)
                    .background(
                        color = borderColor,
                        shape = RoundedCornerShape(properties.cornerRadius.dp)
                    )
            )

            // Main button with border
            Box(
                modifier = Modifier
                    .clickable(
                        interactionSource = interactionSource,
                        indication = null,
                        onClick = onClick
                    )
                    .background(
                        color = backgroundColor,
                        shape = RoundedCornerShape(properties.cornerRadius.dp)
                    )
                    .border(
                        width = properties.borderWidth.dp,
                        color = borderColor,
                        shape = RoundedCornerShape(properties.cornerRadius.dp)
                    )
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                content()
            }
        }
    }
}

/**
 * A Neobrutalist TextField with chunky border
 */
@Composable
fun NeoBrutalTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: @Composable (() -> Unit)? = null,
    placeholder: @Composable (() -> Unit)? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    textStyle: TextStyle = MaterialTheme.typography.bodyLarge,
    backgroundColor: Color = MaterialTheme.colorScheme.surface,
    borderColor: Color = MaterialTheme.colorScheme.onSurface
) {
    val properties = LocalNeoBrutalProperties.current

    // Wrap the entire component in a Box with the provided modifier
    Box(modifier = modifier) {
        // Create a Box that will hold both the shadow and the TextField
        Box(modifier = Modifier.fillMaxWidth()) {
            // Shadow/offset effect - now tied to the TextField size, not parent modifier
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .offset(
                        x = properties.shadowOffset.dp,
                        y = properties.shadowOffset.dp
                    )
                    .background(
                        color = borderColor,
                        shape = RoundedCornerShape(properties.cornerRadius.dp)
                    )
            )

            // Textfield
            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                textStyle = textStyle,
                label = label,
                placeholder = placeholder,
                leadingIcon = leadingIcon,
                modifier = Modifier
                    .fillMaxWidth()  // Make the TextField fill the Box
                    .background(
                        color = backgroundColor,
                        shape = RoundedCornerShape(properties.cornerRadius.dp)
                    ),
                shape = RoundedCornerShape(properties.cornerRadius.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = borderColor,
                    unfocusedBorderColor = borderColor,
                    focusedTextColor = MaterialTheme.colorScheme.onSurface,
                    unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                    cursorColor = MaterialTheme.colorScheme.onSurface,
                    focusedLabelColor = MaterialTheme.colorScheme.onSurface,
                    unfocusedLabelColor = MaterialTheme.colorScheme.onSurface,
                    focusedContainerColor = backgroundColor,
                    unfocusedContainerColor = backgroundColor
                ),
                keyboardOptions = keyboardOptions,
                keyboardActions = keyboardActions
            )
        }
    }
}

/**
 * A Neobrutalist list item with border and interaction animation
 */
@Composable
fun NeoBrutalListItem(
    modifier: Modifier = Modifier,
    backgroundColor: Color = MaterialTheme.colorScheme.surface,
    borderColor: Color = MaterialTheme.colorScheme.onSurface,
    onClick: (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    val properties = LocalNeoBrutalProperties.current
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    // Animate shadow offset based on press state
    val offsetX by animateDpAsState(
        targetValue = if (isPressed && onClick != null) 0.dp else (properties.shadowOffset / 2).dp,
        animationSpec = tween(100),
        label = "offsetX"
    )

    val offsetY by animateDpAsState(
        targetValue = if (isPressed && onClick != null) 0.dp else (properties.shadowOffset / 2).dp,
        animationSpec = tween(100),
        label = "offsetY"
    )

    // Add scale animation for better feedback
    val scale by animateFloatAsState(
        targetValue = if (isPressed && onClick != null) 0.98f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "scale"
    )

    val clickModifier = if (onClick != null) {
        Modifier.clickable(
            interactionSource = interactionSource,
            indication = null
        ) { onClick() }
    } else {
        Modifier
    }

    Box(
        modifier = modifier
            .scale(scale)  // Apply scale animation to the entire component
    ) {
        // Shadow/offset effect
        Box(
            modifier = Modifier
                .matchParentSize()
                .offset(x = offsetX, y = offsetY)
                .background(
                    color = borderColor,
                    shape = RoundedCornerShape(properties.cornerRadius.dp)
                )
        )

        // Main content with border
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .then(clickModifier)
                .background(
                    color = backgroundColor,
                    shape = RoundedCornerShape(properties.cornerRadius.dp)
                )
                .border(
                    width = properties.borderWidth.dp,
                    color = borderColor,
                    shape = RoundedCornerShape(properties.cornerRadius.dp)
                )
                .padding(properties.contentPadding.dp)
        ) {
            content()
        }
    }
}

/**
 * A Neobrutalist empty state component with icon and text
 */
@Composable
fun NeoBrutalEmptyState(
    message: String,
    icon: ImageVector? = null,
    modifier: Modifier = Modifier,
    backgroundColor: Color = LightYellow,
    borderColor: Color = Black,
    contentColor: Color = Black,
    secondaryMessage: String? = null
) {
    val properties = LocalNeoBrutalProperties.current

    NeoBrutalCard(
        modifier = modifier,
        backgroundColor = backgroundColor,
        borderColor = borderColor
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = contentColor,
                    modifier = Modifier
                        .padding(bottom = 16.dp)
                        .size(48.dp)
                )
            }

            Text(
                text = message,
                style = MaterialTheme.typography.titleMedium,
                color = contentColor,
                textAlign = TextAlign.Center
            )

            if (secondaryMessage != null) {
                Text(
                    text = secondaryMessage,
                    style = MaterialTheme.typography.bodyMedium,
                    color = contentColor.copy(alpha = 0.7f),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    }
}

/**
 * A Neobrutalist error state component with icon and text
 */
@Composable
fun NeoBrutalErrorState(
    message: String,
    icon: ImageVector? = null,
    modifier: Modifier = Modifier,
    backgroundColor: Color = LightPink,
    borderColor: Color = NeoBrutalRed,
    contentColor: Color = NeoBrutalRed,
    onRetry: (() -> Unit)? = null
) {
    val properties = LocalNeoBrutalProperties.current

    NeoBrutalCard(
        modifier = modifier,
        backgroundColor = backgroundColor,
        borderColor = borderColor
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = contentColor,
                    modifier = Modifier
                        .padding(bottom = 16.dp)
                        .size(48.dp)
                )
            }

            Text(
                text = message,
                style = MaterialTheme.typography.titleMedium,
                color = contentColor,
                textAlign = TextAlign.Center
            )

            if (onRetry != null) {
                NeoBrutalButton(
                    onClick = onRetry,
                    backgroundColor = NeoBrutalRed.copy(alpha = 0.9f),
                    borderColor = Black,
                    modifier = Modifier.padding(top = 16.dp)
                ) {
                    Text(
                        "Retry",
                        color = White,
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }
        }
    }
}