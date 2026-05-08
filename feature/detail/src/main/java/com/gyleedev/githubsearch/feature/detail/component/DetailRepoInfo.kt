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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.gyleedev.githubsearch.core.designsystem.theme.Yellow

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
                text = description,
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
                text = stargazer.toString(),
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
