package com.gyleedev.githubsearch.domain.usecase

import com.gyleedev.githubsearch.domain.model.DetailFeed
import com.gyleedev.githubsearch.domain.model.UserWrapper
import com.gyleedev.githubsearch.domain.repository.GitHubRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class UpdateFavoriteStatusAndRefreshFeedUseCase
    @Inject
    constructor(
        private val repository: GitHubRepository,
    ) {
        suspend operator fun invoke(id: String): List<DetailFeed> =
            withContext(Dispatchers.IO) {
                val user = repository.updateUserFavorite(id)
                val repo = repository.getReposFromDatabase(id)
                val userModel =
                    if (user is UserWrapper.FromDatabase) {
                        user.data
                    } else {
                        null
                    }
                ModelToFeed.modelToFeed(userModel, repo)
            }
    }
