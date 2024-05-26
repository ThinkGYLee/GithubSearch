package com.gyleedev.githubsearch.domain.usecase

import com.gyleedev.githubsearch.data.repository.GitHubRepository
import javax.inject.Inject

class HomeGetUsersUseCase @Inject constructor(
    private val repository: GitHubRepository,
) {
    operator fun invoke() = repository.getUsers()
}
