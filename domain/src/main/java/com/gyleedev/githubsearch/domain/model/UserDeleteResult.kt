package com.gyleedev.githubsearch.domain.model

sealed interface UserDeleteResult {
    data object Fail : UserDeleteResult
    data object Success : UserDeleteResult
}
