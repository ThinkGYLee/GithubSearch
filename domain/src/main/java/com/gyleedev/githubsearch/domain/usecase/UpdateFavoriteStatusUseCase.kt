package com.gyleedev.githubsearch.domain.usecase

import com.gyleedev.githubsearch.domain.model.UpdateFavoriteResult
import com.gyleedev.githubsearch.domain.model.UserModel
import com.gyleedev.githubsearch.domain.repository.GitHubRepository
import javax.inject.Inject

class UpdateFavoriteStatusUseCase
    @Inject
    constructor(
        private val repository: GitHubRepository,
    ) {
        suspend operator fun invoke(user: UserModel): UpdateFavoriteResult =
            try {
                val updateUser = user.copy(favorite = !user.favorite)
                val updatedId = repository.upsertUser(updateUser)
                if (updateUser.id == updatedId) {
                    UpdateFavoriteResult.Success
                } else {
                    UpdateFavoriteResult.Fail
                }
            } catch (e: Exception) {
                UpdateFavoriteResult.Fail
            }
    }
