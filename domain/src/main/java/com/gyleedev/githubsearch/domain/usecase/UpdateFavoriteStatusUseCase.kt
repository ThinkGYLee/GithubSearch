package com.gyleedev.githubsearch.domain.usecase

import com.gyleedev.githubsearch.domain.repository.GitHubRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class UpdateFavoriteStatusUseCase
    @Inject
    constructor(
        private val repository: GitHubRepository,
    ) {
        suspend operator fun invoke(id: String) =
            withContext(Dispatchers.IO) {
                repository.updateUserFavorite(id)
            }
    }
