package com.gyleedev.githubsearch.feature.detail.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.gyleedev.githubsearch.core.designsystem.theme.GithubSearchTheme
import com.gyleedev.githubsearch.core.designsystem.theme.Yellow
import com.gyleedev.githubsearch.core.designsystem.util.parseEmojis
import com.gyleedev.githubsearch.core.designsystem.util.toCompactString
import com.gyleedev.githubsearch.domain.model.RepositoryModel
import com.gyleedev.githubsearch.feature.detail.preview.DetailPreviewData

@Composable
fun DetailRepoInfo(
    name: String?,
    description: String?,
    language: String?,
    stargazer: Int,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .padding(horizontal = 24.dp, vertical = 12.dp),
    ) {
        if (name != null) {
            Text(
                text = name,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(vertical = 4.dp),
            )
        }

        if (description != null) {
            Text(
                text = description.parseEmojis() ?: "",
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier.padding(vertical = 4.dp),
            )
        }
        Row(
            horizontalArrangement = Arrangement.Start,
            modifier = Modifier.padding(vertical = 4.dp),
        ) {
            Icon(
                imageVector = Icons.Filled.Star,
                contentDescription = null,
                modifier =
                Modifier
                    .padding(vertical = 8.dp)
                    .width(24.dp)
                    .height(24.dp),
                tint = Yellow,
            )
            Text(
                text = stargazer.toCompactString(),
                modifier = Modifier.padding(8.dp),
            )
            if (language != null) {
                Text(
                    text = language,
                    modifier = Modifier.padding(8.dp),
                )
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
private fun DetailRepoInfoPreview(
    repo: RepositoryModel = DetailPreviewData.skydovesRepos.first(),
) {
    GithubSearchTheme {
        Surface {
            DetailRepoInfo(
                name = repo.name,
                description = repo.description,
                language = repo.language,
                stargazer = repo.stargazer,
            )
        }
    }
}
