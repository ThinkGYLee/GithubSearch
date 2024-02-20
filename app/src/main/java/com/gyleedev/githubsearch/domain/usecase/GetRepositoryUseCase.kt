package com.gyleedev.githubsearch.domain.usecase

import com.gyleedev.githubsearch.data.repository.GitHubRepository
import javax.inject.Inject

class GetRepositoryUseCase @Inject constructor(
    private val repository: GitHubRepository
) {
    suspend fun execute(user: String) = repository.getRepositories(user)
}