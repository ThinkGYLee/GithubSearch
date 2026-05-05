package com.gyleedev.githubsearch.domain.repository

import androidx.paging.PagingData
import com.gyleedev.githubsearch.domain.model.AccessTime
import com.gyleedev.githubsearch.domain.model.FilterStatus
import com.gyleedev.githubsearch.domain.model.GithubAccessModel
import com.gyleedev.githubsearch.domain.model.RepositoryModel
import com.gyleedev.githubsearch.domain.model.SearchStatus
import com.gyleedev.githubsearch.domain.model.UserModel
import com.gyleedev.githubsearch.domain.model.UserSearchResult
import kotlinx.coroutines.flow.Flow

interface GitHubRepository {
    fun getUsers(): Flow<PagingData<UserModel>>

    fun getUserAtHome(id: String): Flow<UserModel?>

    suspend fun fetchUserFromGithub(id: String): SearchStatus

    suspend fun getLastAccessById(id: String): AccessTime?

    suspend fun getUser(id: String): UserModel?

    suspend fun getReposFromDatabase(githubId: String): List<RepositoryModel>?

    suspend fun getDetailUser(githubId: String): UserSearchResult

    suspend fun updateUserFavorite(id: String): UserSearchResult

    fun getFavorites(status: FilterStatus): Flow<PagingData<UserModel>>

    suspend fun getAccessToken(code: String): GithubAccessModel?

    suspend fun resetData()

    suspend fun revokeApplication()

    suspend fun saveAccessToken(token: String)

    suspend fun hasAccessToken(): Flow<Boolean>

    suspend fun deleteAccessToken()
}
