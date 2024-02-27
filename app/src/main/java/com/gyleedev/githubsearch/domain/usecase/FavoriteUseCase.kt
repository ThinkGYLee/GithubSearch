package com.gyleedev.githubsearch.domain.usecase

import com.gyleedev.githubsearch.data.repository.GitHubRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class FavoriteUseCase @Inject constructor(
    private val repository: GitHubRepository
) {
    fun getFavorites() = repository.getFavorites()

    suspend fun update(id: String) {
        return withContext(Dispatchers.IO) {
            repository.updateUser(id)
        }
    }
}