package com.gyleedev.githubsearch.feature.detail.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apartment
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.gyleedev.githubsearch.core.designsystem.theme.GithubSearchTheme
import com.gyleedev.githubsearch.core.designsystem.theme.component.UserAvatar
import com.gyleedev.githubsearch.domain.model.UserModel
import com.gyleedev.githubsearch.feature.detail.R
import com.gyleedev.githubsearch.feature.detail.preview.DetailPreviewData

@Composable
fun DetailUserInfo(
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
    modifier: Modifier = Modifier,
) {
    Column(modifier = Modifier.padding(12.dp)) {
        Row(
            modifier =
            modifier
                .fillMaxWidth()
                .height(100.dp)
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceAround,
        ) {
            UserAvatar(
                avatar = avatar,
                size = 80.dp,
            )
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = repos.toString(),
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = stringResource(id = R.string.detail_user_title_repos),
                )
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = followers.toString(),
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = stringResource(id = R.string.detail_user_title_follower),
                )
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = following.toString(),
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = stringResource(id = R.string.detail_user_title_following),
                )
            }
        }
        if (name != null) {
            Text(
                text = name,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(12.dp),
            )
        }
        Text(
            text = login,
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(12.dp),
        )

        if (bio != null) {
            Text(
                text = bio,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(12.dp),
            )
        }

        if (company != null) {
            Row(
                modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Icon(
                    imageVector = Icons.Filled.Apartment,
                    contentDescription = null,
                    modifier =
                    Modifier
                        .width(24.dp)
                        .height(24.dp),
                )
                Text(text = company)
            }
        }

        if (email != null) {
            Row(
                modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Icon(
                    imageVector = Icons.Filled.Mail,
                    contentDescription = null,
                    modifier =
                    Modifier
                        .width(24.dp)
                        .height(24.dp),
                )
                Text(text = email)
            }
        }

        if (!blogUrl.isNullOrBlank()) {
            Row(
                modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Icon(
                    imageVector = Icons.Filled.Link,
                    contentDescription = null,
                    modifier =
                    Modifier.size(width = 24.dp, height = 24.dp),
                )
                Text(text = blogUrl)
            }
        }
    }
}

@androidx.compose.ui.tooling.preview.Preview(
    showBackground = true,
    name = "Light Mode",
)
@androidx.compose.ui.tooling.preview.Preview(
    showBackground = true,
    uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES,
    name = "Dark Mode",
)
@Composable
private fun DetailUserInfoPreview(
    user: UserModel = DetailPreviewData.skydovesUser,
) {
    GithubSearchTheme {
        DetailUserInfo(
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
        )
    }
}
