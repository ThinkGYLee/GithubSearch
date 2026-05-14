package com.gyleedev.githubsearch.domain.model

sealed interface UpdateFavoriteResult {
    data object Fail : UpdateFavoriteResult
    data object Success : UpdateFavoriteResult
}
