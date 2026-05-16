package com.gyleedev.data.repository

import com.gyleedev.data.database.dao.AccessTimeDao
import com.gyleedev.data.database.dao.ReposDao
import com.gyleedev.data.database.dao.UserDao
import com.gyleedev.data.database.entity.AccessTimeEntity
import com.gyleedev.data.preference.TokenPreference
import com.gyleedev.data.remote.AccessService
import com.gyleedev.data.remote.GithubApiService
import com.gyleedev.data.remote.RevokeService
import com.gyleedev.githubsearch.domain.model.AccessTime
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.time.Clock
import java.time.Instant

class GetLastAccessByIdRepositoryImplTest {
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
    fun `마지막 액세스 시간 조회 시 데이터가 존재하면 모델로 매핑하여 반환한다`() = runTest {
        // Given
        val requestGithubId = "test_github_id"
        val expectedInstant = Instant.ofEpochMilli(1680000000000L)
        val expectedEntity = AccessTimeEntity(
            id = 1L,
            githubId = requestGithubId,
            accessTime = expectedInstant,
            isRepoFetched = true,
        )
        val expectedResult = AccessTime(
            id = 1L,
            githubId = requestGithubId,
            accessTime = expectedInstant,
            isRepoFetched = true,
        )

        coEvery { accessTimeDao.getTimeByGithubId(requestGithubId) } returns expectedEntity

        // When
        val result = repository.getLastAccessById(requestGithubId)

        // Then
        assertEquals(expectedResult, result)
        coVerify(exactly = 1) { accessTimeDao.getTimeByGithubId(requestGithubId) }
    }

    @Test
    fun `마지막 액세스 시간 조회 시 데이터가 존재하지 않으면 null을 반환한다`() = runTest {
        // Given
        val requestGithubId = "test_github_id"
        val expectedEntity: AccessTimeEntity? = null
        val expectedResult: AccessTime? = null

        coEvery { accessTimeDao.getTimeByGithubId(requestGithubId) } returns expectedEntity

        // When
        val result = repository.getLastAccessById(requestGithubId)

        // Then
        assertEquals(expectedResult, result)
        coVerify(exactly = 1) { accessTimeDao.getTimeByGithubId(requestGithubId) }
    }
}
