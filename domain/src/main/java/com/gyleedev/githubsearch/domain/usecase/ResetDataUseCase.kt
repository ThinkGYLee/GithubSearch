package com.gyleedev.githubsearch.domain.usecase

import com.gyleedev.githubsearch.domain.model.ResetDataResult
import com.gyleedev.githubsearch.domain.repository.GitHubRepository
import javax.inject.Inject

class ResetDataUseCase
    @Inject
    constructor(
        private val repository: GitHubRepository,
    ) {
        suspend operator fun invoke(): ResetDataResult =
            try {
                repository.resetUser()
                ResetDataResult.Success
            } catch (e: Exception) {
                ResetDataResult.Fail
            }
    }
