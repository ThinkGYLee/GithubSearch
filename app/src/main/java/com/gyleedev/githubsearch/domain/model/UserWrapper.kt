package com.gyleedev.githubsearch.domain.model

sealed interface UserWrapper {
    data class FromDatabase(
        val data: UserModel
    ) : UserWrapper

    data class Success(
        val status: SearchStatus,
        val data: UserModel
    ) : UserWrapper

    data class Failure(
        val status: SearchStatus
    ) : UserWrapper
}
