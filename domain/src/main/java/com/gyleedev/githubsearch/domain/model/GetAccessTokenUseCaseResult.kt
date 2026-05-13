package com.gyleedev.githubsearch.domain.model

sealed interface GetAccessTokenUseCaseResult {
    data object Fail : GetAccessTokenUseCaseResult
    data object Success : GetAccessTokenUseCaseResult
}
