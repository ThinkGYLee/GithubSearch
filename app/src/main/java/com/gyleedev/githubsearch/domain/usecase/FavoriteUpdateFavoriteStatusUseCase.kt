package com.gyleedev.githubsearch.domain.usecase

import com.gyleedev.githubsearch.data.repository.GitHubRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class FavoriteUpdateFavoriteStatusUseCase @Inject constructor(
    private val repository: GitHubRepository
) {
    suspend operator fun invoke(id: String) {
        return withContext(Dispatchers.IO) {
            repository.updateUserFavorite(id)
        }
    }
}
