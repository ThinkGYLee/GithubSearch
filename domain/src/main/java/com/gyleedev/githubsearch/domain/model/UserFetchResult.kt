package com.gyleedev.githubsearch.domain.model

sealed interface UserFetchResult {
    data object NoSuchUser : UserFetchResult
    data class Success(
        val user: UserModel,
    ) : UserFetchResult
    data object ExceedQuota : UserFetchResult
    data object UnknownError : UserFetchResult
}
