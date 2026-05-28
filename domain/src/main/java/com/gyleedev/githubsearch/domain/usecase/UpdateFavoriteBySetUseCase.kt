package com.gyleedev.githubsearch.domain.usecase

import com.gyleedev.githubsearch.domain.model.UpdateFavoriteResult
import com.gyleedev.githubsearch.domain.repository.GitHubRepository
import javax.inject.Inject

class UpdateFavoriteBySetUseCase @Inject constructor(
    private val repository: GitHubRepository,
) {
    suspend operator fun invoke(ids: Set<String>, favorite: Boolean): UpdateFavoriteResult = try {
        val size = ids.size
        val result = repository.updateFavoriteUsers(ids, favorite)
        if (size == result) {
            UpdateFavoriteResult.Success
        } else {
            UpdateFavoriteResult.Fail
        }
    } catch (e: Exception) {
        UpdateFavoriteResult.Fail
    }
}
