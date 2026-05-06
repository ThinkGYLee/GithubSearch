package com.gyleedev.githubsearch.domain.usecase

import com.gyleedev.githubsearch.domain.model.RepositoryModel
import com.gyleedev.githubsearch.domain.repository.GitHubRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetReposWithFlowUseCase @Inject constructor(
    private val repository: GitHubRepository,
) {
    operator fun invoke(id: String): Flow<List<RepositoryModel>> = repository.getReposWithFlow(id)
}
