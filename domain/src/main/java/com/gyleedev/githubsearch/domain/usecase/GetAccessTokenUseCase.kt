package com.gyleedev.githubsearch.domain.usecase

import com.gyleedev.githubsearch.domain.repository.GitHubRepository
import javax.inject.Inject

class GetAccessTokenUseCase @Inject constructor(
    private val repository: GitHubRepository,
) {
    suspend operator fun invoke(code: String): Boolean {
        val response = repository.getAccessToken(code = code)

        return if (response != null) {
            repository.saveAccessToken(token = response.accessToken)
            true
        } else {
            false
        }
    }
}
