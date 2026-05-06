package com.gyleedev.githubsearch.domain.usecase

import com.gyleedev.githubsearch.domain.model.SearchStatus
import com.gyleedev.githubsearch.domain.repository.GitHubRepository
import javax.inject.Inject

class FetchUserUseCase @Inject constructor(
    private val repository: GitHubRepository,
) {
    suspend operator fun invoke(query: String): SearchStatus = try {
        val user = repository.fetchUser(query)
        if (user != null) {
            repository.insertUser(user)
            repository.upsertAccessTime(query, false)
        }
        SearchStatus.SUCCESS
    } catch (e: Exception) {
        // TODO 에러별로 다른 스테이트 떨굴것
        SearchStatus.BAD_NETWORK
    }
}
