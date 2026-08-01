package com.gyleedev.githubsearch.domain.model

sealed interface ResetDataResult {
    data object Fail : ResetDataResult

    data object Success : ResetDataResult
}
