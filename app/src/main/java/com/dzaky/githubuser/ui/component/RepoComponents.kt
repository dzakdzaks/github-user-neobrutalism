package com.dzaky.githubuser.ui.component

import com.dzaky.githubuser.common.formatNumber

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.dzaky.githubuser.domain.model.Repo
import com.dzaky.githubuser.ui.theme.Black
import com.dzaky.githubuser.ui.theme.LightBlue
import com.dzaky.githubuser.ui.theme.LightYellow
import com.dzaky.githubuser.ui.theme.NeoBrutalYellow
import com.dzaky.githubuser.ui.theme.White

/**
 * Header component for repository sections.
 *
 * @param username Username whose repositories are being displayed
 * @param repoCount Number of repositories
 * @param onRefresh Callback when refresh button is clicked
 * @param modifier Additional modifier to apply
 */
@Composable
fun RepositorySectionHeader(
    username: String,
    repoCount: Int,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier
) {
    NeoBrutalCard(
        backgroundColor = LightBlue,
        borderColor = Black,
        modifier = modifier.fillMaxWidth()
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
                    text = "Explore $username's projects",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            RepoCountBadge(repoCount = repoCount)

            Spacer(modifier = Modifier.width(8.dp))

            // Add refresh button for repositories
            NeoBrutalButton(
                onClick = onRefresh,
                backgroundColor = LightYellow,
                modifier = Modifier.size(48.dp)
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

/**
 * Badge to display repository count.
 *
 * @param repoCount Number of repositories
 * @param modifier Additional modifier to apply
 */
@Composable
fun RepoCountBadge(
    repoCount: Int,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
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
            text = "$repoCount",
            style = MaterialTheme.typography.labelLarge.copy(
                fontWeight = FontWeight.Bold
            ),
            color = Black
        )
    }
}

/**
 * A reusable component for displaying repository items.
 *
 * @param repo Repository data to display
 * @param onClick Callback when item is clicked
 * @param modifier Additional modifier to apply
 */
@Composable
fun RepositoryItem(
    repo: Repo,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    NeoBrutalListItem(
        modifier = modifier.fillMaxWidth(),
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