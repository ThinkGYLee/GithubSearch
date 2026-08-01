package com.gyleedev.data.repository

import com.gyleedev.data.database.dao.AccessTimeDao
import com.gyleedev.data.database.dao.ReposDao
import com.gyleedev.data.database.dao.UserDao
import com.gyleedev.data.database.entity.toModel
import com.gyleedev.data.preference.TokenPreference
import com.gyleedev.data.remote.AccessService
import com.gyleedev.data.remote.GithubApiService
import com.gyleedev.data.remote.RevokeService
import com.gyleedev.data.utils.createDummyUserEntity
import com.gyleedev.githubsearch.core.testing.ignoreUnused
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import java.time.Clock
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest

class GetUserWithFlowRepositoryImplTest {
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
    fun `유저 조회 시 해당 ID의 데이터가 존재하면 올바르게 매핑된 모델을 Flow로 반환한다`() =
        runTest {
            // Given
            val expectedId = "testId"
            val expectedEntity = createDummyUserEntity(id = 1, repoCount = 5).copy(githubId = expectedId)
            val expectedModel = expectedEntity.toModel()

            every { userDao.getUserByGithubId(expectedId) } returns flowOf(expectedEntity)

            // When
            val resultFlow = repository.getUserWithFlow(expectedId)
            val actualResult = resultFlow.first()

            // Then
            assertEquals(expectedModel, actualResult)
            coVerify(exactly = 1) { userDao.getUserByGithubId(expectedId).ignoreUnused() }
        }

    @Test
    fun `유저 조회 시 해당 ID의 데이터가 존재하지 않으면 null을 Flow로 반환한다`() =
        runTest {
            // Given
            val expectedId = "testId"

            every { userDao.getUserByGithubId(expectedId) } returns flowOf(null)

            // When
            val resultFlow = repository.getUserWithFlow(expectedId)
            val actualResult = resultFlow.first()

            // Then
            assertNull(actualResult)
            coVerify(exactly = 1) { userDao.getUserByGithubId(expectedId).ignoreUnused() }
        }
}
