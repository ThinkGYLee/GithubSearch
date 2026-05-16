package com.gyleedev.data.repository

import com.gyleedev.data.database.dao.AccessTimeDao
import com.gyleedev.data.database.dao.ReposDao
import com.gyleedev.data.database.dao.UserDao
import com.gyleedev.data.preference.TokenPreference
import com.gyleedev.data.remote.AccessService
import com.gyleedev.data.remote.GithubApiService
import com.gyleedev.data.remote.RevokeService
import com.gyleedev.data.remote.response.GithubAccessResponse
import com.gyleedev.githubsearch.domain.model.GetAccessTokenRepositoryResult
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import retrofit2.Response
import java.time.Clock

class GetAccessTokenRepositoryImplTest {
    private lateinit var repository: GitHubRepositoryImpl
    private val userDao: UserDao = mockk()
    private val reposDao: ReposDao = mockk()
    private val accessTimeDao: AccessTimeDao = mockk()
    private val githubApiService: GithubApiService = mockk()
    private val accessService: AccessService = mockk()
    private val revokeService: RevokeService = mockk()
    private val tokenPreference: TokenPreference = mockk()
    private val clock: Clock = mockk()
    private val mockCode = "mocked_code"
    private val mockToken = "mocked_token"
    private val mockSuccessResponse = Response.success(GithubAccessResponse(mockToken))
    private val mockSuccessNoBodyResponse = Response.success<GithubAccessResponse>(null)
    private val mockFailResponse = Response.error<GithubAccessResponse>(404, "".toResponseBody(null))
    private val mockSuccess = GetAccessTokenRepositoryResult.Success(mockToken)
    private val mockFail = GetAccessTokenRepositoryResult.Fail

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
    fun `api 결과가 성공이고 body가 있을 때`() = runTest {
        // Given
        val expectedResponse = mockSuccessResponse
        val expectedResult = mockSuccess
        coEvery { accessService.getAccessToken(code = mockCode) } returns expectedResponse

        // When
        val actualResult = repository.getAccessToken(mockCode)

        // Then
        assertEquals(expectedResult, actualResult)
        coVerify(exactly = 1) { accessService.getAccessToken(code = mockCode) }
    }

    @Test
    fun `api 결과가 성공이고 body가 없을 때`() = runTest {
        // Given
        val expectedResponse = mockSuccessNoBodyResponse
        val expectedResult = mockFail
        coEvery { accessService.getAccessToken(code = mockCode) } returns expectedResponse

        // When
        val actualResult = repository.getAccessToken(mockCode)

        // Then
        assertEquals(expectedResult, actualResult)
        coVerify(exactly = 1) { accessService.getAccessToken(code = mockCode) }
    }

    @Test
    fun `api 결과가 실패일 때`() = runTest {
        // Given
        val expectedResponse = mockFailResponse
        val expectedResult = mockFail
        coEvery { accessService.getAccessToken(code = mockCode) } returns expectedResponse

        // When
        val actualResult = repository.getAccessToken(mockCode)

        // Then
        assertEquals(expectedResult, actualResult)
        coVerify(exactly = 1) { accessService.getAccessToken(code = mockCode) }
    }
}
