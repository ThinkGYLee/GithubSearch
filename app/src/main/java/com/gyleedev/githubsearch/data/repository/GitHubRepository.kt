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
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

interface GitHubRepository {

    suspend fun getRepositories(user: String): List<RepositoryModel>
    fun getUsers(): Flow<PagingData<UserModel>>
    suspend fun getUserAtHome(id: String): UserModel?
    suspend fun getLastAccessById(id: String): AccessTime?
    suspend fun getUser(id: String): UserModel?
    suspend fun getUserFromGithub(id: String): UserModel?
    suspend fun getReposFromDatabase(githubId: String): List<RepositoryModel>?


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

    override suspend fun getRepositories(user: String): List<RepositoryModel> {
        val list = githubApiService.getRepos(user)
        return list.map { it.toModel(user) }
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

    //마지막 액세스 시간 가져오기
    override suspend fun getLastAccessById(id: String): AccessTime? {
        return accessTimeDao.getTimeByGithubId(id)
    }
    //유저정보 가져오기
    override suspend fun getUser(id: String): UserModel? {
        return userDao.getUser(id).toModel()
    }
    //유저정보 없거나 오래됐을때 깃헙에서 유저정보 가져오기
    override suspend fun getUserFromGithub(id: String): UserModel? {
        userDao.deleteUser(id)
        val user = githubApiService.getUser(id)
        val userEntityId = userDao.insertUser(user.toModel().toEntity())
        insertRepos(id, userEntityId)
        return user.toModel()
    }
    //레포정보 삽입
    private suspend fun insertRepos(githubId: String, userEntityId: Long) {
        reposDao.deleteRepos(githubId)
        val respond = githubApiService.getRepos(githubId)
        if (respond != null) {
            reposDao.insertRepos(respond.map { it.toModel(githubId).toEntity(userEntityId) })
        }
    }
    //db에서 레포정보 가져오기
    override suspend fun getReposFromDatabase(githubId: String): List<RepositoryModel>? {
        return reposDao.getReposByGithubId(githubId).map { it.toModel() }
    }


}