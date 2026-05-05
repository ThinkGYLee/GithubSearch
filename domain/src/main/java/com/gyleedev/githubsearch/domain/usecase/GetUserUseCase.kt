package com.gyleedev.githubsearch.domain.usecase

import com.gyleedev.githubsearch.domain.model.UserModel
import com.gyleedev.githubsearch.domain.repository.GitHubRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetUserUseCase
@Inject
constructor(
    private val repository: GitHubRepository,
) {
    operator fun invoke(user: String): Flow<UserModel?> = repository.getUserAtHome(user)
}
