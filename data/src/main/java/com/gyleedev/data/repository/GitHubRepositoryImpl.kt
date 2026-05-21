package com.gyleedev.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
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
import com.gyleedev.data.remote.request.RevokeRequest
import com.gyleedev.data.remote.response.toModel
import com.gyleedev.githubsearch.domain.model.FilterStatus
import com.gyleedev.githubsearch.domain.model.GetAccessTokenRepositoryResult
import com.gyleedev.githubsearch.domain.model.RepositoryModel
import com.gyleedev.githubsearch.domain.model.RevokeResult
import com.gyleedev.githubsearch.domain.model.UserFetchResult
import com.gyleedev.githubsearch.domain.model.UserModel
import com.gyleedev.githubsearch.domain.model.UserSyncResult
import com.gyleedev.githubsearch.domain.repository.GitHubRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
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

    override suspend fun fetchUser(id: String): UserFetchResult {
        val response = githubApiService.getUser(id)

        // 성공 범위(200-299)인 경우 처리
        if (response.isSuccessful) {
            val userResponse = response.body()

            if (userResponse != null) {
                return UserFetchResult.Success(
                    user = userResponse.toModel(),
                )
            }

            return UserFetchResult.UnknownError
        }

        // 성공 범위가 아닌 경우(400-500대) 상태 코드별 처리
        return when (response.code()) {
            // 유저를 찾을 수 없음
            404 -> {
                UserFetchResult.NoSuchUser
            }

            // 기본 할당량 초과
            403 -> {
                UserFetchResult.ExceedQuota
            }

            // 보조 제한(Secondary Rate Limit) 초과
            429 -> {
                UserFetchResult.ExceedQuota
            }

            // 그 외 에러
            else -> {
                UserFetchResult.UnknownError
            }
        }
    }

    override suspend fun fetchRepos(id: String): List<RepositoryModel> {
        val response = githubApiService.getRepos(id)
        val body = response.body()
        return if (response.isSuccessful && body != null) {
            body.map { response ->
                response.toModel(id = id)
            }
        } else {
            emptyList()
        }
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

    override suspend fun syncUserData(githubId: String): UserSyncResult {
        val userFetchResult = fetchUser(githubId)
        val localUser = getUserWithFlow(githubId).first()

        return if (userFetchResult is UserFetchResult.Success && localUser != null) {
            val insertUser = userFetchResult.user.copy(
                id = localUser.id,
                favorite = localUser.favorite,
            )
            val upsertResult = upsertUser(insertUser)

            // upsert 결과가 -1이면 업데이트가 일어난 것이므로 기존 localUser.id 사용
            val finalEntityId = if (upsertResult == -1L) localUser.id else upsertResult

            UserSyncResult.Success(entityId = finalEntityId)
        } else {
            UserSyncResult.Fail
        }
    }

    override suspend fun syncRepoDataList(entityId: Long, githubId: String) {
        val fetchedRepos = fetchRepos(githubId)
        if (fetchedRepos.isNotEmpty()) {
            insertRepositoryList(entityId, fetchedRepos)
        }
    }

    override suspend fun deleteUserById(githubId: String) = userDao.deleteUserById(githubId)

    override suspend fun upsertUser(user: UserModel): Long = userDao.upsertUser(user.toEntity())

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
    override suspend fun getAccessToken(code: String): GetAccessTokenRepositoryResult {
        val response =
            accessService.getAccessToken(code = code)
        return if (response.isSuccessful) {
            val body = response.body()
            if (body != null) {
                val accessToken = body.accessToken
                GetAccessTokenRepositoryResult.Success(token = accessToken)
            } else {
                GetAccessTokenRepositoryResult.Fail
            }
        } else {
            GetAccessTokenRepositoryResult.Fail
        }
    }

    override suspend fun saveAccessToken(token: String) {
        tokenPreference.setString(str = token)
    }

    // Setting
    override suspend fun resetUser() {
        userDao.resetUser()
    }

    override suspend fun revokeApplication(): RevokeResult {
        val accessToken = tokenPreference.getString()
        if (accessToken.isBlank()) {
            return RevokeResult.NO_KEY
        }
        val response = revokeService.revoke(request = RevokeRequest(accessToken))
        return if (response.isSuccessful) {
            RevokeResult.SUCCESS
        } else {
            RevokeResult.FAIL
        }
    }

    override suspend fun hasAccessToken(): Flow<Boolean> = flowOf(tokenPreference.isKeyExist())

    override suspend fun deleteAccessToken() {
        tokenPreference.deleteKey()
    }
}
