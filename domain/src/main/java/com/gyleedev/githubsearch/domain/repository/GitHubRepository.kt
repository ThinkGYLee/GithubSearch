package com.gyleedev.githubsearch.domain.repository

import androidx.paging.PagingData
import com.gyleedev.githubsearch.domain.model.AccessTime
import com.gyleedev.githubsearch.domain.model.FilterStatus
import com.gyleedev.githubsearch.domain.model.GetAccessTokenRepositoryResult
import com.gyleedev.githubsearch.domain.model.RepositoryModel
import com.gyleedev.githubsearch.domain.model.RevokeResult
import com.gyleedev.githubsearch.domain.model.UserFetchResult
import com.gyleedev.githubsearch.domain.model.UserModel
import kotlinx.coroutines.flow.Flow

interface GitHubRepository {
    fun getUsers(): Flow<PagingData<UserModel>>

    fun getUserWithFlow(id: String): Flow<UserModel?>

    suspend fun getLastAccessById(id: String): AccessTime?

    suspend fun fetchUser(id: String): UserFetchResult

    suspend fun fetchRepos(id: String): List<RepositoryModel>

    suspend fun insertUser(userModel: UserModel)

    suspend fun insertRepositoryList(
        userEntityId: Long,
        list: List<RepositoryModel>,
    )

    suspend fun upsertAccessTime(id: Long, githubId: String, isRepoFetched: Boolean = true)

    suspend fun upsertUser(user: UserModel)

    suspend fun getUserId(id: String): Long?

    fun getReposWithFlow(githubId: String): Flow<List<RepositoryModel>>

    fun getFavorites(status: FilterStatus): Flow<PagingData<UserModel>>

    suspend fun getAccessToken(code: String): GetAccessTokenRepositoryResult

    suspend fun resetData()

    suspend fun revokeApplication(): RevokeResult

    suspend fun saveAccessToken(token: String)

    suspend fun hasAccessToken(): Flow<Boolean>

    suspend fun deleteAccessToken()
}
