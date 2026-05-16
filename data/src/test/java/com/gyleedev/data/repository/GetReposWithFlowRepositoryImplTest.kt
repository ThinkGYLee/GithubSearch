package com.gyleedev.data.repository

import com.gyleedev.data.database.dao.AccessTimeDao
import com.gyleedev.data.database.dao.ReposDao
import com.gyleedev.data.database.dao.UserDao
import com.gyleedev.data.database.entity.ReposEntity
import com.gyleedev.data.database.entity.toModel
import com.gyleedev.data.preference.TokenPreference
import com.gyleedev.data.remote.AccessService
import com.gyleedev.data.remote.GithubApiService
import com.gyleedev.data.remote.RevokeService
import com.gyleedev.data.utils.ignoreUnused
import com.gyleedev.githubsearch.domain.model.RepositoryModel
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.time.Clock

class GetReposWithFlowRepositoryImplTest {
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
    fun `레포지토리 리스트 조회 시 데이터가 존재하면 올바르게 매핑된 모델 리스트를 Flow로 반환한다`() = runTest {
        // Given
        val requestGithubId = "test_user"
        val expectedEntities = listOf(
            ReposEntity(
                id = 1L,
                userEntityId = 1L,
                userGithubId = requestGithubId,
                name = "repo1",
                description = "desc1",
                language = "Kotlin",
                stargazer = 10,
            ),
            ReposEntity(
                id = 2L,
                userEntityId = 1L,
                userGithubId = requestGithubId,
                name = "repo2",
                description = "desc2",
                language = "Java",
                stargazer = 20,
            ),
        )
        val expectedModels = expectedEntities.map { it.toModel() }

        every { reposDao.getReposByGithubIdWithFlow(requestGithubId) } returns flowOf(expectedEntities)

        // When
        val resultFlow = repository.getReposWithFlow(requestGithubId)
        val actualResult = resultFlow.first()

        // Then
        assertEquals(expectedModels, actualResult)
        coVerify(exactly = 1) { reposDao.getReposByGithubIdWithFlow(requestGithubId).ignoreUnused() }
    }

    @Test
    fun `레포지토리 리스트 조회 시 데이터가 존재하지 않으면 빈 리스트를 Flow로 반환한다`() = runTest {
        // Given
        val requestGithubId = "test_user"
        val expectedEntities = emptyList<ReposEntity>()
        val expectedModels = emptyList<RepositoryModel>()

        every { reposDao.getReposByGithubIdWithFlow(requestGithubId) } returns flowOf(expectedEntities)

        // When
        val resultFlow = repository.getReposWithFlow(requestGithubId)
        val actualResult = resultFlow.first()

        // Then
        assertEquals(expectedModels, actualResult)
        coVerify(exactly = 1) { reposDao.getReposByGithubIdWithFlow(requestGithubId).ignoreUnused() }
    }
}
