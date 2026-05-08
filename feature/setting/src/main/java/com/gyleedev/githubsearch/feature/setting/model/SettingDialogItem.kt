package com.gyleedev.githubsearch.feature.setting.model

import androidx.core.os.LocaleListCompat

sealed interface SettingDialogItem {
    data class Theme(
        val content: List<ThemeItem>,
    ) : SettingDialogItem

    data class Language(
        val content: List<LanguageItem>,
    ) : SettingDialogItem
}

data class ThemeItem(
    val type: Int,
    val content: Int,
)

data class LanguageItem(
    val type: LocaleListCompat,
    val content: Int,
)
