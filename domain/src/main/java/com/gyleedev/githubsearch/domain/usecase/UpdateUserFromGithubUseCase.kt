package com.gyleedev.githubsearch.domain.usecase

import com.gyleedev.githubsearch.domain.model.UserModel
import com.gyleedev.githubsearch.domain.repository.GitHubRepository
import kotlinx.coroutines.flow.first
import java.time.Instant
import javax.inject.Inject

private const val ACCESS_TIMEOUT_MS = 3_600_000L

class UpdateUserFromGithubUseCase @Inject constructor(
    private val repository: GitHubRepository,
) {
    suspend operator fun invoke(id: String) {
        val lastAccess = repository.getLastAccessById(id)
        lastAccess?.let { access ->
            val durationSinceLastAccess = Instant.now().toEpochMilli() - access.accessTime.toEpochMilli()
            // 1시간 경과 여부 확인
            val isTimeOut = durationSinceLastAccess >= ACCESS_TIMEOUT_MS
            when {
                // 시간이 경과됐을 때
                isTimeOut -> {
                    val fetchedUser = repository.fetchUser(id)
                    val localUser = repository.getUserWithFlow(id).first() as UserModel
                    fetchedUser?.let {
                        val insertUser = fetchedUser.copy(favorite = localUser.favorite)
                        repository.upsertUser(insertUser)
                    }
                    val entityId = localUser.id
                    val fetchedRepos = repository.fetchRepos(id)
                    if (fetchedRepos.isNotEmpty()) {
                        repository.insertRepositoryList(entityId, fetchedRepos)
                    }
                    repository.upsertAccessTime(id = access.id, githubId = id, isRepoFetched = true)
                }

                // 시간 경과와 상관없이 repo 가 fetch 되지 않았을 때
                !access.isRepoFetched -> {
                    val entityId = repository.getUserId(id)
                    if (entityId != null) {
                        val fetchedRepos = repository.fetchRepos(id)
                        if (fetchedRepos.isNotEmpty()) {
                            repository.insertRepositoryList(entityId, fetchedRepos)
                        }
                    }
                    repository.upsertAccessTime(id = access.id, githubId = id, isRepoFetched = true)
                }
            }
        }
    }
}
