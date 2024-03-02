package com.gyleedev.githubsearch.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.gyleedev.githubsearch.data.database.dao.AccessTimeDao
import com.gyleedev.githubsearch.data.database.dao.ReposDao
import com.gyleedev.githubsearch.data.database.dao.UserDao
import com.gyleedev.githubsearch.data.database.entity.AccessTime
import com.gyleedev.githubsearch.data.database.entity.UserEntity
import com.gyleedev.githubsearch.data.database.entity.toEntity
import com.gyleedev.githubsearch.data.database.entity.toModel
import com.gyleedev.githubsearch.data.paging.FavoritePagingSource
import com.gyleedev.githubsearch.data.paging.UserPagingSource
import com.gyleedev.githubsearch.domain.model.FilterStatus
import com.gyleedev.githubsearch.domain.model.RepositoryModel
import com.gyleedev.githubsearch.domain.model.UserModel
import com.gyleedev.githubsearch.data.remote.GithubApiService
import com.gyleedev.githubsearch.data.remote.response.toModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.time.Instant
import javax.inject.Inject

interface GitHubRepository {

    suspend fun getRepositories(user: String): List<RepositoryModel>
    fun getUsers(): Flow<PagingData<UserModel>>
    suspend fun getUserAtHome(id: String): UserModel?
    suspend fun getLastAccessById(id: String): AccessTime?
    suspend fun getUser(id: String): UserModel?
    suspend fun getReposFromDatabase(githubId: String): List<RepositoryModel>?
    suspend fun getDetailUser(githubId: String): UserModel?
    suspend fun updateUserFavorite(id: String): UserModel?
    fun getFavorites(status: FilterStatus): Flow<PagingData<UserModel>>
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

    override fun getFavorites(status: FilterStatus): Flow<PagingData<UserModel>> {
        return Pager(config = PagingConfig(
            pageSize = 10,
            enablePlaceholders = false
        ),
            pagingSourceFactory = { FavoritePagingSource(userDao, status) }
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
        return withContext(Dispatchers.IO) {
            val user = userDao.getUserByGithubId(id)
            cachingUserAtHome(user, id)
        }
    }

    //home에서 사용할 user의 db값과 network에 존재하는 값을 캐싱
    private suspend fun cachingUserAtHome(user: UserEntity?, id: String): UserModel? {
        //유저정보가 존재할때
        return user?.toModel() ?: //존재하지 않을 때
        return githubApiService.getUser(id).toModel()
    }

    //마지막 액세스 시간 가져오기
    override suspend fun getLastAccessById(id: String): AccessTime? {
        return accessTimeDao.getTimeByGithubId(id)
    }

    //유저정보 가져오기
    override suspend fun getUser(id: String): UserModel? {
        return withContext(Dispatchers.IO) {
            userDao.getUser(id).toModel()
        }
    }

    //유저정보 없거나 오래됐을때 깃헙에서 유저정보 가져오기
    private suspend fun insertUserFromGithub(id: String): UserModel? {
        val userRemote = githubApiService.getUser(id)
        val entityId = userDao.insertUser(userRemote.toModel().toEntity())
        insertRepos(id, entityId)
        updateAccessTime(id)
        return userRemote.toModel()
    }


    private suspend fun updateUserFromGithub(id: String): UserModel? {
        val userRemote = githubApiService.getUser(id)
        val userLocal = userDao.getUser(id)

        val updateUser = UserEntity(
            id = userLocal.id,
            userId = userRemote.login,
            name = userRemote.name,
            followers = userRemote.followers,
            following = userRemote.following,
            avatar = userRemote.avatar,
            company = userRemote.company,
            email = userRemote.email,
            bio = userRemote.bio,
            blogUrl = userRemote.blogUrl,
            createdDate = userRemote.createdDate,
            updatedDate = userRemote.updatedDate,
            repos = userRemote.repos,
            reposAddress = userRemote.reposAddress,
            favorite = userLocal.favorite
        )

        if (userLocal == null) {
            userDao.insertUser(userRemote.toModel().toEntity())

        } else {
            userDao.updateUser(
                updateUser
            )
        }
        insertRepos(id, userLocal.id)
        updateAccessTime(id)
        return updateUser.toModel()
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

    private fun updateAccessTime(id: String) {
        val accessTime = accessTimeDao.getTimeByGithubId(id)
        if (accessTime != null) {
            accessTimeDao.updateTime(
                AccessTime(
                    id = accessTime.id,
                    githubId = accessTime.githubId,
                    accessTime = Instant.now()
                )
            )
        } else {
            accessTimeDao.insertTime(
                AccessTime(
                    id = 0,
                    githubId = id,
                    accessTime = Instant.now()
                )
            )
        }
    }

    override suspend fun getDetailUser(githubId: String): UserModel? {
        return withContext(Dispatchers.IO) {
            val lastAccess = getLastAccessById(githubId)
            if (lastAccess != null) {
                if (Instant.now().toEpochMilli() - lastAccess.accessTime.toEpochMilli() < 3600000) {
                    getUser(githubId)
                } else {
                    updateUserFromGithub(githubId)
                }
            } else {
                insertUserFromGithub(githubId)
            }
        }
    }

    override suspend fun updateUserFavorite(id: String): UserModel? {

        val user = userDao.getUser(id)
        userDao.updateUser(
            UserEntity(
                id = user.id,
                userId = user.userId,
                name = user.name,
                followers = user.followers,
                following = user.following,
                company = user.company,
                avatar = user.avatar,
                email = user.email,
                bio = user.bio,
                repos = user.repos,
                createdDate = user.createdDate,
                updatedDate = user.updatedDate,
                reposAddress = user.reposAddress,
                blogUrl = user.blogUrl,
                favorite = !user.favorite
            )
        )
        return userDao.getUser(id).toModel()

    }


}