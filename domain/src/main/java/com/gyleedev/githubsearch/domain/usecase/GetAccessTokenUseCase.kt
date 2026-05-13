package com.gyleedev.githubsearch.domain.usecase

import com.gyleedev.githubsearch.domain.model.GetAccessTokenRepositoryResult
import com.gyleedev.githubsearch.domain.model.GetAccessTokenUseCaseResult
import com.gyleedev.githubsearch.domain.repository.GitHubRepository
import javax.inject.Inject

class GetAccessTokenUseCase @Inject constructor(
    private val repository: GitHubRepository,
) {
    suspend operator fun invoke(code: String): GetAccessTokenUseCaseResult = try {
        val result = repository.getAccessToken(code = code)
        if (result is GetAccessTokenRepositoryResult.Success) {
            repository.saveAccessToken(token = result.token)
            GetAccessTokenUseCaseResult.Success
        } else {
            GetAccessTokenUseCaseResult.Fail
        }
    } catch (e: Exception) {
        GetAccessTokenUseCaseResult.Fail
    }
}
