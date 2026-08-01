package com.gyleedev.data.repository

import com.gyleedev.data.database.dao.AccessTimeDao
import com.gyleedev.data.database.dao.ReposDao
import com.gyleedev.data.database.dao.UserDao
import com.gyleedev.data.preference.TokenPreference
import com.gyleedev.data.remote.AccessService
import com.gyleedev.data.remote.GithubApiService
import com.gyleedev.data.remote.RevokeService
import com.gyleedev.data.remote.response.UserResponse
import com.gyleedev.githubsearch.domain.model.UserFetchResult
import com.gyleedev.githubsearch.domain.model.UserModel
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import retrofit2.Response
import java.time.Clock
import kotlinx.coroutines.test.runTest

class FetchUserRepositoryImplTest {
    private lateinit var repository: GitHubRepositoryImpl
    private val userDao: UserDao = mockk()
    private val reposDao: ReposDao = mockk()
    private val accessTimeDao: AccessTimeDao = mockk()
    private val githubApiService: GithubApiService = mockk()
    private val accessService: AccessService = mockk()
    private val revokeService: RevokeService = mockk()
    private val tokenPreference: TokenPreference = mockk()
    private val clock: Clock = mockk()

    private val mockModel =
        UserModel(
            id = 0L,
            name = "test_name",
            login = "test_user",
            followers = 123,
            following = 234,
            avatar = "test_avatar",
            company = "test_company",
            email = "test_email",
            bio = "test_bio",
            repoCount = 8,
            createdDate = "test_created_date",
            updatedDate = "test_updated_date",
            reposAddress = "test_repos_address",
            blogUrl = "test_blog_url",
            favorite = false,
        )

    private val mockResponse =
        UserResponse(
            name = "test_name",
            login = "test_user",
            followers = 123,
            following = 234,
            avatar = "test_avatar",
            company = "test_company",
            email = "test_email",
            bio = "test_bio",
            repoCount = 8,
            createdDate = "test_created_date",
            updatedDate = "test_updated_date",
            reposAddress = "test_repos_address",
            blogUrl = "test_blog_url",
        )

    private val mockSuccess = Response.success(mockResponse)
    private val mockSuccessNoBody = Response.success<UserResponse>(null)
    private val mockNoUser = Response.error<UserResponse>(404, "".toResponseBody(null))
    private val mockExceedQuota = Response.error<UserResponse>(403, "".toResponseBody(null))
    private val mockExceedLimit = Response.error<UserResponse>(429, "".toResponseBody(null))
    private val mockUnknownError = Response.error<UserResponse>(503, "".toResponseBody(null))

    @Before
    fun setUp() {
        repository =
            GitHubRepositoryImpl(
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
    fun `Api 결과가 성공이고 body가 존재할 때`() =
        runTest {
            // Given
            val givenId = "test_user"
            val expectedResult = UserFetchResult.Success(mockModel)
            coEvery { githubApiService.getUser(givenId) } returns mockSuccess

            // When
            val actualResult = repository.fetchUser(givenId)

            // Then
            assertEquals(expectedResult, actualResult)
            coVerify(exactly = 1) { githubApiService.getUser(givenId) }
        }

    @Test
    fun `Api 결과가 성공이고 body가 null 일 때`() =
        runTest {
            // Given
            val givenId = "test_user"
            val expectedResult = UserFetchResult.UnknownError
            coEvery { githubApiService.getUser(givenId) } returns mockSuccessNoBody

            // When
            val actualResult = repository.fetchUser(givenId)

            // Then
            assertEquals(expectedResult, actualResult)
            coVerify(exactly = 1) { githubApiService.getUser(givenId) }
        }

    @Test
    fun `Api 검색 결과가 없을 때`() =
        runTest {
            // Given
            val givenId = "test_user"
            val expectedResult = UserFetchResult.NoSuchUser
            coEvery { githubApiService.getUser(givenId) } returns mockNoUser

            // When
            val actualResult = repository.fetchUser(givenId)

            // Then
            assertEquals(expectedResult, actualResult)
            coVerify(exactly = 1) { githubApiService.getUser(givenId) }
        }

    @Test
    fun `Api 리밋 초과 코드 403`() =
        runTest {
            // Given
            val givenId = "test_user"
            val expectedResult = UserFetchResult.ExceedQuota
            coEvery { githubApiService.getUser(givenId) } returns mockExceedQuota

            // When
            val actualResult = repository.fetchUser(givenId)

            // Then
            assertEquals(expectedResult, actualResult)
            coVerify(exactly = 1) { githubApiService.getUser(givenId) }
        }

    @Test
    fun `Api 리밋 초과 코드 429`() =
        runTest {
            // Given
            val givenId = "test_user"
            val expectedResult = UserFetchResult.ExceedQuota
            coEvery { githubApiService.getUser(givenId) } returns mockExceedLimit

            // When
            val actualResult = repository.fetchUser(givenId)

            // Then
            assertEquals(expectedResult, actualResult)
            coVerify(exactly = 1) { githubApiService.getUser(givenId) }
        }

    @Test
    fun `Api 검색 결과 그 외의 실패`() =
        runTest {
            // Given
            val givenId = "test_user"
            val expectedResult = UserFetchResult.UnknownError
            coEvery { githubApiService.getUser(givenId) } returns mockUnknownError

            // When
            val actualResult = repository.fetchUser(givenId)

            // Then
            assertEquals(expectedResult, actualResult)
            coVerify(exactly = 1) { githubApiService.getUser(givenId) }
        }
}
