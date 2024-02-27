package com.gyleedev.githubsearch.domain.usecase

import com.gyleedev.githubsearch.data.repository.GitHubRepository
import javax.inject.Inject

class FavoriteUseCase @Inject constructor(
    private val repository: GitHubRepository
) {
    fun getFavorites() = repository.getFavorites()
}