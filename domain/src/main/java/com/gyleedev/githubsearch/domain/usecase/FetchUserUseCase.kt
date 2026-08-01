package com.gyleedev.githubsearch.domain.usecase

import com.gyleedev.githubsearch.domain.model.SearchStatus
import com.gyleedev.githubsearch.domain.model.UserFetchResult
import com.gyleedev.githubsearch.domain.repository.GitHubRepository
import javax.inject.Inject

class FetchUserUseCase
    @Inject
    constructor(
        private val repository: GitHubRepository,
    ) {
        suspend operator fun invoke(query: String): SearchStatus =
            try {
                val result = repository.fetchUser(query)
                if (result is UserFetchResult.Success) {
                    repository.upsertUser(result.user)
                    repository.upsertAccessTime(
                        id = 0L,
                        githubId = result.user.login,
                        isRepoFetched = false,
                    )
                }
                return when (result) {
                    is UserFetchResult.Success -> {
                        SearchStatus.SUCCESS
                    }

                    is UserFetchResult.NoSuchUser -> {
                        SearchStatus.NO_SUCH_USER
                    }

                    is UserFetchResult.ExceedQuota -> {
                        SearchStatus.NEED_AUTHENTICATION
                    }

                    is UserFetchResult.UnknownError -> {
                        SearchStatus.UNKNOWN_FAIL
                    }
                }
            } catch (e: Exception) {
                // TODO 에러별로 다른 스테이트 떨굴것
                SearchStatus.BAD_NETWORK
            }
    }
