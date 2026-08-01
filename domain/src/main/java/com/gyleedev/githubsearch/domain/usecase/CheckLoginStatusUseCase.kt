package com.gyleedev.githubsearch.domain.usecase

import com.gyleedev.githubsearch.domain.repository.GitHubRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class CheckLoginStatusUseCase
    @Inject
    constructor(
        private val repository: GitHubRepository,
    ) {
        suspend operator fun invoke(): Flow<Boolean> = repository.hasAccessToken()
    }
