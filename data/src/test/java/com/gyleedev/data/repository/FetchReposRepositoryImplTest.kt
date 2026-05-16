package com.gyleedev.data.repository

import com.gyleedev.data.database.dao.AccessTimeDao
import com.gyleedev.data.database.dao.ReposDao
import com.gyleedev.data.database.dao.UserDao
import com.gyleedev.data.preference.TokenPreference
import com.gyleedev.data.remote.AccessService
import com.gyleedev.data.remote.GithubApiService
import com.gyleedev.data.remote.RevokeService
import com.gyleedev.data.remote.response.RepoResponse
import com.gyleedev.githubsearch.domain.model.RepositoryModel
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Before
import org.junit.Test
import retrofit2.Response
import java.time.Clock

class FetchReposRepositoryImplTest {
    private lateinit var repository: GitHubRepositoryImpl
    private val userDao: UserDao = mockk()
    private val reposDao: ReposDao = mockk()
    private val accessTimeDao: AccessTimeDao = mockk()
    private val githubApiService: GithubApiService = mockk()
    private val accessService: AccessService = mockk()
    private val revokeService: RevokeService = mockk()
    private val tokenPreference: TokenPreference = mockk()
    private val clock: Clock = mockk()
    private val mockModel = listOf(
        RepositoryModel(
            name = "name_1",
            userGithubId = "testUser",
            description = "desc_1",
            language = "Kotlin",
            stargazer = 10,
        ),
        RepositoryModel(
            name = "name_2",
            userGithubId = "testUser",
            description = "desc_2",
            language = "Java",
            stargazer = 20,
        ),
    )
    val mockResponse = listOf(
        RepoResponse(
            name = "name_1",
            description = "desc_1",
            language = "Kotlin",
            stargazer = 10,
        ),
        RepoResponse(
            name = "name_2",
            description = "desc_2",
            language = "Java",
            stargazer = 20,
        ),
    )
    private val mockSuccess = Response.success(mockResponse)
    private val mockFail = Response.error<List<RepoResponse>>(404, "".toResponseBody(null))

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
    fun `Api Response 가 Success 일 때`() = runTest {
        // Given
        val givenId = "testUser"
        val expectedResponse = mockSuccess
        val expectedResult = mockModel
        coEvery { githubApiService.getRepos(givenId) } returns expectedResponse
        // When
        val actualResult = repository.fetchRepos(givenId)
        // Then
        assertEquals(expectedResult, actualResult)
        coVerify(exactly = 1) { githubApiService.getRepos(givenId) }
    }

    @Test
    fun `Api Response 가 Error 일 때`() = runTest {
        // Given
        val givenId = "testUser"
        val expectedResponse = mockFail
        val expectedResult = emptyList<RepositoryModel>()
        coEvery { githubApiService.getRepos(givenId) } returns expectedResponse
        // When
        val actualResult = repository.fetchRepos(givenId)
        // Then
        assertEquals(expectedResult, actualResult)
        coVerify(exactly = 1) { githubApiService.getRepos(givenId) }
    }
}
