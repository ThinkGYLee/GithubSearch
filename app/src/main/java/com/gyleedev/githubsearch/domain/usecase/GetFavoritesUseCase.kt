package com.gyleedev.githubsearch.domain.usecase

import com.gyleedev.githubsearch.data.repository.GitHubRepository
import com.gyleedev.githubsearch.domain.model.FilterStatus
import javax.inject.Inject

class GetFavoritesUseCase @Inject constructor(
    private val repository: GitHubRepository
) {
    operator fun invoke(status: FilterStatus) = repository.getFavorites(status)
}