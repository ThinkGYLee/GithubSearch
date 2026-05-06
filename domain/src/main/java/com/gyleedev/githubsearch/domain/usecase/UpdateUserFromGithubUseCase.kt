package com.gyleedev.githubsearch.domain.usecase

import com.gyleedev.githubsearch.domain.repository.GitHubRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.Instant
import javax.inject.Inject

private const val ACCESS_TIMEOUT_MS = 3_600_000L

class UpdateUserFromGithubUseCase @Inject constructor(
    private val repository: GitHubRepository,
) {
    suspend operator fun invoke(id: String) = withContext(Dispatchers.IO) {
        val lastAccess = repository.getLastAccessById(id)
        lastAccess?.let { access ->
            val durationSinceLastAccess = Instant.now().toEpochMilli() - access.accessTime.toEpochMilli()
            // 1시간 경과 여부 확인
            val isTimeOut = durationSinceLastAccess >= ACCESS_TIMEOUT_MS
            println(isTimeOut)
            println(!access.isRepoFetched)
            when {
                // 시간이 경과됐을 때
                isTimeOut -> {
                    repository.updateUser(access.id, access.githubId)
                    repository.updateRepos(access.id, access.githubId)
                }

                // 시간 경과와 상관없이 repo 가 fetch 되지 않았을 때
                !access.isRepoFetched -> {
                    repository.updateRepos(access.id, access.githubId)
                }
            }
        }
    }
}
