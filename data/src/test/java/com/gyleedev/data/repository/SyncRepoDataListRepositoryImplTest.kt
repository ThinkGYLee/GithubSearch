package com.gyleedev.data.repository

import com.gyleedev.data.database.dao.AccessTimeDao
import com.gyleedev.data.database.dao.ReposDao
import com.gyleedev.data.database.dao.UserDao
import com.gyleedev.data.database.entity.toEntity
import com.gyleedev.data.preference.TokenPreference
import com.gyleedev.data.remote.AccessService
import com.gyleedev.data.remote.GithubApiService
import com.gyleedev.data.remote.RevokeService
import com.gyleedev.data.remote.response.RepoResponse
import com.gyleedev.data.remote.response.toModel
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.coVerifySequence
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import retrofit2.Response
import java.time.Clock

class SyncRepoDataListRepositoryImplTest {
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
    fun `레포지토리 데이터 동기화 시 API에서 데이터가 반환되면 DB에 저장한다`() = runTest {
        // Given
        val entityId = 1L
        val githubId = "test_user"
        val mockRepoResponses = listOf(
            RepoResponse(name = "repo1", description = "desc1", language = "Kotlin", stargazer = 10),
            RepoResponse(name = "repo2", description = "desc2", language = "Java", stargazer = 20),
        )
        // RepoResponse -> RepositoryModel -> ReposEntity 변환 과정 검증
        val expectedEntities = mockRepoResponses.map { it.toModel(githubId).toEntity(entityId) }
        val expectedResponse = Response.success(mockRepoResponses)

        coEvery { githubApiService.getRepos(githubId) } returns expectedResponse
        coEvery { reposDao.insertRepos(expectedEntities) } just runs

        // When
        repository.syncRepoDataList(entityId, githubId)

        // Then
        coVerifySequence {
            githubApiService.getRepos(githubId)
            reposDao.insertRepos(expectedEntities)
        }
    }

    @Test
    fun `레포지토리 데이터 동기화 시 API 결과가 비어있으면 DB 저장을 수행하지 않는다`() = runTest {
        // Given
        val entityId = 1L
        val githubId = "test_user"
        val mockRepoResponses = emptyList<RepoResponse>()
        val expectedResponse = Response.success(mockRepoResponses)

        coEvery { githubApiService.getRepos(githubId) } returns expectedResponse

        // When
        repository.syncRepoDataList(entityId, githubId)

        // Then
        coVerify(exactly = 1) { githubApiService.getRepos(githubId) }
        coVerify(exactly = 0) { reposDao.insertRepos(any()) }
    }
}
