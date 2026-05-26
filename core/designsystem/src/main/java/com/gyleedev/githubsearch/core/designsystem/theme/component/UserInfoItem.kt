package com.gyleedev.githubsearch.core.designsystem.theme.component

import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.gyleedev.githubsearch.core.designsystem.util.removeAtPrefix
import com.gyleedev.githubsearch.core.designsystem.util.toCompactString

@Composable
fun UserInfoItem(
    avatar: String,
    name: String?,
    login: String,
    follower: Int,
    company: String?,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    modifier: Modifier = Modifier,
    isSelected: Boolean = false,
) {
    val exposeTitle = if (name != null) {
        "$name ($login)"
    } else {
        login
    }
    Row(
        modifier =
        modifier
            .fillMaxWidth()
            .heightIn(min = 80.dp, max = 100.dp)
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick,
            )
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        FlippableUserAvatar(avatar = avatar, isSelected = isSelected)
        Column {
            Text(
                text = exposeTitle,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleMedium,
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = follower.toCompactString(),
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.labelMedium,
                )
                Text(
                    text = " followers",
                    fontWeight = FontWeight.Medium,
                    style = MaterialTheme.typography.labelMedium,
                )
                Spacer(modifier = Modifier.width(12.dp))
                if (company != null) {
                    Icon(
                        imageVector = Icons.Default.Work,
                        contentDescription = null,
                        modifier = Modifier.size(12.dp),
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = company.removeAtPrefix(),
                        fontWeight = FontWeight.Normal,
                        style = MaterialTheme.typography.labelMedium,
                    )
                }
            }
        }
    }
}
