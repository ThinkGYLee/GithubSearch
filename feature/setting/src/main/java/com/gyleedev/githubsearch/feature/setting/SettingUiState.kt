package com.gyleedev.githubsearch.feature.setting

sealed interface SettingUiState {
    data object Loading : SettingUiState

    data class Success(
        val showLanguageDialog: Boolean,
        val showThemeDialog: Boolean,
        val showLoginDialog: Boolean,
        val showLogoutDialog: Boolean,
        val showResetDialog: Boolean,
    ) : SettingUiState
}
