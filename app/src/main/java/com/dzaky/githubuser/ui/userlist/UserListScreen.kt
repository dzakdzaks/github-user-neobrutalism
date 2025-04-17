package com.dzaky.githubuser.ui.userlist

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.rememberAsyncImagePainter
import com.dzaky.githubuser.R
import com.dzaky.githubuser.ui.component.PaginationFooter
import com.dzaky.githubuser.ui.theme.Black
import com.dzaky.githubuser.ui.theme.LightBlue
import com.dzaky.githubuser.ui.theme.LightGray
import com.dzaky.githubuser.ui.theme.LightYellow
import com.dzaky.githubuser.ui.theme.LocalNeoBrutalProperties
import com.dzaky.githubuser.ui.theme.NeoBrutalButton
import com.dzaky.githubuser.ui.theme.NeoBrutalCard
import com.dzaky.githubuser.ui.theme.NeoBrutalEmptyState
import com.dzaky.githubuser.ui.theme.NeoBrutalErrorState
import com.dzaky.githubuser.ui.theme.NeoBrutalListItem
import com.dzaky.githubuser.ui.theme.NeoBrutalTextField
import com.dzaky.githubuser.ui.theme.NeoBrutalYellow
import com.dzaky.githubuser.ui.theme.White
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

@Composable
fun UserListScreen(
    viewModel: UserListViewModel,
    onUserClick: (String) -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val localFocusManager = LocalFocusManager.current
    val listState = rememberLazyListState()
    val properties = LocalNeoBrutalProperties.current
    val coroutineScope = rememberCoroutineScope()

    // Animation states for staggered appearance
    val headerAlpha = remember { Animatable(0f) }
    val searchAlpha = remember { Animatable(0f) }
    val contentAlpha = remember { Animatable(0f) }

    // Check if we're near the bottom of the list to trigger load more
    val shouldLoadMore by remember {
        derivedStateOf {
            val layoutInfo = listState.layoutInfo
            val totalItemsNumber = layoutInfo.totalItemsCount
            val lastVisibleItemIndex = layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0

            // Load more when user scrolls to the last 3 items
            lastVisibleItemIndex >= totalItemsNumber - 3
        }
    }

    // Trigger load more when scrolled to bottom
    LaunchedEffect(shouldLoadMore) {
        if (shouldLoadMore && state.hasNextPage && !state.isLoadingMore && !state.isLoading) {
            viewModel.onEvent(UserListEvent.LoadNextPage)
        }
    }

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

    // Auto-clear focus when user scrolls
    LaunchedEffect(listState) {
        snapshotFlow { listState.isScrollInProgress }
            .map { it }
            .distinctUntilChanged()
            .collect { isScrolling ->
                if (isScrolling) localFocusManager.clearFocus()
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
                    detectTapGestures(onTap = {
                        localFocusManager.clearFocus()
                    })
                }
        ) {
            // App header with improved neobrutalist style
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 20.dp)
                    .alpha(headerAlpha.value)
                    .offset(y = animateDpAsState(
                        targetValue = if (headerAlpha.value == 1f) 0.dp else (-20).dp,
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessLow
                        ),
                        label = "headerOffset"
                    ).value)
            ) {
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
                            modifier = Modifier.size(36.dp)
                        )

                        Spacer(modifier = Modifier.width(12.dp))

                        Text(
                            text = "GitHub User Explorer",
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // Enhanced search field with animation
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .alpha(searchAlpha.value)
                    .offset(y = animateDpAsState(
                        targetValue = if (searchAlpha.value == 1f) 0.dp else 20.dp,
                        animationSpec = spring(),
                        label = "searchOffset"
                    ).value)
                    .padding(bottom = 16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    NeoBrutalTextField(
                        value = state.searchQuery,
                        onValueChange = { viewModel.onEvent(UserListEvent.Search(it)) },
                        placeholder = { Text("Search GitHub Users") },
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                        keyboardActions = KeyboardActions(
                            onSearch = {
                                viewModel.onEvent(UserListEvent.ForceSearch)
                                localFocusManager.clearFocus()
                            }
                        ),
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
                    if (state.searchQuery.isNotEmpty()) {
                        NeoBrutalButton(
                            onClick = {
                                viewModel.onEvent(UserListEvent.ForceSearch)
                                localFocusManager.clearFocus()
                            },
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

                    // Add refresh button if already searched
                    if (state.users.isNotEmpty()) {
                        NeoBrutalButton(
                            onClick = {
                                viewModel.onEvent(UserListEvent.RefreshData)
                                coroutineScope.launch {
                                    listState.scrollToItem(0)
                                }
                            },
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

            // Main content with alpha animation
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .alpha(contentAlpha.value)
            ) {
                when {
                    state.isLoading && state.users.isEmpty() -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                CircularProgressIndicator(
                                    color = NeoBrutalYellow,
                                    strokeWidth = 4.dp,
                                    modifier = Modifier.size(48.dp)
                                )

                                Spacer(modifier = Modifier.height(16.dp))

                                Text(
                                    text = "Searching...",
                                    style = MaterialTheme.typography.bodyLarge
                                )
                            }
                        }
                    }

                    state.error != null && state.users.isEmpty() -> {
                        NeoBrutalErrorState(
                            message = "Error: ${state.error}",
                            icon = Icons.Default.Warning,
                            modifier = Modifier.fillMaxWidth(),
                            onRetry = {
                                if (state.searchQuery.isNotEmpty()) {
                                    viewModel.onEvent(UserListEvent.ForceSearch)
                                    localFocusManager.clearFocus()
                                }
                            }
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

                            // User list
                            LazyColumn(
                                state = listState,
                                modifier = Modifier.fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                items(
                                    items = state.users,
                                    key = { user -> "${user.id}_${state.users.indexOf(user)}" }
                                ) { user ->
                                    val itemAlpha = remember { Animatable(0f) }
                                    val itemOffset = remember { Animatable(50f) }

                                    LaunchedEffect(user.id) {
                                        launch {
                                            itemAlpha.animateTo(
                                                targetValue = 1f,
                                                animationSpec = tween(300)
                                            )
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
                                        modifier = Modifier
                                            .alpha(itemAlpha.value)
                                            .offset(y = itemOffset.value.dp)
                                    ) {
                                        NeoBrutalListItem(
                                            modifier = Modifier.fillMaxWidth(),
                                            backgroundColor = White,
                                            onClick = { onUserClick(user.username) }
                                        ) {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                modifier = Modifier.padding(12.dp)
                                            ) {
                                                // Enhanced avatar with animated border on hover
                                                Box {
                                                    val interactionSource = remember { MutableInteractionSource() }
                                                    val isHovered by interactionSource.collectIsHoveredAsState()

                                                    Image(
                                                        painter = rememberAsyncImagePainter(
                                                            model = user.avatarUrl,
                                                            placeholder = painterResource(R.drawable.baseline_person_24)
                                                        ),
                                                        contentDescription = null,
                                                        modifier = Modifier
                                                            .size(60.dp)
                                                            .hoverable(interactionSource)
                                                            .border(
                                                                width = animateDpAsState(
                                                                    targetValue = if (isHovered) (properties.borderWidth + 1).dp else properties.borderWidth.dp,
                                                                    label = "borderWidth"
                                                                ).value,
                                                                color = if (isHovered) NeoBrutalYellow else Black,
                                                                shape = CircleShape
                                                            )
                                                            .padding(4.dp)
                                                            .clip(CircleShape)
                                                    )
                                                }

                                                Spacer(modifier = Modifier.width(16.dp))

                                                Text(
                                                    text = user.username,
                                                    style = MaterialTheme.typography.titleMedium.copy(
                                                        fontWeight = FontWeight.Bold
                                                    )
                                                )

                                                Spacer(modifier = Modifier.weight(1f))

                                                // Add a visual indicator to show it's clickable
                                                Icon(
                                                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                                    contentDescription = null,
                                                    tint = Black.copy(alpha = 0.6f),
                                                    modifier = Modifier.size(24.dp)
                                                )
                                            }
                                        }
                                    }
                                }

                                // Add pagination footer at the bottom of the list
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
                                            onLoadMore = { viewModel.onEvent(UserListEvent.LoadNextPage) }
                                        )
                                    }
                                }
                            }

                            // Show error state at the bottom if error occurs during pagination
                            if (state.error != null && state.users.isNotEmpty()) {
                                NeoBrutalErrorState(
                                    message = "Error loading more: ${state.error}",
                                    icon = Icons.Default.Warning,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 8.dp),
                                    onRetry = { viewModel.onEvent(UserListEvent.LoadNextPage) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}