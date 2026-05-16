package com.gyleedev.data.repository

import com.gyleedev.data.database.dao.AccessTimeDao
import com.gyleedev.data.database.dao.ReposDao
import com.gyleedev.data.database.dao.UserDao
import com.gyleedev.data.database.entity.AccessTimeEntity
import com.gyleedev.data.preference.TokenPreference
import com.gyleedev.data.remote.AccessService
import com.gyleedev.data.remote.GithubApiService
import com.gyleedev.data.remote.RevokeService
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import java.time.Clock
import java.time.Instant

class UpsertAccessTimeRepositoryImplTest {
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
    fun `액세스 시간을 업데이트할 때 주입된 Clock을 사용하여 AccessTimeEntity를 생성하고 Dao를 호출한다`() = runTest {
        // Given
        val expectedId = 1L
        val expectedGithubId = "test_github_id"
        val expectedIsRepoFetched = true
        val expectedInstant = Instant.ofEpochMilli(1680000000000L)

        // Instant.now(clock)은 내부적으로 clock.instant()를 호출하므로 이를 모킹
        every { clock.instant() } returns expectedInstant

        val expectedEntity = AccessTimeEntity(
            id = expectedId,
            githubId = expectedGithubId,
            accessTime = expectedInstant,
            isRepoFetched = expectedIsRepoFetched,
        )

        coEvery { accessTimeDao.upsertAccessTime(expectedEntity) } just runs

        // When
        repository.upsertAccessTime(expectedId, expectedGithubId, expectedIsRepoFetched)

        // Then
        coVerify(exactly = 1) { accessTimeDao.upsertAccessTime(expectedEntity) }
    }
}
