package com.gyleedev.githubsearch.domain.usecase

import com.gyleedev.githubsearch.domain.repository.GitHubRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class CheckLoginStatusUseCase @Inject constructor(private val repository: GitHubRepository) {
    suspend operator fun invoke(): Flow<Boolean> = repository.hasAccessToken()
}
