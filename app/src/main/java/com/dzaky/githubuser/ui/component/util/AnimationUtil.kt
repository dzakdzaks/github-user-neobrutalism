package com.dzaky.githubuser.ui.component.util

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

/**
 * A wrapper component that applies a fade-in and slide-up animation to its content.
 *
 * @param key The key used to trigger the animation when it changes
 * @param delayMillis Optional delay before starting the animation (in milliseconds)
 * @param modifier Additional modifier to be applied to the content
 * @param content The composable content to be animated
 */
@Composable
fun AnimatedItem(
    key: Any,
    delayMillis: Int = 0,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val itemAlpha = remember { Animatable(0f) }
    val itemOffset = remember { Animatable(50f) }

    LaunchedEffect(key) {
        launch {
            itemAlpha.animateTo(1f, animationSpec = tween(300, delayMillis = delayMillis))
        }
        launch {
            itemOffset.animateTo(
                targetValue = 0f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                )
            )
        }
    }

    Box(
        modifier = modifier
            .alpha(itemAlpha.value)
            .offset { IntOffset(0, itemOffset.value.dp.roundToPx()) }
    ) {
        content()
    }
}

/**
 * A wrapper component that applies a staggered animation based on index.
 *
 * @param key The key used to trigger the animation when it changes
 * @param index The index of the item in a list, used to calculate staggered delay
 * @param baseDelay Base delay before starting the animation (in milliseconds)
 * @param staggerDelay Additional delay per item based on index (in milliseconds)
 * @param modifier Additional modifier to be applied to the content
 * @param content The composable content to be animated
 */
@Composable
fun StaggeredAnimatedItem(
    key: Any,
    index: Int,
    baseDelay: Int = 0,
    staggerDelay: Int = 50,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val delay = baseDelay + staggerDelay
    AnimatedItem(
        key = key,
        delayMillis = delay,
        modifier = modifier,
        content = content
    )
}