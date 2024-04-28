package com.gyleedev.githubsearch.domain.usecase

import com.gyleedev.githubsearch.data.repository.GitHubRepository
import javax.inject.Inject

class ResetApplicationUseCase @Inject constructor(
    private val repository: GitHubRepository
) {
    suspend operator fun invoke() {
        repository.resetApplication()
    }
}