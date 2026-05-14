package com.gyleedev.githubsearch.domain.usecase

import com.gyleedev.githubsearch.domain.model.ResetDataResult
import com.gyleedev.githubsearch.domain.repository.GitHubRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import javax.inject.Inject

class ResetDataUseCase @Inject constructor(
    private val repository: GitHubRepository,
) {
    suspend operator fun invoke(): ResetDataResult = try {
        coroutineScope {
            val resetAccessTimeTask = async { repository.resetAccessTime() }
            val resetReposTask = async { repository.resetRepos() }
            val resetUserTask = async { repository.resetUser() }
            awaitAll(resetAccessTimeTask, resetReposTask, resetUserTask)
            ResetDataResult.Success
        }
    } catch (e: Exception) {
        ResetDataResult.Fail
    }
}
