package com.dzaky.githubuser.ui.userdetail

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.rememberAsyncImagePainter
import com.dzaky.githubuser.domain.model.Repo
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
import com.dzaky.githubuser.ui.theme.NeoBrutalYellow
import com.dzaky.githubuser.ui.theme.White
import kotlinx.coroutines.launch

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
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.align(Alignment.Center).padding(16.dp)
                ) {
                    CircularProgressIndicator(
                        color = NeoBrutalYellow,
                        strokeWidth = 4.dp,
                        modifier = Modifier.size(48.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Loading user profile...",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }

            state.error != null && state.user == null -> {
                NeoBrutalErrorState(
                    message = "Couldn't load user data: ${state.error}",
                    icon = Icons.Default.Warning,
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    onRetry = { viewModel.onEvent(UserDetailEvent.LoadUserData) }
                )
            }

            state.user != null -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .alpha(contentAlpha.value)
                        .offset(
                            y = animateDpAsState(
                                targetValue = if (contentAlpha.value == 1f) 0.dp else 30.dp,
                                animationSpec = spring(
                                    dampingRatio = Spring.DampingRatioMediumBouncy,
                                    stiffness = Spring.StiffnessLow
                                ),
                                label = "contentOffset"
                            ).value
                        )
                ) {
                    UserDetailContent(
                        state = state,
                        onRepoClick = { repoUrl ->
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(repoUrl))
                            context.startActivity(intent)
                        },
                        onRefresh = { viewModel.onEvent(UserDetailEvent.RefreshData) },
                        onBackClick = { onBackClick() },
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
    val headerOffset =
        remember { derivedStateOf { scrollState.firstVisibleItemScrollOffset.toFloat() / 2 } }

    // Check if we're near the bottom to trigger load more
    val shouldLoadMore = remember {
        derivedStateOf {
            val layoutInfo = scrollState.layoutInfo
            val totalItemsNumber = layoutInfo.totalItemsCount
            val lastVisibleItemIndex = layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0

            // Load more when user scrolls to the last 3 items
            lastVisibleItemIndex >= totalItemsNumber - 3
        }
    }

    // Trigger load more when scrolled to bottom
    LaunchedEffect(shouldLoadMore.value) {
        if (shouldLoadMore.value && state.hasNextRepoPage && !state.isLoadingMoreRepos && !state.isLoading) {
            onLoadMore()
        }
    }

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
                        .offset(y = (-headerOffset.value).dp)
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
                                horizontalArrangement = Arrangement.spacedBy(16.dp),
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
                                        text = "@${user.username}",
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = Color.DarkGray
                                    )

                                    Spacer(modifier = Modifier.height(8.dp))

                                    // Updated stats row with simpler design
                                    UserStatsRow(
                                        followers = user.followers,
                                        following = user.following,
                                        modifier = Modifier.padding(8.dp)
                                    )

                                    Spacer(modifier = Modifier.height(8.dp))

                                    // Enhanced profile link button
                                    GlowingNeoBrutalButton(
                                        onClick = { onRepoClick(user.profileUrl) },
                                        backgroundColor = NeoBrutalYellow,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.PlayArrow,
                                                contentDescription = null,
                                                tint = Black,
                                                modifier = Modifier.size(18.dp)
                                            )

                                            Spacer(modifier = Modifier.width(8.dp))

                                            Text(
                                                text = "Visit GitHub Profile",
                                                style = MaterialTheme.typography.labelLarge.copy(
                                                    fontWeight = FontWeight.Bold
                                                )
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Repository section header
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp)
                ) {
                    NeoBrutalCard(
                        backgroundColor = LightBlue,
                        borderColor = Black,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 16.dp, horizontal = 16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Menu,
                                contentDescription = null,
                                tint = Black,
                                modifier = Modifier.size(28.dp)
                            )

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Repositories",
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        fontWeight = FontWeight.Bold
                                    )
                                )

                                Text(
                                    text = "Explore ${user.username}'s projects",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .background(
                                        color = NeoBrutalYellow,
                                        shape = RoundedCornerShape(16.dp)
                                    )
                                    .border(
                                        width = 1.dp,
                                        color = Black,
                                        shape = RoundedCornerShape(16.dp)
                                    )
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = "${state.repos.size}",
                                    style = MaterialTheme.typography.labelLarge.copy(
                                        fontWeight = FontWeight.Bold
                                    ),
                                    color = Black
                                )
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            // Add refresh button for repositories
                            NeoBrutalButton(
                                onClick = onRefresh,
                                backgroundColor = LightYellow,
                                modifier = Modifier.size(40.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Refresh,
                                    contentDescription = "Refresh repositories",
                                    tint = Black,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
            }

            // Repository list with animations - directly in the main LazyColumn
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
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                color = NeoBrutalYellow,
                                strokeWidth = 3.dp,
                                modifier = Modifier.size(40.dp)
                            )
                        }
                    }
                } else {
                    // Repository items
                    itemsIndexed(
                        items = state.repos,
                        key = { _, repo -> "${repo.id}_${state.repos.indexOf(repo)}" }
                    ) { index, repo ->
                        // Staggered animation for repositories
                        val itemAlpha = remember { Animatable(0f) }
                        val itemOffset = remember { Animatable(50f) }

                        LaunchedEffect(repo.id) {
                            launch {
                                itemAlpha.animateTo(1f, animationSpec = tween(300))
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
                                .padding(vertical = 4.dp)
                        ) {
                            EnhancedRepositoryItem(
                                repo = repo,
                                onClick = { onRepoClick(repo.htmlUrl) },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }

                    // Add pagination footer at the bottom of the repository list
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
                            NeoBrutalErrorState(
                                message = "Error loading more repositories: ${state.error}",
                                icon = Icons.Default.Warning,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 8.dp),
                                onRetry = onLoadMore
                            )
                        }
                    }
                }
            }
        }

        // Back button floating at the top
        Box(
            modifier = Modifier.align(Alignment.TopStart).padding(16.dp)
        ) {
            NeoBrutalButton(
                onClick = onBackClick,
                backgroundColor = White.copy(alpha = 0.9f),
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                    contentDescription = "Back",
                    tint = Black,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@Composable
private fun UserStatsRow(
    followers: Int,
    following: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Followers card
        NeoBrutalCard(
            backgroundColor = Color(0xFFE8DDBA) // Light beige matching screenshot
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(vertical = 12.dp)
            ) {
                Text(
                    text = formatNumber(followers),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "Followers",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        // Following card
        NeoBrutalCard(
            backgroundColor = Color(0xFFBFD2E3) // Light blue matching screenshot
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(vertical = 12.dp)
            ) {
                Text(
                    text = formatNumber(following),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "Following",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
private fun GlowingNeoBrutalButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    backgroundColor: Color = MaterialTheme.colorScheme.primary,
    borderColor: Color = MaterialTheme.colorScheme.onPrimary,
    content: @Composable () -> Unit
) {
    val properties = LocalNeoBrutalProperties.current
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val isHovered by interactionSource.collectIsHoveredAsState()

    // Animations for hover/press effects
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "scaleAnimation"
    )

    val borderWidth by animateDpAsState(
        targetValue = if (isHovered && !isPressed) (properties.borderWidth + 1).dp else properties.borderWidth.dp,
        label = "borderAnimation"
    )

    val glowAlpha by animateFloatAsState(
        targetValue = if (isHovered) 0.3f else 0f,
        label = "glowAnimation"
    )

    Box(modifier = modifier) {
        // Glow effect
        if (glowAlpha > 0) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .clip(RoundedCornerShape(properties.cornerRadius.dp))
                    .shadow(
                        elevation = 8.dp,
                        shape = RoundedCornerShape(properties.cornerRadius.dp),
                        spotColor = backgroundColor,
                        ambientColor = backgroundColor
                    )
                    .alpha(glowAlpha)
            )
        }

        // Button with enhanced effects
        Box(
            modifier = Modifier
                .scale(scale)
        ) {
            // Shadow/offset effect
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .offset(
                        x = if (isPressed) 0.dp else properties.shadowOffset.dp,
                        y = if (isPressed) 0.dp else properties.shadowOffset.dp
                    )
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
                    .hoverable(interactionSource)
                    .background(
                        color = backgroundColor,
                        shape = RoundedCornerShape(properties.cornerRadius.dp)
                    )
                    .border(
                        width = borderWidth,
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

@Composable
private fun EnhancedRepositoryItem(
    repo: Repo,
    onClick: () -> Unit,
) {
    NeoBrutalListItem(
        modifier = Modifier.fillMaxWidth(),
        backgroundColor = White,
        onClick = onClick
    ) {
        Column(
            modifier = Modifier
                .padding(vertical = 16.dp)
        ) {
            // Repository name with icon
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.Email,
                    contentDescription = null,
                    tint = Black,
                    modifier = Modifier.size(24.dp)
                )

                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    text = repo.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            // Description with animation
            repo.description?.let {
                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = it,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 36.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Repository stats in chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 36.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Star count
                StatChip(
                    icon = Icons.Default.Star,
                    value = formatNumber(repo.stargazersCount),
                    backgroundColor = LightYellow.copy(alpha = 0.5f)
                )

                // Language indicator
                repo.language?.let {
                    StatChip(
                        icon = Icons.Default.Create,
                        value = it,
                        backgroundColor = LightBlue.copy(alpha = 0.5f)
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                // View indicator
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = "View repository",
                    tint = Black.copy(alpha = 0.5f),
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
private fun StatChip(
    icon: ImageVector,
    value: String,
    backgroundColor: Color
) {
    Surface(
        color = backgroundColor,
        shape = RoundedCornerShape(4.dp),
        border = BorderStroke(1.dp, Black.copy(alpha = 0.3f))
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(14.dp),
                tint = Black.copy(alpha = 0.7f)
            )

            Spacer(modifier = Modifier.width(4.dp))

            Text(
                text = value,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

// Helper function to format numbers with K for thousands
private fun formatNumber(number: Int): String {
    return when {
        number >= 1000 -> String.format("%.1fK", number / 1000.0)
        else -> number.toString()
    }
}