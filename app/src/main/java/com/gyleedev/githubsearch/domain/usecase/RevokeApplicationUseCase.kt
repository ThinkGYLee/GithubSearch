package com.gyleedev.githubsearch.domain.usecase

import com.gyleedev.githubsearch.data.repository.GitHubRepository
import javax.inject.Inject

class RevokeApplicationUseCase @Inject constructor(
    private val repository: GitHubRepository
) {
    suspend operator fun invoke() {
        repository.revokeApplication()
    }
}
