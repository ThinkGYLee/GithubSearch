package com.gyleedev.githubsearch.domain.usecase

import com.gyleedev.githubsearch.data.repository.GitHubRepository
import com.gyleedev.githubsearch.domain.model.DetailFeed
import com.gyleedev.githubsearch.domain.model.UserWrapper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject


class DetailUpdateFavoriteStatusUseCase @Inject constructor(
    private val repository: GitHubRepository
) {
    suspend operator fun invoke(id: String): List<DetailFeed> {
        return withContext(Dispatchers.IO) {
            val user = repository.updateUserFavorite(id)
            val repo = repository.getReposFromDatabase(id)
            val userModel = if(user is UserWrapper.FromDatabase) {
                user.data
            } else {
                null
            }
            ModelToFeed.modelToFeed(userModel, repo)
        }
    }
}