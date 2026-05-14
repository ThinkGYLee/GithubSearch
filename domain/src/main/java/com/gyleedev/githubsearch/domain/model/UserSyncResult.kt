package com.gyleedev.githubsearch.domain.model

sealed interface UserSyncResult {
    data object Fail : UserSyncResult
    data class Success(
        val entityId: Long,
    ) : UserSyncResult
}
