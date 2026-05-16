package com.gyleedev.data.repository

import com.gyleedev.data.database.dao.AccessTimeDao
import com.gyleedev.data.database.dao.ReposDao
import com.gyleedev.data.database.dao.UserDao
import com.gyleedev.data.database.entity.UserEntity
import com.gyleedev.data.database.entity.toEntity
import com.gyleedev.data.preference.TokenPreference
import com.gyleedev.data.remote.AccessService
import com.gyleedev.data.remote.GithubApiService
import com.gyleedev.data.remote.RevokeService
import com.gyleedev.data.remote.response.UserResponse
import com.gyleedev.data.remote.response.toModel
import com.gyleedev.data.utils.createDummyUserEntity
import com.gyleedev.data.utils.ignoreUnused
import com.gyleedev.githubsearch.domain.model.UserSyncResult
import io.mockk.coEvery
import io.mockk.coVerifySequence
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import retrofit2.Response
import java.time.Clock

class SyncUserDataRepositoryImplTest {
    private lateinit var repository: GitHubRepositoryImpl
    private val userDao: UserDao = mockk()
    private val reposDao: ReposDao = mockk()
    private val accessTimeDao: AccessTimeDao = mockk()
    private val githubApiService: GithubApiService = mockk()
    private val accessService: AccessService = mockk()
    private val revokeService: RevokeService = mockk()
    private val tokenPreference: TokenPreference = mockk()
    private val clock: Clock = mockk()

    @Before
    fun setUp() {
        repository = GitHubRepositoryImpl(
            userDao = userDao,
            reposDao = reposDao,
            accessTimeDao = accessTimeDao,
            githubApiService = githubApiService,
            accessService = accessService,
            revokeService = revokeService,
            tokenPreference = tokenPreference,
            clock = clock,
        )
    }

    @Test
    fun `유저 데이터 동기화 시 API 호출이 성공하면 로컬 즐겨찾기 상태를 유지하며 DB를 업데이트한다`() = runTest {
        // Given
        val githubId = "test_user"
        val remoteUserResponse = UserResponse(
            name = "remote_name",
            login = githubId,
            followers = 100,
            following = 50,
            avatar = "remote_avatar",
            company = "remote_company",
            email = "remote_email",
            bio = "remote_bio",
            repoCount = 10,
            createdDate = "2023-01-01",
            updatedDate = "2023-01-02",
            reposAddress = "remote_repos",
            blogUrl = "remote_blog",
        )
        val localUserEntity: UserEntity = createDummyUserEntity(id = 1L, repoCount = 5).copy(
            githubId = githubId,
            favorite = true, // 로컬에서는 즐겨찾기 상태임
        )

        // UserModel을 거쳐 favorite 상태 유지 후 Entity로 변환
        val expectedMergedEntity: UserEntity = remoteUserResponse.toModel().copy(favorite = true).toEntity()
        val expectedGeneratedId = 100L
        val expectedResponse = Response.success(remoteUserResponse)
        val expectedResult = UserSyncResult.Success(expectedGeneratedId)

        coEvery { githubApiService.getUser(githubId) } returns expectedResponse
        every { userDao.getUserByGithubId(githubId) } returns flowOf(localUserEntity)
        coEvery { userDao.upsertUser(expectedMergedEntity) } returns expectedGeneratedId

        // When
        val result = repository.syncUserData(githubId)

        // Then
        assertEquals(expectedResult, result)
        coVerifySequence {
            githubApiService.getUser(githubId)
            userDao.getUserByGithubId(githubId).ignoreUnused()
            userDao.upsertUser(expectedMergedEntity)
        }
    }

    @Test
    fun `유저 데이터 동기화 시 API 호출이 실패하면 Fail을 반환한다`() = runTest {
        // Given
        val githubId = "test_user"
        val expectedResult = UserSyncResult.Fail
        val mockErrorResponse = Response.error<UserResponse>(404, "".toResponseBody(null))

        coEvery { githubApiService.getUser(githubId) } returns mockErrorResponse

        val localUserEntity = createDummyUserEntity(id = 1L, repoCount = 5).copy(githubId = githubId)
        every { userDao.getUserByGithubId(githubId) } returns flowOf(localUserEntity)

        // When
        val result = repository.syncUserData(githubId)

        // Then
        assertEquals(expectedResult, result)
        coVerifySequence {
            githubApiService.getUser(githubId)
            userDao.getUserByGithubId(githubId).ignoreUnused()
        }
    }
}
