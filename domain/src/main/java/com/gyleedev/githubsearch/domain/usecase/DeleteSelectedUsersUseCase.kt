package com.gyleedev.githubsearch.domain.usecase

import com.gyleedev.githubsearch.domain.model.UserDeleteResult
import com.gyleedev.githubsearch.domain.repository.GitHubRepository
import javax.inject.Inject

class DeleteSelectedUsersUseCase
    @Inject
    constructor(
        private val repository: GitHubRepository,
    ) {
        suspend operator fun invoke(userIds: Set<String>): UserDeleteResult =
            try {
                val userCount = userIds.size
                val result = repository.deleteUsersWithSet(userIds)
                if (userCount == result) {
                    UserDeleteResult.Success
                } else {
                    UserDeleteResult.Fail
                }
            } catch (e: Exception) {
                UserDeleteResult.Fail
            }
    }
