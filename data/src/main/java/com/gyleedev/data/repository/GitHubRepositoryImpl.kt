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
import com.gyleedev.data.remote.response.RepoResponse
import com.gyleedev.data.remote.response.UserResponse
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

    override fun getUserAtHome(id: String): Flow<UserModel?> = userDao.getUserByGithubId(id).map { it?.toModel() }

    private suspend fun insertUserToDatabase(userResponse: UserResponse) {
        userDao.insertUser(userResponse.toModel().toEntity())
    }

    override suspend fun fetchUserFromGithub(id: String): SearchStatus = try {
        val userResponse = githubApiService.getUser(id)
        insertUserToDatabase(userResponse)
        updateAccessTime(id = id, isRepoFetched = false)
        SearchStatus.SUCCESS
    } catch (e: Exception) {
        exceptionToStatusUtil(e)
    } catch (e: UnknownError) {
        SearchStatus.BAD_NETWORK
    }

    private suspend fun fetchUserFromGithub1(id: String): UserModel? = try {
        githubApiService.getUser(id).toModel()
    } catch (e: Exception) {
        null
    }

    private suspend fun fetchRepoResponse(id: String): List<RepoResponse> = try {
        githubApiService.getRepos(id)
    } catch (e: Exception) {
        emptyList<RepoResponse>()
    }

    // 마지막 액세스 시간 가져오기
    override suspend fun getLastAccessById(id: String): AccessTimeModel? = accessTimeDao.getTimeByGithubId(id)?.let {
        AccessTimeModel(
            id = it.id,
            githubId = it.githubId,
            accessTime = it.accessTime,
            isRepoFetched = it.isRepoFetched,
        )
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
        updateAccessTime(id, true)
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

    private suspend fun insertRepos1(
        githubId: String,
        userEntityId: Long,
        list: List<RepoResponse>,
    ) {
        try {
            println("insert repos")
            val mappedList = list.map {
                it.toModel(id = githubId).toEntity(userEntityId = userEntityId)
            }.also { println("mappedList ${it.size}") }
            reposDao.deleteRepos(githubId)
            reposDao.insertRepos(mappedList)
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

    override fun getReposFromDatabaseByFlow(githubId: String): Flow<List<RepositoryModel>> = reposDao.getReposByGithubIdWithFlow(githubId).map { it.map { it.toModel() } }

    private suspend fun updateAccessTime(id: String, isRepoFetched: Boolean) {
        println("update")
        val accessTime = accessTimeDao.getTimeByGithubId(id)
        if (accessTime != null) {
            accessTimeDao.updateTime(
                AccessTimeEntity(
                    id = accessTime.id,
                    githubId = accessTime.githubId,
                    accessTime = Instant.now(),
                    isRepoFetched = isRepoFetched,
                ).also { println(it) },
            )
        } else {
            accessTimeDao.insertTime(
                AccessTimeEntity(
                    id = 0,
                    githubId = id,
                    accessTime = Instant.now(),
                    isRepoFetched = false,
                ).also { println(it) },
            )
        }
    }

    override suspend fun updateUser(id: Long, githubId: String) {
        val response = fetchUserFromGithub1(githubId)
        response?.let {
            userDao.updateUser(it.toEntity().copy(id = id))
        }
    }

    override suspend fun updateRepos(id: Long, githubId: String) {
        val response = fetchRepoResponse(githubId).also {
            println("response $it")
        }
        if (response.isNotEmpty()) {
            println("not empty")
            insertRepos1(
                githubId = githubId,
                userEntityId = id,
                list = response,
            )
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
