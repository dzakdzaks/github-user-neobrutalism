package com.dzaky.githubuser.ui.userdetail

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.rememberAsyncImagePainter
import com.dzaky.githubuser.R
import com.dzaky.githubuser.ui.component.ErrorState
import com.dzaky.githubuser.ui.component.LoadingState
import com.dzaky.githubuser.ui.component.NeoBrutalButton
import com.dzaky.githubuser.ui.component.NeoBrutalCard
import com.dzaky.githubuser.ui.component.NeoBrutalEmptyState
import com.dzaky.githubuser.ui.component.PaginationFooter
import com.dzaky.githubuser.ui.component.ProfileButton
import com.dzaky.githubuser.ui.component.RepositoryItem
import com.dzaky.githubuser.ui.component.RepositorySectionHeader
import com.dzaky.githubuser.ui.component.UserStatsRow
import com.dzaky.githubuser.ui.component.util.PaginationHandler
import com.dzaky.githubuser.ui.component.util.StaggeredAnimatedItem
import com.dzaky.githubuser.ui.component.util.shouldLoadMore
import com.dzaky.githubuser.ui.theme.Black
import com.dzaky.githubuser.ui.theme.LightGray
import com.dzaky.githubuser.ui.theme.LocalNeoBrutalProperties
import com.dzaky.githubuser.ui.theme.NeoBrutalYellow
import com.dzaky.githubuser.ui.theme.White

@Composable
fun UserDetailScreen(
    viewModel: UserDetailViewModel = hiltViewModel(),
    onBackClick: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current

    // Animation states
    val contentAlpha = remember { Animatable(0f) }

    LaunchedEffect(state.user) {
        // Animate content in when user data is loaded
        if (state.user != null) {
            contentAlpha.animateTo(1f, animationSpec = tween(500))
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(LightGray.copy(alpha = 0.2f))
    ) {
        when {
            state.isLoading && state.user == null -> {
                LoadingState(message = "Loading user profile...")
            }

            state.error != null && state.user == null -> {
                ErrorState(
                    message = "Couldn't load user data: ${state.error}",
                    onRetry = { viewModel.onEvent(UserDetailEvent.LoadUserData) }
                )
            }

            state.user != null -> {
                val offsetY by animateDpAsState(
                    targetValue = if (contentAlpha.value == 1f) 0.dp else 30.dp,
                    label = "contentOffset"
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .alpha(contentAlpha.value)
                        .offset {
                            IntOffset(0, offsetY.roundToPx())
                        }
                ) {
                    UserDetailContent(
                        state = state,
                        onRepoClick = { repoUrl ->
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(repoUrl))
                            context.startActivity(intent)
                        },
                        onRefresh = { viewModel.onEvent(UserDetailEvent.RefreshData) },
                        onBackClick = onBackClick,
                        onLoadMore = { viewModel.onEvent(UserDetailEvent.LoadNextReposPage) }
                    )
                }
            }
        }
    }
}

@Composable
private fun UserDetailContent(
    state: UserDetailState,
    onRepoClick: (String) -> Unit,
    onRefresh: () -> Unit,
    onBackClick: () -> Unit,
    onLoadMore: () -> Unit
) {
    val user = state.user ?: return
    val properties = LocalNeoBrutalProperties.current
    val scrollState = rememberLazyListState()

    // Parallax effect for header
    val headerHeight = 280.dp
    val headerOffset = remember {
        androidx.compose.runtime.derivedStateOf {
            scrollState.firstVisibleItemScrollOffset.toFloat() / 2
        }
    }

    // Use pagination utility
    val shouldLoadMore by scrollState.shouldLoadMore()

    // Handle pagination with reusable component
    PaginationHandler(
        shouldLoadMore = shouldLoadMore,
        hasNextPage = state.hasNextRepoPage,
        isLoading = state.isLoading,
        isLoadingMore = state.isLoadingMoreRepos,
        onLoadMore = onLoadMore
    )

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            state = scrollState,
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp)
        ) {
            // User profile header
            item {
                Box(
                    modifier = Modifier
                        .height(headerHeight)
                        .fillMaxWidth()
                        .offset {
                            IntOffset(0, (-headerOffset.value).dp.roundToPx())
                        }
                ) {
                    // Profile card with avatar and info
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.Center)
                    ) {
                        NeoBrutalCard(
                            backgroundColor = White,
                            borderColor = Black
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Image(
                                    painter = rememberAsyncImagePainter(user.avatarUrl),
                                    contentDescription = null,
                                    modifier = Modifier
                                        .size(110.dp)
                                        .border(
                                            width = properties.borderWidth.dp,
                                            color = Black,
                                            shape = CircleShape
                                        )
                                        .padding(properties.borderWidth.dp)
                                        .clip(CircleShape)
                                        .border(
                                            width = 2.dp,
                                            color = NeoBrutalYellow,
                                            shape = CircleShape
                                        )
                                        .padding(2.dp)
                                        .clip(CircleShape)
                                )

                                Spacer(modifier = Modifier.width(16.dp))

                                Column(
                                    modifier = Modifier.fillMaxWidth(),
                                ) {
                                    Text(
                                        text = user.fullName ?: user.username,
                                        style = MaterialTheme.typography.headlineMedium.copy(
                                            fontWeight = FontWeight.Bold
                                        ),
                                        textAlign = TextAlign.Center
                                    )

                                    Text(
                                        text = stringResource(R.string.at_username, user.username),
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = androidx.compose.ui.graphics.Color.DarkGray
                                    )

                                    Spacer(modifier = Modifier.height(8.dp))

                                    // Use reusable component
                                    UserStatsRow(
                                        followers = user.followers,
                                        following = user.following,
                                        modifier = Modifier.padding(8.dp)
                                    )

                                    Spacer(modifier = Modifier.height(8.dp))

                                    // Use reusable component
                                    ProfileButton(
                                        onClick = { onRepoClick(user.profileUrl) },
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Repository section header
            item {
                Spacer(modifier = Modifier.height(16.dp))

                // Use reusable component
                RepositorySectionHeader(
                    username = user.username,
                    repoCount = state.repos.size,
                    onRefresh = onRefresh
                )

                Spacer(modifier = Modifier.height(8.dp))
            }

            // Repository list content
            if (state.repos.isEmpty() && !state.isLoading && !state.isLoadingMoreRepos) {
                item {
                    NeoBrutalEmptyState(
                        message = "No repositories found",
                        icon = Icons.Default.Search,
                        secondaryMessage = "This user doesn't have any public repositories yet",
                        backgroundColor = LightGray,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    )
                }
            } else {
                // Show loading indicator for initial load
                if (state.isLoading && state.repos.isEmpty()) {
                    item {
                        LoadingState(
                            message = "Loading repositories...",
                            centerInParent = false,
                            modifier = Modifier.padding(vertical = 16.dp)
                        )
                    }
                } else {
                    // Repository items with staggered animation
                    itemsIndexed(
                        items = state.repos,
                        key = { _, repo -> "${repo.id}_${state.repos.indexOf(repo)}" }
                    ) { index, repo ->
                        StaggeredAnimatedItem(
                            key = repo.id,
                            index = index,
                            modifier = Modifier.padding(vertical = 4.dp)
                        ) {
                            RepositoryItem(
                                repo = repo,
                                onClick = { onRepoClick(repo.htmlUrl) },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }

                    // Add pagination footer
                    item {
                        AnimatedVisibility(
                            visible = state.repos.isNotEmpty(),
                            enter = fadeIn(),
                            exit = fadeOut()
                        ) {
                            PaginationFooter(
                                hasNextPage = state.hasNextRepoPage,
                                isLoading = state.isLoadingMoreRepos,
                                currentPage = state.currentRepoPage,
                                totalItems = state.repos.size,
                                onLoadMore = onLoadMore
                            )
                        }
                    }

                    // Error state for repository pagination errors
                    if (state.error != null && state.repos.isNotEmpty()) {
                        item {
                            ErrorState(
                                message = "Error loading more repositories: ${state.error}",
                                icon = Icons.Default.Warning,
                                modifier = Modifier.padding(top = 8.dp),
                                onRetry = onLoadMore
                            )
                        }
                    }
                }
            }
        }

        // Back button floating at the top
        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(16.dp)
        ) {
            NeoBrutalButton(
                onClick = onBackClick,
                backgroundColor = White.copy(alpha = 0.9f),
                modifier = Modifier.size(56.dp)
            ) {
                androidx.compose.material3.Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                    contentDescription = "Back",
                    tint = Black,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}