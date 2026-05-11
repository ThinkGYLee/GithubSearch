package com.gyleedev.githubsearch.feature.favorite

import com.gyleedev.githubsearch.domain.model.FilterStatus

sealed interface FavoriteUiState {
    data object Loading : FavoriteUiState
    data class Success(
        val favoriteDialogState: Boolean,
        val filterDialogState: Boolean,
        val filterState: FilterStatus,
    ) : FavoriteUiState
}
