package com.gyleedev.githubsearch.domain.usecase

import com.gyleedev.githubsearch.data.repository.GitHubRepository
import com.gyleedev.githubsearch.domain.model.DetailFeed
import com.gyleedev.githubsearch.domain.model.UserWrapper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class DetailGetFeedUseCase @Inject constructor(
    private val repository: GitHubRepository,
) {
    suspend operator fun invoke(id: String): List<DetailFeed> {
        return withContext(Dispatchers.IO) {
            val userModel = when (val user = repository.getDetailUser(id)) {
                is UserWrapper.FromDatabase -> {
                    user.data
                }

                is UserWrapper.Success -> {
                    user.data
                }

                is UserWrapper.Failure -> {
                    null
                }
            }

            val repos = if (userModel != null && userModel.repos > 0) {
                repository.getReposFromDatabase(id)
            } else {
                null
            }

            ModelToFeed.modelToFeed(userModel, repos)
        }
    }
}
