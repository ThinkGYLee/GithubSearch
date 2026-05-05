package com.gyleedev.githubsearch.domain.model

sealed interface UserSearchResult {
    data class FromDatabase(
        val data: UserModel,
    ) : UserSearchResult

    data class Success(
        val status: SearchStatus,
        val data: UserModel,
    ) : UserSearchResult

    data class Failure(
        val status: SearchStatus,
    ) : UserSearchResult
}
