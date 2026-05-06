package com.gyleedev.githubsearch.domain.usecase

import com.gyleedev.githubsearch.domain.model.UserModel
import com.gyleedev.githubsearch.domain.repository.GitHubRepository
import javax.inject.Inject

class UpdateFavoriteStatusUseCase @Inject constructor(
    private val repository: GitHubRepository,
) {
    suspend operator fun invoke(user: UserModel) {
        val updateUser = user.copy(favorite = !user.favorite)
        repository.upsertUser(updateUser)
    }
}
