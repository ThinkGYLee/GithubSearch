package com.gyleedev.githubsearch.domain.model

sealed interface GetAccessTokenRepositoryResult {
    data object Fail : GetAccessTokenRepositoryResult
    data class Success(
        val token: String,
    ) : GetAccessTokenRepositoryResult
}
