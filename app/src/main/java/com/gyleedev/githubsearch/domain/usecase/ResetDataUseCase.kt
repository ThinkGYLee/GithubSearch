package com.gyleedev.githubsearch.domain.usecase

import com.gyleedev.githubsearch.data.repository.GitHubRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ResetDataUseCase @Inject constructor(
    private val repository: GitHubRepository
) {
    suspend operator fun invoke() {
        withContext(Dispatchers.IO) {
            repository.resetData()
        }
    }
}
