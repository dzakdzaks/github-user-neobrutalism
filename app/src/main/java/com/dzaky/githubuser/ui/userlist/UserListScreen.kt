package com.dzaky.githubuser.ui.userlist

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dzaky.githubuser.ui.component.ErrorState
import com.dzaky.githubuser.ui.component.LoadingState
import com.dzaky.githubuser.ui.component.NeoBrutalButton
import com.dzaky.githubuser.ui.component.NeoBrutalCard
import com.dzaky.githubuser.ui.component.NeoBrutalEmptyState
import com.dzaky.githubuser.ui.component.NeoBrutalTextField
import com.dzaky.githubuser.ui.component.PaginationFooter
import com.dzaky.githubuser.ui.component.UserListItem
import com.dzaky.githubuser.ui.component.util.ClearFocusOnScroll
import com.dzaky.githubuser.ui.component.util.PaginationHandler
import com.dzaky.githubuser.ui.component.util.StaggeredAnimatedItem
import com.dzaky.githubuser.ui.component.util.shouldLoadMore
import com.dzaky.githubuser.ui.theme.Black
import com.dzaky.githubuser.ui.theme.LightBlue
import com.dzaky.githubuser.ui.theme.LightGray
import com.dzaky.githubuser.ui.theme.LightYellow
import com.dzaky.githubuser.ui.theme.NeoBrutalYellow
import kotlinx.coroutines.launch

@Composable
fun UserListScreen(
    viewModel: UserListViewModel,
    onUserClick: (String) -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val localFocusManager = LocalFocusManager.current
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    // Animation states for staggered appearance
    val headerAlpha = remember { Animatable(0f) }
    val searchAlpha = remember { Animatable(0f) }
    val contentAlpha = remember { Animatable(0f) }

    // Use pagination utility
    val shouldLoadMore by listState.shouldLoadMore()

    // Handle pagination with reusable component
    PaginationHandler(
        shouldLoadMore = shouldLoadMore,
        hasNextPage = state.hasNextPage,
        isLoading = state.isLoading,
        isLoadingMore = state.isLoadingMore,
        onLoadMore = { viewModel.onEvent(UserListEvent.LoadNextPage) }
    )

    // Use extension to clear focus on scroll
    listState.ClearFocusOnScroll { localFocusManager.clearFocus() }

    // Launch animations with staggered delay
    LaunchedEffect(Unit) {
        if (state.isFirstLoad) {
            viewModel.onEvent(UserListEvent.FirstLoaded)
            headerAlpha.animateTo(1f, animationSpec = tween(500))
            searchAlpha.animateTo(1f, animationSpec = tween(500, delayMillis = 200))
            contentAlpha.animateTo(1f, animationSpec = tween(500, delayMillis = 400))
        } else {
            headerAlpha.animateTo(1f)
            searchAlpha.animateTo(1f)
            contentAlpha.animateTo(1f)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(LightGray.copy(alpha = 0.2f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .pointerInput(Unit) {
                    detectTapGestures(onTap = { localFocusManager.clearFocus() })
                }
        ) {
            // App header with improved neobrutalist style
            val headerOffsetY by animateDpAsState(
                targetValue = if (headerAlpha.value == 1f) 0.dp else (-20).dp,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                ),
                label = "headerOffset"
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 20.dp)
                    .alpha(headerAlpha.value)
                    .offset {
                        IntOffset(0, headerOffsetY.roundToPx())
                    }
            ) {
                AppHeader()
            }

            // Enhanced search field with animation
            val searchOffsetY by animateDpAsState(
                targetValue = if (searchAlpha.value == 1f) 0.dp else 20.dp,
                animationSpec = spring(),
                label = "searchOffset"
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .alpha(searchAlpha.value)
                    .offset {
                        IntOffset(0, searchOffsetY.roundToPx())
                    }
                    .padding(bottom = 16.dp)
            ) {
                SearchBar(
                    query = state.searchQuery,
                    onQueryChange = { viewModel.onEvent(UserListEvent.Search(it)) },
                    onSearch = {
                        viewModel.onEvent(UserListEvent.ForceSearch)
                        localFocusManager.clearFocus()
                    },
                    onRefresh = {
                        viewModel.onEvent(UserListEvent.RefreshData)
                        coroutineScope.launch {
                            listState.scrollToItem(0)
                        }
                    },
                    showRefreshButton = state.users.isNotEmpty()
                )
            }

            // Main content with alpha animation
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .alpha(contentAlpha.value)
            ) {
                UserListContent(
                    state = state,
                    listState = listState,
                    onUserClick = onUserClick,
                    onSearch = {
                        viewModel.onEvent(UserListEvent.ForceSearch)
                        localFocusManager.clearFocus()
                    },
                    onLoadMore = { viewModel.onEvent(UserListEvent.LoadNextPage) }
                )
            }
        }
    }
}

@Composable
private fun AppHeader() {
    NeoBrutalCard(
        modifier = Modifier.fillMaxWidth(),
        backgroundColor = NeoBrutalYellow,
        borderColor = Black
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp, horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = null,
                tint = Black,
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = "GitHub User Explorer",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold
                ),
                textAlign = TextAlign.Center,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onSearch: () -> Unit,
    onRefresh: () -> Unit,
    showRefreshButton: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        NeoBrutalTextField(
            value = query,
            onValueChange = onQueryChange,
            placeholder = { Text("Search GitHub Users") },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(onSearch = { onSearch() }),
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    tint = Black
                )
            },
            modifier = Modifier.weight(1f),
            backgroundColor = MaterialTheme.colorScheme.background,
            borderColor = Black,
        )

        // Add search button
        if (query.isNotEmpty()) {
            AnimatedVisibility(
                visible = query.isNotEmpty(),
                enter = fadeIn(animationSpec = tween(300)),
                exit = fadeOut(animationSpec = tween(300))
            ) {
                NeoBrutalButton(
                    onClick = onSearch,
                    backgroundColor = NeoBrutalYellow,
                    modifier = Modifier
                        .height(56.dp)
                        .width(56.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = Black,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

        }

        // Add refresh button if already searched
        if (showRefreshButton) {
            NeoBrutalButton(
                onClick = onRefresh,
                backgroundColor = LightBlue,
                modifier = Modifier
                    .height(56.dp)
                    .width(56.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Refresh",
                    tint = Black,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@Composable
private fun UserListContent(
    state: UserListState,
    listState: androidx.compose.foundation.lazy.LazyListState,
    onUserClick: (String) -> Unit,
    onSearch: () -> Unit,
    onLoadMore: () -> Unit
) {
    when {
        state.isLoading && state.users.isEmpty() -> {
            LoadingState(message = "Searching...")
        }

        state.error != null && state.users.isEmpty() -> {
            ErrorState(
                message = "Error: ${state.error}",
                onRetry = onSearch
            )
        }

        state.users.isEmpty() && state.searchQuery.isEmpty() -> {
            // Initial state with enhanced empty state
            NeoBrutalEmptyState(
                message = "Find GitHub Users",
                icon = Icons.Default.Search,
                secondaryMessage = "Enter a username to discover GitHub profiles and repositories",
                backgroundColor = LightYellow,
                modifier = Modifier.fillMaxWidth()
            )
        }

        state.users.isEmpty() && state.searchQuery.isNotEmpty() -> {
            // Enhanced no results state
            NeoBrutalEmptyState(
                message = "No users found",
                icon = Icons.Default.Person,
                secondaryMessage = "Try searching with a different username",
                backgroundColor = LightBlue,
                modifier = Modifier.fillMaxWidth()
            )
        }

        else -> {
            Column {
                // Results header with count and styling
                NeoBrutalCard(
                    backgroundColor = LightBlue.copy(alpha = 0.7f),
                    borderColor = Black,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp, horizontal = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Search Results",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.weight(1f)
                        )

                        Text(
                            text = "${state.users.size} of ${state.totalCount} users",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }

                // User list with staggered animations
                LazyColumn(
                    state = listState,
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(
                        items = state.users,
                        key = { user -> "${user.id}_${state.users.indexOf(user)}" }
                    ) { user ->
                        // Use reusable animation component
                        StaggeredAnimatedItem(
                            key = user.id,
                            index = state.users.indexOf(user),
                        ) {
                            // Use reusable user list item
                            UserListItem(
                                username = user.username,
                                avatarUrl = user.avatarUrl,
                                onClick = { onUserClick(user.username) },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }

                    // Add pagination footer
                    item {
                        AnimatedVisibility(
                            visible = state.users.isNotEmpty(),
                            enter = fadeIn(),
                            exit = fadeOut()
                        ) {
                            PaginationFooter(
                                hasNextPage = state.hasNextPage,
                                isLoading = state.isLoadingMore,
                                currentPage = state.currentPage,
                                totalItems = state.users.size,
                                onLoadMore = onLoadMore
                            )
                        }
                    }
                }

                // Show error state for pagination errors
                if (state.error != null && state.users.isNotEmpty()) {
                    ErrorState(
                        message = "Error loading more: ${state.error}",
                        icon = Icons.Default.Warning,
                        modifier = Modifier.padding(top = 8.dp),
                        onRetry = onLoadMore
                    )
                }
            }
        }
    }
}