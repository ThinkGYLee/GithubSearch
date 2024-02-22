package com.gyleedev.githubsearch.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.gyleedev.githubsearch.data.database.dao.AccessTimeDao
import com.gyleedev.githubsearch.data.database.dao.ReposDao
import com.gyleedev.githubsearch.data.database.dao.UserDao
import com.gyleedev.githubsearch.data.database.entity.toEntity
import com.gyleedev.githubsearch.data.database.entity.toModel
import com.gyleedev.githubsearch.data.paging.UserPagingSource
import com.gyleedev.githubsearch.domain.model.RepositoryModel
import com.gyleedev.githubsearch.domain.model.UserModel
import com.gyleedev.githubsearch.remote.GithubApiService
import com.gyleedev.githubsearch.remote.response.toModel
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

interface GitHubRepository {
    suspend fun getUser(user: String): UserModel?
    suspend fun getRepositories(user: String): List<RepositoryModel>
    fun getUsers(): Flow<PagingData<UserModel>>
    suspend fun getUserAtHome(id: String): UserModel?
}

class GitHubRepositoryImpl @Inject constructor(
    private val userDao: UserDao,
    private val reposDao: ReposDao,
    private val accessTimeDao: AccessTimeDao,
    private val githubApiService: GithubApiService
) : GitHubRepository {

    override fun getUsers(): Flow<PagingData<UserModel>> {
        return Pager(
            config = PagingConfig(
                pageSize = 10,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { UserPagingSource(userDao) }
        ).flow.map { pagingData ->
            pagingData.map { it.toModel() }
        }


    }


    override suspend fun getUser(user: String): UserModel {
        return withContext(Dispatchers.IO) {
            val userLocal = userDao.getUser(user)
            val response = githubApiService.getUser(user)
            if (userLocal == null) {
                val id = userDao.insertUser(response.toModel().toEntity())
                userDao.getUserById(id).toModel()
            } else {
                userDao.deleteUser(user)
                val id = userDao.insertUser(response.toModel().toEntity())
                userDao.getUserById(id).toModel()
            }
        }
    }

    override suspend fun getRepositories(user: String): List<RepositoryModel> {
        val list = githubApiService.getRepos(user)
        return list.map { it.toModel() }
    }


    override suspend fun getUserAtHome(id: String): UserModel? {
        val user = checkUserInDatabase(id)
        return cachingUserAtHome(user, id)
    }

    private fun checkUserInDatabase(id: String): UserModel? {
        return userDao.getUserByGithubId(id).toModel()
    }

    private suspend fun cachingUserAtHome(user: UserModel?, id: String): UserModel? {
        return if (user != null) {
            user
        } else {
            val response = githubApiService.getUser(id)
            userDao.insertUser(response.toModel().toEntity())
            userDao.getUserByGithubId(id).toModel()
        }
    }


}