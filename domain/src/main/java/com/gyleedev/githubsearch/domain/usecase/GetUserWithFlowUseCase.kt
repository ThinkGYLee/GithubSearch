package com.gyleedev.githubsearch.domain.usecase

import com.gyleedev.githubsearch.domain.model.UserModel
import com.gyleedev.githubsearch.domain.repository.GitHubRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class GetUserWithFlowUseCase
    @Inject
    constructor(
        private val repository: GitHubRepository,
    ) {
        operator fun invoke(user: String): Flow<UserModel?> =
            try {
                repository.getUserWithFlow(user)
            } catch (e: Exception) {
                flowOf(null)
            }
    }
