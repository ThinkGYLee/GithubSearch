package com.gyleedev.githubsearch.domain.usecase

import com.gyleedev.githubsearch.domain.model.UserWrapper
import com.gyleedev.githubsearch.domain.repository.GitHubRepository
import javax.inject.Inject

class SearchUserUseCase
    @Inject
    constructor(
        private val repository: GitHubRepository,
    ) {
        suspend operator fun invoke(user: String): UserWrapper = repository.getUserAtHome(user)
    }
