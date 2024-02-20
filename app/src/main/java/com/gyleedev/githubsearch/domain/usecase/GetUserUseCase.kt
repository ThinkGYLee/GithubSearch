package com.gyleedev.githubsearch.domain.usecase

import com.gyleedev.githubsearch.data.repository.GitHubRepository
import javax.inject.Inject

class GetUserUseCase @Inject constructor(
    private val repository: GitHubRepository
) {
    suspend fun execute(user: String) = repository.getUser(user)
    fun getUsers() = repository.getUsers()
}