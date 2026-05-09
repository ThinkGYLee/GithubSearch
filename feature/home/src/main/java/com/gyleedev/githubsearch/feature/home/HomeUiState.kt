package com.gyleedev.githubsearch.feature.home

import androidx.compose.runtime.Stable

@Stable
sealed interface HomeUiState {
    data object Loading : HomeUiState

    data class Success(
        val searchQuery: String,
        val isLoading: Boolean,
        val searchState: SearchUiState,
        val isSearchActive: Boolean,
        val showRequestAuthDialog: Boolean,
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
