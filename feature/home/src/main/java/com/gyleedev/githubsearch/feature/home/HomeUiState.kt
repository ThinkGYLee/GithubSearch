package com.gyleedev.githubsearch.feature.home

import androidx.compose.runtime.Stable

enum class HomeMode {
    DEFAULT,
    SEARCH,
    SELECT,
}

@Stable
sealed interface HomeUiState {
    data object Loading : HomeUiState

    data class Success(
        val searchQuery: String,
        val isLoading: Boolean,
        val searchState: SearchUiState,
        val mode: HomeMode,
        val selectedUsers: Set<String>,
        val showRequestAuthDialog: Boolean,
        val showDeleteDialog: Boolean,
    ) : HomeUiState
}

@Stable
sealed interface SearchUiState {
    data object Empty : SearchUiState

    data class Success(
        val name: String?,
        val login: String,
        val bio: String?,
        val avatar: String,
    ) : SearchUiState
}
