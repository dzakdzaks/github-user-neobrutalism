package com.dzaky.githubuser.ui.component.util

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter

/**
 * Extension function to determine if we should load more items based on scroll position.
 *
 * @param buffer Number of items from the end to trigger loading (default: 3)
 * @return Derived state boolean indicating if we should load more
 */
@Composable
fun LazyListState.shouldLoadMore(buffer: Int = 3) = remember {
    derivedStateOf {
        val layoutInfo = this.layoutInfo
        val totalItemsNumber = layoutInfo.totalItemsCount
        val lastVisibleItemIndex = layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0

        // Load more when user scrolls to the last N items
        lastVisibleItemIndex >= totalItemsNumber - buffer
    }
}

/**
 * A composable that automatically triggers loading more items when the user
 * scrolls near the end of the list, respecting pagination state.
 *
 * @param hasNextPage Whether there are more items to load
 * @param isLoading Whether new items are currently being loaded
 * @param isLoadingMore Whether more items are being loaded (pagination)
 * @param onLoadMore Callback to load more items
 */
@Composable
fun PaginationHandler(
    shouldLoadMore: Boolean,
    hasNextPage: Boolean,
    isLoading: Boolean,
    isLoadingMore: Boolean,
    onLoadMore: () -> Unit
) {
    LaunchedEffect(shouldLoadMore) {
        if (shouldLoadMore && hasNextPage && !isLoadingMore && !isLoading) {
            onLoadMore()
        }
    }
}

/**
 * Extension function to clear focus when scrolling begins.
 *
 * @param clearFocus Function to clear focus
 */
@Composable
fun LazyListState.ClearFocusOnScroll(clearFocus: () -> Unit) {
    LaunchedEffect(this) {
        snapshotFlow { this@ClearFocusOnScroll.isScrollInProgress }
            .distinctUntilChanged()
            .filter { it }  // Only when scrolling starts
            .collect { clearFocus() }
    }
}