package com.gyleedev.githubsearch.domain.usecase

import com.gyleedev.githubsearch.domain.model.FilterStatus
import com.gyleedev.githubsearch.domain.repository.GitHubRepository
import javax.inject.Inject

class GetFavoritesUseCase
    @Inject
    constructor(
        private val repository: GitHubRepository,
    ) {
        operator fun invoke(status: FilterStatus) = repository.getFavorites(status)
    }
