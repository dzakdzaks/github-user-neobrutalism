package com.dzaky.githubuser.ui.component

import com.dzaky.githubuser.common.formatNumber
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.dzaky.githubuser.R
import com.dzaky.githubuser.ui.theme.Black
import com.dzaky.githubuser.ui.theme.LocalNeoBrutalProperties
import com.dzaky.githubuser.ui.theme.NeoBrutalYellow

/**
 * A reusable component for displaying user statistics (followers/following).
 *
 * @param followers Number of followers
 * @param following Number of following
 * @param modifier Additional modifier to apply
 */
@Composable
fun UserStatsRow(
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
            backgroundColor = Color(0xFFE8DDBA) // Light beige
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
                    text = stringResource(R.string.followers),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        // Following card
        NeoBrutalCard(
            backgroundColor = Color(0xFFBFD2E3) // Light blue
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
                    text = stringResource(R.string.following),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

/**
 * A reusable component for displaying user avatars with hover effects.
 *
 * @param avatarUrl URL of the avatar image
 * @param size Size of the avatar in dp
 * @param modifier Additional modifier to apply
 */
@Composable
fun UserAvatar(
    avatarUrl: String,
    size: Int = 60,
    modifier: Modifier = Modifier
) {
    val properties = LocalNeoBrutalProperties.current
    val interactionSource = remember { MutableInteractionSource() }
    val isHovered by interactionSource.collectIsHoveredAsState()

    Image(
        painter = rememberAsyncImagePainter(
            model = avatarUrl,
            placeholder = painterResource(R.drawable.baseline_person_24)
        ),
        contentDescription = null,
        modifier = modifier
            .size(size.dp)
            .hoverable(interactionSource)
            .border(
                width = if (isHovered) (properties.borderWidth + 1).dp else properties.borderWidth.dp,
                color = if (isHovered) NeoBrutalYellow else Black,
                shape = CircleShape
            )
            .padding(4.dp)
            .clip(CircleShape)
    )
}

/**
 * A reusable component for displaying a user in a list item.
 *
 * @param username Username to display
 * @param avatarUrl URL of the avatar image
 * @param onClick Callback when item is clicked
 * @param modifier Additional modifier to apply
 */
@Composable
fun UserListItem(
    username: String,
    avatarUrl: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    NeoBrutalListItem(
        modifier = modifier.fillMaxWidth(),
        onClick = onClick
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(12.dp)
        ) {
            UserAvatar(avatarUrl = avatarUrl)

            Spacer(modifier = Modifier.width(16.dp))

            Text(
                text = username,
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

/**
 * A button that navigates to the user's GitHub profile.
 *
 * @param onClick Callback when button is clicked
 * @param modifier Additional modifier to apply
 */
@Composable
fun ProfileButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    IconTextButton(
        onClick = onClick,
        text = stringResource(R.string.visit_github_profile),
        icon = Icons.Default.PlayArrow,
        backgroundColor = NeoBrutalYellow,
        modifier = modifier
    )
}