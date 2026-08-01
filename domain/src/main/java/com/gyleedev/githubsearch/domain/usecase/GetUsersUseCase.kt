package com.gyleedev.githubsearch.domain.usecase

import com.gyleedev.githubsearch.domain.repository.GitHubRepository
import javax.inject.Inject

class GetUsersUseCase
    @Inject
    constructor(
        private val repository: GitHubRepository,
    ) {
        operator fun invoke() = repository.getUsers()
    }
