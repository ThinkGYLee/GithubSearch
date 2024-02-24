package com.gyleedev.githubsearch.domain.usecase

import com.gyleedev.githubsearch.data.repository.GitHubRepository
import com.gyleedev.githubsearch.domain.model.UserModel
import javax.inject.Inject

class GetUserUseCase @Inject constructor(
    private val repository: GitHubRepository
) {
    suspend fun execute(user: String): UserModel? = repository.getUser(user)
    fun getUsers() = repository.getUsers()

    suspend fun getUserAtHome(user: String): UserModel? = repository.getUserAtHome(user)
}