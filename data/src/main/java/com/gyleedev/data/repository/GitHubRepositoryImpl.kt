package com.gyleedev.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.gyleedev.data.BuildConfig
import com.gyleedev.data.database.dao.AccessTimeDao
import com.gyleedev.data.database.dao.ReposDao
import com.gyleedev.data.database.dao.UserDao
import com.gyleedev.data.database.entity.AccessTimeEntity
import com.gyleedev.data.database.entity.toEntity
import com.gyleedev.data.database.entity.toModel
import com.gyleedev.data.preference.TokenPreference
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
import com.gyleedev.githubsearch.domain.model.UserModel
import com.gyleedev.githubsearch.domain.repository.GitHubRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import java.time.Clock
import java.time.Instant
import javax.inject.Inject
import com.gyleedev.githubsearch.domain.model.AccessTime as AccessTimeModel

class GitHubRepositoryImpl @Inject constructor(
    private val userDao: UserDao,
    private val reposDao: ReposDao,
    private val accessTimeDao: AccessTimeDao,
    @TypeApi private val githubApiService: GithubApiService,
    @TypeAccess private val accessService: AccessService,
    @TypeRevoke private val revokeService: RevokeService,
    private val tokenPreference: TokenPreference,
    private val clock: Clock,
) : GitHubRepository {
    /*
    세팅
        1. 데이터 리셋 [resetData]
        2. 권한 리셋 [revokeApplication, deleteAccessToken]
        3. 로그인 상태 체크 [hasAccessToken]
    홈
        1. UserList 가져오는거 [getUsers]
        2. SearchBar 에서 query 가지고 debounce 로 flow로 User [getUserWithFlow]
        3. web fetch 해서 User 가져오는거

        Home 에서 필요한 것 User 가져오는것
        1. fetchUser A [fetchUser]
        2. insertUser B [insertUser]
        3. insertAccessTime [upsertAccessTime]
    페이보릿
        1. update favoriteState
            UserUpdate [upsertUser]
        2. favorite 리스트 가져오는거 [getFavorites]
    디테일
        1. user Flow로 받아오는 거 [getUserWithFlow]
        2. repos flow로 받아오는 거 [getReposWithFlow]
        3. user, repo 업데이트(싱크 맞추는 기능)
            1. userId 로 AccessTime 체크 [getLastAccessById]
            2. fetchUser A [fetchUser]
            3. insertUser B [insertUser]
            4. fetchRepos [fetchRepos]
            4. insertRepos [insertRepositoryList]
            5. updateUser C [upsertUser]
            6. updateRepos [insertRepositoryList] replace 되도록 수정
            7. updateAccessTime [upsertAccessTime]
        4. favoriteState 업데이트
            updateUser C [upsertUser]
    메인
        1. 토큰 가져오기 [getAccessToken]
        2. 토큰 저장하기 [saveAccessToken]
     */

    // Home
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
    // flowOn 디스페처 지정을 할때 안할때의 차이? 해야되나 안해도 괜찮나?

    override fun getUserWithFlow(id: String): Flow<UserModel?> = userDao.getUserByGithubId(id).map { it?.toModel() }

    // Favorite
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

    override fun getReposWithFlow(githubId: String): Flow<List<RepositoryModel>> = reposDao.getReposByGithubIdWithFlow(githubId)
        .map { list ->
            list.map { entity ->
                entity.toModel()
            }
        }

    override suspend fun insertUser(userModel: UserModel) {
        userDao.insertUser(userModel.toEntity())
    }

    // 유닛 테스트 코드 짜봐
    override suspend fun upsertAccessTime(id: Long, githubId: String, isRepoFetched: Boolean) {
        val entity = AccessTimeEntity(
            id = id,
            githubId = githubId,
            accessTime = Instant.now(clock),
            isRepoFetched = isRepoFetched,
        )
        accessTimeDao.upsertAccessTime(entity)
    }

    override suspend fun fetchUser(id: String): UserModel? = githubApiService.getUser(id).toModel()

    override suspend fun fetchRepos(id: String): List<RepositoryModel> = githubApiService.getRepos(id)
        .map { response -> response.toModel(id = id) }

    // 마지막 액세스 시간 가져오기
    override suspend fun getLastAccessById(id: String): AccessTimeModel? = accessTimeDao.getTimeByGithubId(id)?.let {
        AccessTimeModel(
            id = it.id,
            githubId = it.githubId,
            accessTime = it.accessTime,
            isRepoFetched = it.isRepoFetched,
        )
    }

    override suspend fun upsertUser(user: UserModel) = userDao.upsertUser(user.toEntity())

    override suspend fun getUserId(id: String): Long? = userDao.getUser(id)?.id

    override suspend fun insertRepositoryList(
        userEntityId: Long,
        list: List<RepositoryModel>,
    ) {
        val mappedList = list.map { model ->
            model.toEntity(
                userEntityId = userEntityId,
            )
        }
        reposDao.insertRepos(mappedList)
    }

    // Main
    override suspend fun getAccessToken(code: String): GithubAccessModel? {
        try {
            val response =
                accessService.getAccessToken(
                    clientId = BuildConfig.CLIENT_ID,
                    clientSecret = BuildConfig.CLIENT_SECRET,
                    code = code,
                )

            if (response.isSuccessful && response.code() == 200) {
                val body = response.body()

                if (body != null) {
                    val accessToken = body.accessToken

                    return GithubAccessModel(accessToken = accessToken)
                }
            }

            return null
        } catch (e: Exception) {
            return null
        }
    }

    override suspend fun saveAccessToken(token: String) {
        tokenPreference.setString(str = token)
    }

    // Setting
    override suspend fun resetData() {
        accessTimeDao.resetAccessTime()
        userDao.resetUser()
        reposDao.resetRepos()
    }

    override suspend fun revokeApplication() {
        val accessToken = tokenPreference.getString()
        if (accessToken.isNotEmpty() || accessToken.isNotBlank()) {
            revokeService.revoke(
                clientId = BuildConfig.CLIENT_ID,
                accessToken = RevokeRequestBody(accessToken).toRequest(),
            )
        }
    }

    override suspend fun hasAccessToken(): Flow<Boolean> = flowOf(tokenPreference.isKeyExist())

    override suspend fun deleteAccessToken() {
        tokenPreference.deleteKey()
    }
}
