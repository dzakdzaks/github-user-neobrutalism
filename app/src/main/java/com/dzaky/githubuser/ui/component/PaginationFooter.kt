package com.dzaky.githubuser.ui.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.dzaky.githubuser.ui.theme.NeoBrutalButton
import com.dzaky.githubuser.ui.theme.NeoBrutalYellow

@Composable
fun PaginationFooter(
    hasNextPage: Boolean,
    isLoading: Boolean,
    currentPage: Int,
    totalItems: Int = -1,
    onLoadMore: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Show info about current page if totalItems is available
            if (totalItems > 0) {
                Text(
                    text = "Showing $totalItems items",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Page $currentPage",
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))
            }

            // Show loading indicator or load more button
            AnimatedVisibility(
                visible = isLoading,
                enter = fadeIn(animationSpec = tween(300)),
                exit = fadeOut(animationSpec = tween(300))
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator(
                        color = NeoBrutalYellow,
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(20.dp)
                    )

                    Spacer(modifier = Modifier.padding(4.dp))

                    Text(
                        text = "Loading more...",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            // Show load more button when there are more items and not currently loading
            AnimatedVisibility(
                visible = hasNextPage && !isLoading,
                enter = fadeIn(animationSpec = tween(300)),
                exit = fadeOut(animationSpec = tween(300))
            ) {
                NeoBrutalButton(
                    onClick = onLoadMore,
                    backgroundColor = NeoBrutalYellow
                ) {
                    Text(
                        text = "Load More",
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }
        }
    }
}