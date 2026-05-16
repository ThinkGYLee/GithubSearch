package com.gyleedev.data.repository

import com.gyleedev.data.BuildConfig
import com.gyleedev.data.database.dao.AccessTimeDao
import com.gyleedev.data.database.dao.ReposDao
import com.gyleedev.data.database.dao.UserDao
import com.gyleedev.data.preference.TokenPreference
import com.gyleedev.data.remote.AccessService
import com.gyleedev.data.remote.GithubApiService
import com.gyleedev.data.remote.RevokeService
import com.gyleedev.data.remote.request.RevokeRequest
import com.gyleedev.githubsearch.domain.model.RevokeResult
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.coVerifySequence
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import retrofit2.Response
import java.time.Clock

class RevokeApplicationRepositoryImplTest {
    private lateinit var repository: GitHubRepositoryImpl
    private val userDao: UserDao = mockk()
    private val reposDao: ReposDao = mockk()
    private val accessTimeDao: AccessTimeDao = mockk()
    private val githubApiService: GithubApiService = mockk()
    private val accessService: AccessService = mockk()
    private val revokeService: RevokeService = mockk()
    private val tokenPreference: TokenPreference = mockk()
    private val clock: Clock = mockk()

    private val mockSuccessResponse = Response.success(Unit)
    private val mockErrorResponse = Response.error<Unit>(
        400,
        "Error".toResponseBody("application/json".toMediaTypeOrNull()),
    )

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
    fun `권한 해제 시 토큰이 비어있으면 NO_KEY를 반환한다`() = runTest {
        // Given
        val expectedResult = RevokeResult.NO_KEY
        every { tokenPreference.getString() } returns ""

        // When
        val result = repository.revokeApplication()

        // Then
        assertEquals(expectedResult, result)
        coVerify(exactly = 1) { tokenPreference.getString() }
    }

    @Test
    fun `권한 해제 시 토큰이 존재하고 API 호출에 성공하면 SUCCESS를 반환한다`() = runTest {
        // Given
        val expectedResult = RevokeResult.SUCCESS
        val testToken = "valid_token"
        val revokeRequest = RevokeRequest(testToken)

        every { tokenPreference.getString() } returns testToken
        coEvery { revokeService.revoke(BuildConfig.CLIENT_ID, revokeRequest) } returns mockSuccessResponse

        // When
        val result = repository.revokeApplication()

        // Then
        assertEquals(expectedResult, result)
        coVerifySequence {
            tokenPreference.getString()
            revokeService.revoke(BuildConfig.CLIENT_ID, revokeRequest)
        }
    }

    @Test
    fun `권한 해제 시 토큰이 존재하지만 API 호출에 실패하면 FAIL을 반환한다`() = runTest {
        // Given
        val expectedResult = RevokeResult.FAIL
        val testToken = "valid_token"
        val revokeRequest = RevokeRequest(testToken)

        every { tokenPreference.getString() } returns testToken
        coEvery { revokeService.revoke(BuildConfig.CLIENT_ID, revokeRequest) } returns mockErrorResponse

        // When
        val result = repository.revokeApplication()

        // Then
        assertEquals(expectedResult, result)
        coVerifySequence {
            tokenPreference.getString()
            revokeService.revoke(BuildConfig.CLIENT_ID, revokeRequest)
        }
    }
}
