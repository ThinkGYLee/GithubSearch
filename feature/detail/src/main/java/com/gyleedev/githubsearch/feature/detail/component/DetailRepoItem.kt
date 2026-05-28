package com.gyleedev.githubsearch.feature.detail.component

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gyleedev.githubsearch.core.designsystem.theme.GithubSearchTheme
import com.gyleedev.githubsearch.core.designsystem.util.getColor
import com.gyleedev.githubsearch.core.designsystem.util.parseEmojis
import com.gyleedev.githubsearch.core.designsystem.util.toCompactString
import com.gyleedev.githubsearch.domain.model.RepositoryModel
import com.gyleedev.githubsearch.feature.detail.preview.DetailPreviewData

@Composable
fun DetailRepoItem(
    name: String?,
    description: String?,
    language: String?,
    stargazer: Int,
    modifier: Modifier = Modifier,
) {
    OutlinedCard(
        modifier = modifier
            .fillMaxWidth()
            .padding(
                vertical = 8.dp,
                horizontal = 16.dp,
            ),
        border = CardDefaults.outlinedCardBorder().copy(width = 0.2.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 16.dp,
                    vertical = 16.dp,
                ),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                if (name != null) {
                    Text(
                        text = name,
                        style = MaterialTheme.typography.titleMedium,
                    )
                }
                CustomSurfaceChip(
                    text = stargazer.toCompactString(),
                    icon = Icons.Outlined.StarOutline,
                    onClick = {},
                    enabled = false,
                )
            }

            if (description != null) {
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(
                        text = description.parseEmojis() ?: "",
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
            }

            if (language != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        imageVector = Icons.Filled.Circle,
                        tint = getColor(language),
                        contentDescription = "repository language",
                        modifier = Modifier.size(16.dp),
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = language,
                        style = MaterialTheme.typography.labelSmall,
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
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    name = "Dark Mode",
)
@Composable
private fun DetailRepoInfoPreview(
    repo: RepositoryModel = DetailPreviewData.skydovesRepos.first(),
) {
    GithubSearchTheme {
        Surface {
            DetailRepoItem(
                name = repo.name,
                description = repo.description,
                language = repo.language,
                stargazer = repo.stargazer,
            )
        }
    }
}
