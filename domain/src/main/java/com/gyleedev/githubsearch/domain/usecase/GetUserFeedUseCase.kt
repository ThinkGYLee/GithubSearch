package com.gyleedev.githubsearch.domain.usecase

import com.gyleedev.githubsearch.domain.model.DetailFeed
import com.gyleedev.githubsearch.domain.model.UserSearchResult
import com.gyleedev.githubsearch.domain.repository.GitHubRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GetUserFeedUseCase
@Inject
constructor(
    private val repository: GitHubRepository,
) {
    suspend operator fun invoke(id: String): List<DetailFeed> = withContext(Dispatchers.IO) {
        val userModel =
            when (val user = repository.getDetailUser(id)) {
                is UserSearchResult.FromDatabase -> {
                    user.data
                }

                is UserSearchResult.Success -> {
                    user.data
                }

                is UserSearchResult.Failure -> {
                    null
                }
            }

        val repos =
            if (userModel != null && userModel.repos > 0) {
                repository.getReposFromDatabase(id)
            } else {
                null
            }

        ModelToFeed.modelToFeed(userModel, repos)
    }
}
