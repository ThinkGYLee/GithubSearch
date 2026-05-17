package com.gyleedev.data.repository

import com.gyleedev.data.database.dao.AccessTimeDao
import com.gyleedev.data.database.dao.ReposDao
import com.gyleedev.data.database.dao.UserDao
import com.gyleedev.data.preference.TokenPreference
import com.gyleedev.data.remote.AccessService
import com.gyleedev.data.remote.GithubApiService
import com.gyleedev.data.remote.RevokeService
import com.gyleedev.data.utils.createDummyUserEntity
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.time.Clock

class GetUserIdRepositoryImplTest {
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
    fun `유저 ID 조회 시 데이터가 존재하면 해당 유저의 내부 Long ID를 반환한다`() = runTest {
        // Given
        val requestGithubId = "test_github_id"
        val expectedLongId = 12345L
        val expectedEntity = createDummyUserEntity(id = expectedLongId, repoCount = 0).copy(githubId = requestGithubId)

        coEvery { userDao.getUser(requestGithubId) } returns expectedEntity

        // When
        val result = repository.getUserId(requestGithubId)

        // Then
        assertEquals(expectedLongId, result)
        coVerify(exactly = 1) { userDao.getUser(requestGithubId) }
    }

    @Test
    fun `유저 ID 조회 시 데이터가 존재하지 않으면 null을 반환한다`() = runTest {
        // Given
        val requestGithubId = "test_github_id"
        val expectedEntity: com.gyleedev.data.database.entity.UserEntity? = null
        val expectedResult: Long? = null

        coEvery { userDao.getUser(requestGithubId) } returns expectedEntity

        // When
        val result = repository.getUserId(requestGithubId)

        // Then
        assertEquals(expectedResult, result)
        coVerify(exactly = 1) { userDao.getUser(requestGithubId) }
    }
}
