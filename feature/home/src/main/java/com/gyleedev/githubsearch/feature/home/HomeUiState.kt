package com.gyleedev.githubsearch.feature.home

import com.gyleedev.githubsearch.domain.model.UserModel

sealed interface HomeUiState {
    data object Loading: HomeUiState

    data class Success(
        val searchQuery: String,
        val searchedUser: UserModel?,
        val isLoading: Boolean,

        ): HomeUiState
}
