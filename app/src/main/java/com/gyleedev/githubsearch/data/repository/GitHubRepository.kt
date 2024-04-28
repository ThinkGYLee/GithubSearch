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
import com.gyleedev.githubsearch.data.paging.UserPagingSource
import com.gyleedev.githubsearch.data.remote.AccessService
import com.gyleedev.githubsearch.data.remote.GithubApiService
import com.gyleedev.githubsearch.data.remote.NetworkModule
import com.gyleedev.githubsearch.data.remote.response.GithubAccessResponse
import com.gyleedev.githubsearch.data.remote.response.toModel
import com.gyleedev.githubsearch.domain.model.FilterStatus
import com.gyleedev.githubsearch.domain.model.RepositoryModel
import com.gyleedev.githubsearch.domain.model.SearchStatus
import com.gyleedev.githubsearch.domain.model.UserModel
import com.gyleedev.githubsearch.domain.model.UserWrapper
import com.gyleedev.githubsearch.util.exceptionToStatusUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import retrofit2.Response
import java.time.Instant
import javax.inject.Inject

interface GitHubRepository {
    fun getUsers(): Flow<PagingData<UserModel>>
    suspend fun getUserAtHome(id: String): UserWrapper
    suspend fun getLastAccessById(id: String): AccessTime?
    suspend fun getUser(id: String): UserModel?
    suspend fun getReposFromDatabase(githubId: String): List<RepositoryModel>?
    suspend fun getDetailUser(githubId: String): UserWrapper
    suspend fun updateUserFavorite(id: String): UserWrapper
    fun getFavorites(status: FilterStatus): Flow<PagingData<UserModel>>

    suspend fun getAccessToken(
        id: String,
        secret: String,
        code: String
    ): Response<GithubAccessResponse>

    suspend fun resetApplication()
}

class GitHubRepositoryImpl @Inject constructor(
    private val userDao: UserDao,
    private val reposDao: ReposDao,
    private val accessTimeDao: AccessTimeDao,
    @NetworkModule.TypeApi private val githubApiService: GithubApiService,
    @NetworkModule.TypeAccess private val accessService: AccessService
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
            pagingSourceFactory = {
                userDao.getUsers(status)
            }
        ).flow.map { pagingData ->
            pagingData.map { it.toModel() }
        }
    }

    //Home에서 user정보를 요청하는 함수
    override suspend fun getUserAtHome(id: String): UserWrapper {
        return withContext(Dispatchers.IO) {
            try {
                val user = userDao.getUserByGithubId(id)
                UserWrapper.FromDatabase(data = user.toModel())
            } catch (e: NullPointerException) {
                getUserFromGithub(id)
            }
        }
    }

    private suspend fun getUserFromGithub(id: String): UserWrapper {
        return try {
            val userResponse = githubApiService.getUser(id)
            UserWrapper.Success(
                status = SearchStatus.SUCCESS,
                data = userResponse.toModel()
            )
        } catch (e: Exception) {
            val status = exceptionToStatusUtil(e)
            UserWrapper.Failure(
                status = status
            )
        } catch (e: UnknownError) {
            UserWrapper.Failure(
                status = SearchStatus.BAD_NETWORK
            )
        }
    }

    //마지막 액세스 시간 가져오기
    override suspend fun getLastAccessById(id: String): AccessTime? {
        return accessTimeDao.getTimeByGithubId(id)
    }

    //유저정보 가져오기
    override suspend fun getUser(id: String): UserModel {
        return withContext(Dispatchers.IO) {
            userDao.getUser(id).toModel()
        }
    }

    //유저정보 없거나 오래됐을때 깃헙에서 유저정보 가져오기
    private suspend fun insertUserFromGithub(id: String): UserWrapper {
        return try {
            val userRemote = githubApiService.getUser(id)
            val entityId = userDao.insertUser(userRemote.toModel().toEntity())
            insertRepos(id, entityId)
            updateAccessTime(id)
            UserWrapper.Success(
                status = SearchStatus.SUCCESS,
                data = userRemote.toModel()
            )
        } catch (e: Exception) {
            val status = exceptionToStatusUtil(e)
            UserWrapper.Failure(
                status = status
            )
        } catch (e: UnknownError) {
            UserWrapper.Failure(
                status = SearchStatus.BAD_NETWORK
            )
        }
    }

    private suspend fun updateUserFromGithub(id: String): UserWrapper {

        try {
            val userResponse = githubApiService.getUser(id)
            val userRemote = UserWrapper.Success(
                status = SearchStatus.SUCCESS,
                data = userResponse.toModel()
            )
            val userLocal = userDao.getUser(id)

            val updateUser = UserEntity(
                id = userLocal.id,
                userId = userRemote.data.login,
                name = userRemote.data.name,
                followers = userRemote.data.followers,
                following = userRemote.data.following,
                avatar = userRemote.data.avatar,
                company = userRemote.data.company,
                email = userRemote.data.email,
                bio = userRemote.data.bio,
                blogUrl = userRemote.data.blogUrl,
                createdDate = userRemote.data.createdDate,
                updatedDate = userRemote.data.updatedDate,
                repos = userRemote.data.repos,
                reposAddress = userRemote.data.reposAddress,
                favorite = userLocal.favorite
            )
            userDao.updateUser(updateUser)
            if(userResponse.repos > 0) {
                insertRepos(id, userLocal.id)
            }
            updateAccessTime(id)
            return userRemote
        } catch (e: Exception) {
            val status = exceptionToStatusUtil(e)
            return UserWrapper.Failure(
                status = status
            )
        } catch (e: UnknownError) {
            return UserWrapper.Failure(
                status = SearchStatus.BAD_NETWORK
            )
        }
    }

    //레포정보 삽입
    private suspend fun insertRepos(githubId: String, userEntityId: Long) {
        try {
            val response = githubApiService.getRepos(githubId)
            reposDao.deleteRepos(githubId)
            reposDao.insertRepos(response.map { it.toModel(githubId).toEntity(userEntityId) })
        } catch (e: Throwable) {
            insertRepos(githubId, userEntityId)
        }
    }

    //db에서 레포정보 가져오기
    override suspend fun getReposFromDatabase(githubId: String): List<RepositoryModel> {
        return try {
            reposDao.getReposByGithubId(githubId).map { it.toModel() }
        } catch (e: Throwable) {
            getReposFromDatabase(githubId)
        }
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

    override suspend fun getDetailUser(githubId: String): UserWrapper {
        return withContext(Dispatchers.IO) {
            val lastAccess = getLastAccessById(githubId)
            if (lastAccess != null) {
                if (Instant.now().toEpochMilli() - lastAccess.accessTime.toEpochMilli() < 3600000) {
                    UserWrapper.FromDatabase(
                        data = getUser(githubId)
                    )
                } else {
                    updateUserFromGithub(githubId)
                }
            } else {
                insertUserFromGithub(githubId)
            }
        }
    }

    override suspend fun updateUserFavorite(id: String): UserWrapper {
        return try {
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
            UserWrapper.FromDatabase(
                data = userDao.getUser(id).toModel()
            )
        } catch (e: Throwable) {
            updateUserFavorite(id)
        }

    }

    override suspend fun getAccessToken(
        id: String,
        secret: String,
        code: String
    ) = accessService.getAccessToken(clientId = id, clientSecret = secret, code = code)

    override suspend fun resetApplication() {
        withContext(Dispatchers.IO) {
            userDao.resetUsers()
            accessTimeDao.resetTime()
        }
    }
}