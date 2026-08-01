package com.gyleedev.githubsearch.domain.usecase

import com.gyleedev.githubsearch.domain.model.RevokeResult
import com.gyleedev.githubsearch.domain.repository.GitHubRepository
import javax.inject.Inject

class RevokeApplicationUseCase
    @Inject
    constructor(
        private val repository: GitHubRepository,
    ) {
        suspend operator fun invoke(): RevokeResult =
            try {
                val result = repository.revokeApplication()
                if (result == RevokeResult.SUCCESS) {
                    repository.deleteAccessToken()
                }
                result
            } catch (e: Exception) {
                // 어떤 실패인지 추가할 필요 있음
                RevokeResult.FAIL
            }
    }
