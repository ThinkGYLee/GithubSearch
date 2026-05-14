package com.gyleedev.githubsearch.domain.model

sealed interface UserUpdateResult {
    data object Success : UserUpdateResult
    data object Fail : UserUpdateResult
}
