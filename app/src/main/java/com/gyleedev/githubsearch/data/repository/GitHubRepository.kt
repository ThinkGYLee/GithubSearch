package com.gyleedev.githubsearch.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.gyleedev.githubsearch.data.database.dao.AccessTimeDao
import com.gyleedev.githubsearch.data.database.dao.ReposDao
import com.gyleedev.githubsearch.data.database.dao.UserDao
import com.gyleedev.githubsearch.data.database.entity.AccessTime
import com.gyleedev.githubsearch.data.database.entity.toEntity
import com.gyleedev.githubsearch.data.database.entity.toModel
import com.gyleedev.githubsearch.data.paging.UserPagingSource
import com.gyleedev.githubsearch.domain.model.RepositoryModel
import com.gyleedev.githubsearch.domain.model.UserModel
import com.gyleedev.githubsearch.remote.GithubApiService
import com.gyleedev.githubsearch.remote.response.toModel
import java.time.LocalDateTime
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

    //Home에서 user정보를 요청하는 함수
    override suspend fun getUserAtHome(id: String): UserModel? {
        val user = userDao.getUserByGithubId(id).toModel()
        return cachingUserAtHome(user, id)
    }

    //home에서 사용할 user의 db값과 network에 존재하는 값을 캐싱
    private suspend fun cachingUserAtHome(user: UserModel?, id: String): UserModel? {
        //유저정보가 존재할때
        return if (user != null) {
            user
        } else {
            //존재하지 않을 때
            return githubApiService.getUser(id).toModel()
        }
    }


}