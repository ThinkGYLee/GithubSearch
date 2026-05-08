package com.gyleedev.githubsearch.feature.setting.model

import androidx.compose.ui.graphics.vector.ImageVector

sealed interface SettingItem {
    data class Card(
        val items: List<SettingRowItem>,
    ) : SettingItem

    data class Title(
        val text: Int,
    ) : SettingItem
}

sealed class SettingRowItem(
    open val icon: ImageVector,
    open val content: Int,
    open val event: SettingEvent,
) {
    data class ClickableItem(
        override val icon: ImageVector,
        override val content: Int,
        override val event: SettingEvent,
    ) : SettingRowItem(icon, content, event)

    data class TextItem(
        override val icon: ImageVector,
        override val content: Int,
        override val event: SettingEvent,
        val text: String,
    ) : SettingRowItem(icon, content, event)
}
