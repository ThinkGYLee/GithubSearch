package com.gyleedev.githubsearch.feature.setting.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.gyleedev.githubsearch.feature.setting.model.SettingEvent
import com.gyleedev.githubsearch.feature.setting.model.SettingItem
import com.gyleedev.githubsearch.feature.setting.model.SettingRowItem

@Composable
fun SettingCard(
    isDark: Boolean,
    items: SettingItem.Card,
    onClick: (SettingEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        colors =
        if (isDark) {
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainer,
            )
        } else {
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
            )
        },
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.elevatedCardElevation(),
    ) {
        items.items.forEach { item ->
            SettingRow(
                leadingIcon = item.icon,
                text = stringResource(id = item.content),
                onClick = { onClick(item.event) },
            ) {
                if (item is SettingRowItem.ClickableItem) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                        contentDescription = null,
                    )
                } else {
                    Text(
                        text = (item as SettingRowItem.TextItem).text,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            }
        }
    }
}
