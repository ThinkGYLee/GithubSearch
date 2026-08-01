package com.gyleedev.githubsearch.domain.usecase

import com.gyleedev.githubsearch.domain.model.RepositoryModel
import com.gyleedev.githubsearch.domain.repository.GitHubRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class GetReposWithFlowUseCase
    @Inject
    constructor(
        private val repository: GitHubRepository,
    ) {
        operator fun invoke(id: String): Flow<List<RepositoryModel>> =
            try {
                repository.getReposWithFlow(id)
            } catch (e: Exception) {
                flowOf(emptyList())
            }
    }
