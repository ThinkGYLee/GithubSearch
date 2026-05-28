package com.gyleedev.githubsearch.feature.detail.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gyleedev.githubsearch.core.designsystem.theme.GithubSearchTheme
import com.gyleedev.githubsearch.core.designsystem.theme.component.UserAvatar
import com.gyleedev.githubsearch.core.designsystem.util.toCompactString
import com.gyleedev.githubsearch.domain.model.UserModel
import com.gyleedev.githubsearch.feature.detail.extractDomain
import com.gyleedev.githubsearch.feature.detail.preview.DetailPreviewData

@Composable
fun DetailUserProfile(
    avatar: String,
    repos: Int,
    followers: Int,
    following: Int,
    name: String?,
    login: String,
    bio: String?,
    company: String?,
    email: String?,
    blogUrl: String?,
    onBlogClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    transitionKeyPrefix: String = "",
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        UserAvatar(
            avatar = avatar,
            login = login,
            size = 120.dp,
            transitionKeyPrefix = transitionKeyPrefix,
        )
        Spacer(modifier = Modifier.height(20.dp))
        if (name != null) {
            Text(
                text = name,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
        Text(
            text = login,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodyLarge,
        )

        if (bio != null) {
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = bio,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodyMedium,
            )
        }
        if (company != null || blogUrl != null) {
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
            ) {
                if (company != null) {
                    CustomSurfaceChip(
                        text = company,
                        icon = Icons.Default.Work,
                        onClick = {},
                        enabled = true,
                        modifier = Modifier.padding(horizontal = 4.dp),
                    )
                }
                if (blogUrl != null && blogUrl.isNotBlank()) {
                    CustomSurfaceChip(
                        text = extractDomain(blogUrl),
                        icon = Icons.Default.Link,
                        onClick = { onBlogClick(blogUrl) },
                        enabled = true,
                        modifier = Modifier.padding(horizontal = 4.dp),
                    )
                }
            }
        }
        if (email != null) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
            ) {
                CustomSurfaceChip(
                    text = email,
                    icon = Icons.Default.Mail,
                    onClick = {},
                    enabled = true,
                    modifier = Modifier.padding(horizontal = 4.dp),
                )
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
        ElevatedCard {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 24.dp, horizontal = 16.dp)
                    .height(IntrinsicSize.Min),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        text = repos.toCompactString(),
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.primary,
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Repository",
                        fontWeight = FontWeight.Normal,
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                VerticalDivider()
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        text = followers.toCompactString(),
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.primary,
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Follower",
                        fontWeight = FontWeight.Normal,
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                VerticalDivider()
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        text = following.toCompactString(),
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.primary,
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Following",
                        fontWeight = FontWeight.Normal,
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
    }
}

@Preview(
    showBackground = true,
    name = "Light Mode",
)
@Preview(
    showBackground = true,
    uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES,
    name = "Dark Mode",
)
@Composable
private fun DetailUserInfoPreview(
    user: UserModel = DetailPreviewData.skydovesUser,
) {
    GithubSearchTheme {
        Surface {
            DetailUserProfile(
                avatar = user.avatar,
                repos = user.repoCount,
                followers = user.followers,
                following = user.following,
                name = user.name,
                login = user.login,
                bio = user.bio,
                company = user.company,
                email = user.email,
                blogUrl = user.blogUrl,
                onBlogClick = {},
            )
        }
    }
}

@Preview(
    showBackground = true,
    name = "Light Mode",
)
@Preview(
    showBackground = true,
    uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES,
    name = "Dark Mode",
)
@Composable
private fun ChipPreview(
    user: UserModel = DetailPreviewData.skydovesUser,
) {
    GithubSearchTheme {
        Surface {
            CustomSurfaceChip(
                text = user.company!!,
                icon = Icons.Filled.Work,
                onClick = {},
                enabled = true,
            )
        }
    }
}
