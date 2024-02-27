package com.gyleedev.githubsearch.domain.usecase

import com.gyleedev.githubsearch.data.repository.GitHubRepository
import com.gyleedev.githubsearch.domain.model.FilterStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class FavoriteUseCase @Inject constructor(
    private val repository: GitHubRepository
) {
    fun getFavorites(status: FilterStatus) = repository.getFavorites(status)

    suspend fun update(id: String) {
        return withContext(Dispatchers.IO) {
            repository.updateUser(id)
        }
    }
}