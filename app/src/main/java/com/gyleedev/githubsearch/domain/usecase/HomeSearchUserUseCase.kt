package com.gyleedev.githubsearch.domain.usecase

import com.gyleedev.githubsearch.data.repository.GitHubRepository
import com.gyleedev.githubsearch.domain.model.UserWrapper
import javax.inject.Inject

class HomeSearchUserUseCase @Inject constructor(
    private val repository: GitHubRepository
) {
    suspend operator fun invoke(user: String): UserWrapper {
        return repository.getUserAtHome(user)
    }
}