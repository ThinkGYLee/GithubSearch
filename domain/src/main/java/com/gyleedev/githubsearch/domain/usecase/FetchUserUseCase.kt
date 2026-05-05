package com.gyleedev.githubsearch.domain.usecase

import com.gyleedev.githubsearch.domain.model.SearchStatus
import com.gyleedev.githubsearch.domain.repository.GitHubRepository
import javax.inject.Inject

class FetchUserUseCase @Inject constructor(
    private val repository: GitHubRepository,
) {
    suspend operator fun invoke(query: String): SearchStatus = repository.fetchUserFromGithub(query)
}
