package com.gyleedev.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.gyleedev.data.BuildConfig
import com.gyleedev.data.PreferenceUtil
import com.gyleedev.data.database.dao.AccessTimeDao
import com.gyleedev.data.database.dao.ReposDao
import com.gyleedev.data.database.dao.UserDao
import com.gyleedev.data.database.entity.AccessTimeEntity
import com.gyleedev.data.database.entity.UserEntity
import com.gyleedev.data.database.entity.toEntity
import com.gyleedev.data.database.entity.toModel
import com.gyleedev.data.exceptionToStatusUtil
import com.gyleedev.data.remote.AccessService
import com.gyleedev.data.remote.GithubApiService
import com.gyleedev.data.remote.RevokeService
import com.gyleedev.data.remote.TypeAccess
import com.gyleedev.data.remote.TypeApi
import com.gyleedev.data.remote.TypeRevoke
import com.gyleedev.data.remote.request.toRequest
import com.gyleedev.data.remote.response.toModel
import com.gyleedev.githubsearch.domain.model.FilterStatus
import com.gyleedev.githubsearch.domain.model.GithubAccessModel
import com.gyleedev.githubsearch.domain.model.RepositoryModel
import com.gyleedev.githubsearch.domain.model.RevokeRequestBody
import com.gyleedev.githubsearch.domain.model.SearchStatus
import com.gyleedev.githubsearch.domain.model.UserModel
import com.gyleedev.githubsearch.domain.model.UserSearchResult
import com.gyleedev.githubsearch.domain.repository.GitHubRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.time.Instant
import javax.inject.Inject
import com.gyleedev.githubsearch.domain.model.AccessTime as AccessTimeModel

class GitHubRepositoryImpl
@Inject
constructor(
    private val userDao: UserDao,
    private val reposDao: ReposDao,
    private val accessTimeDao: AccessTimeDao,
    @TypeApi private val githubApiService: GithubApiService,
    @TypeAccess private val accessService: AccessService,
    @TypeRevoke private val revokeService: RevokeService,
    private val preferenceUtil: PreferenceUtil,
) : GitHubRepository {
    override fun getUsers(): Flow<PagingData<UserModel>> = Pager(
        config =
        PagingConfig(
            pageSize = 10,
            enablePlaceholders = false,
        ),
        pagingSourceFactory = { userDao.getUsers() },
    ).flow.map { pagingData ->
        pagingData.map {
            it.toModel()
        }
    }

    override fun getFavorites(status: FilterStatus): Flow<PagingData<UserModel>> = Pager(
        config =
        PagingConfig(
            pageSize = 10,
            enablePlaceholders = false,
        ),
        pagingSourceFactory = {
            userDao.getUsers(status)
        },
    ).flow.map { pagingData ->
        pagingData.map { it.toModel() }
    }

    // Home에서 user정보를 요청하는 함수
    override suspend fun getUserAtHome(id: String): UserSearchResult = withContext(Dispatchers.IO) {
        val user = userDao.getUserByGithubId(id)
        if (user != null) {
            UserSearchResult.FromDatabase(data = user.toModel())
        } else {
            getUserFromGithub(id)
        }
    }

    private suspend fun getUserFromGithub(id: String): UserSearchResult = try {
        val userResponse = githubApiService.getUser(id)
        UserSearchResult.Success(
            status = SearchStatus.SUCCESS,
            data = userResponse.toModel(),
        )
    } catch (e: Exception) {
        val status = exceptionToStatusUtil(e)
        UserSearchResult.Failure(
            status = status,
        )
    } catch (e: UnknownError) {
        UserSearchResult.Failure(
            status = SearchStatus.BAD_NETWORK,
        )
    }

    // 마지막 액세스 시간 가져오기
    override suspend fun getLastAccessById(id: String): AccessTimeModel? = accessTimeDao.getTimeByGithubId(id)?.let {
        AccessTimeModel(it.id, it.githubId, it.accessTime)
    }

    // 유저정보 가져오기
    override suspend fun getUser(id: String): UserModel? = withContext(Dispatchers.IO) {
        val user = userDao.getUser(id)
        if (user != null) {
            user.toModel()
        } else {
            null
        }
    }

    // 유저정보 없거나 오래됐을때 깃헙에서 유저정보 가져오기
    private suspend fun insertUserFromGithub(id: String): UserSearchResult = try {
        val userRemote = githubApiService.getUser(id)
        val entityId = userDao.insertUser(userRemote.toModel().toEntity())
        insertRepos(id, entityId)
        updateAccessTime(id)
        UserSearchResult.Success(
            status = SearchStatus.SUCCESS,
            data = userRemote.toModel(),
        )
    } catch (e: Exception) {
        val status = exceptionToStatusUtil(e)
        UserSearchResult.Failure(
            status = status,
        )
    } catch (e: UnknownError) {
        UserSearchResult.Failure(
            status = SearchStatus.BAD_NETWORK,
        )
    }

    private suspend fun updateUserFromGithub(id: String): UserSearchResult {
        try {
            val userResponse = githubApiService.getUser(id)
            val userRemote =
                UserSearchResult.Success(
                    status = SearchStatus.SUCCESS,
                    data = userResponse.toModel(),
                )
            val userLocal = userDao.getUser(id)

            if (userLocal != null) {
                val updateUser =
                    UserEntity(
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
                        favorite = userLocal.favorite,
                    )
                userDao.updateUser(updateUser)
                if (userResponse.repos > 0) {
                    insertRepos(id, userLocal.id)
                }
                updateAccessTime(id)
                return userRemote
            } else {
                return insertUserFromGithub(id)
            }
        } catch (e: Exception) {
            val status = exceptionToStatusUtil(e)
            return UserSearchResult.Failure(
                status = status,
            )
        } catch (e: UnknownError) {
            return UserSearchResult.Failure(
                status = SearchStatus.BAD_NETWORK,
            )
        }
    }

    // 레포정보 삽입
    private suspend fun insertRepos(
        githubId: String,
        userEntityId: Long,
    ) {
        try {
            val response = githubApiService.getRepos(githubId)
            reposDao.deleteRepos(githubId)
            reposDao.insertRepos(response.map { it.toModel(githubId).toEntity(userEntityId) })
        } catch (e: Throwable) {
            insertRepos(githubId, userEntityId)
        }
    }

    // db에서 레포정보 가져오기
    override suspend fun getReposFromDatabase(githubId: String): List<RepositoryModel>? = try {
        reposDao.getReposByGithubId(githubId).map { it.toModel() }
    } catch (e: Throwable) {
        getReposFromDatabase(githubId)
    }

    private fun updateAccessTime(id: String) {
        val accessTime = accessTimeDao.getTimeByGithubId(id)
        if (accessTime != null) {
            accessTimeDao.updateTime(
                AccessTimeEntity(
                    id = accessTime.id,
                    githubId = accessTime.githubId,
                    accessTime = Instant.now(),
                ),
            )
        } else {
            accessTimeDao.insertTime(
                AccessTimeEntity(
                    id = 0,
                    githubId = id,
                    accessTime = Instant.now(),
                ),
            )
        }
    }

    override suspend fun getDetailUser(githubId: String): UserSearchResult = withContext(Dispatchers.IO) {
        val lastAccess = getLastAccessById(githubId)
        if (lastAccess != null) {
            if (Instant.now().toEpochMilli() - lastAccess.accessTime.toEpochMilli() < 3600000) {
                val user = getUser(githubId)
                if (user != null) {
                    UserSearchResult.FromDatabase(
                        data = user,
                    )
                } else {
                    insertUserFromGithub(githubId)
                }
            } else {
                updateUserFromGithub(githubId)
            }
        } else {
            insertUserFromGithub(githubId)
        }
    }

    override suspend fun updateUserFavorite(id: String): UserSearchResult = try {
        val user = userDao.getUser(id)
        if (user != null) {
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
                    favorite = !user.favorite,
                ),
            )
            val updatedUser = userDao.getUser(id)
            if (updatedUser != null) {
                UserSearchResult.FromDatabase(
                    data = updatedUser.toModel(),
                )
            } else {
                UserSearchResult.Failure(status = SearchStatus.BAD_NETWORK)
            }
        } else {
            UserSearchResult.Failure(status = SearchStatus.BAD_NETWORK)
        }
    } catch (e: Exception) {
        UserSearchResult.Failure(status = SearchStatus.BAD_NETWORK)
    }

    override suspend fun getAccessToken(code: String): GithubAccessModel? {
        val response =
            accessService.getAccessToken(
                clientId = BuildConfig.CLIENT_ID,
                clientSecret = BuildConfig.CLIENT_SECRET,
                code = code,
            )
        return if (response.isSuccessful && response.code() == 200) {
            response.body()?.let { GithubAccessModel(it.accessToken) }
        } else {
            null
        }
    }

    override suspend fun resetData() {
        accessTimeDao.resetAccessTime()
        userDao.resetUser()
        reposDao.resetRepos()
    }

    override suspend fun revokeApplication() {
        val accessToken = preferenceUtil.getString(defValue = "")
        if (accessToken.isNotEmpty() || accessToken.isNotBlank()) {
            try {
                revokeService.revoke(
                    clientId = BuildConfig.CLIENT_ID,
                    accessToken = RevokeRequestBody(accessToken).toRequest(),
                )
            } catch (e: Exception) {
                // 예외처리
                println(e)
            }
        }
    }

    override suspend fun saveAccessToken(token: String) {
        preferenceUtil.setString(str = token)
    }

    override suspend fun hasAccessToken(): Flow<Boolean> = flowOf(preferenceUtil.isKeyExist())

    override suspend fun deleteAccessToken() {
        preferenceUtil.deleteKey()
    }
}
