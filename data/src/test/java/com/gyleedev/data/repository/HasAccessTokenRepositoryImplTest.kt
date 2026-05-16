package com.gyleedev.data.repository

import com.gyleedev.data.database.dao.AccessTimeDao
import com.gyleedev.data.database.dao.ReposDao
import com.gyleedev.data.database.dao.UserDao
import com.gyleedev.data.preference.TokenPreference
import com.gyleedev.data.remote.AccessService
import com.gyleedev.data.remote.GithubApiService
import com.gyleedev.data.remote.RevokeService
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.time.Clock

class HasAccessTokenRepositoryImplTest {
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
    fun `액세스 토큰 확인 시 토큰이 존재하면 true를 반환한다`() = runTest {
        // Given
        val expectedResult = true
        every { tokenPreference.isKeyExist() } returns expectedResult

        // When
        val resultFlow = repository.hasAccessToken()
        val actualResult = resultFlow.first()

        // Then
        assertEquals(expectedResult, actualResult)
        coVerify(exactly = 1) { tokenPreference.isKeyExist() }
    }

    @Test
    fun `액세스 토큰 확인 시 토큰이 존재하지 않으면 false를 반환한다`() = runTest {
        // Given
        val expectedResult = false
        every { tokenPreference.isKeyExist() } returns expectedResult

        // When
        val resultFlow = repository.hasAccessToken()
        val actualResult = resultFlow.first()

        // Then
        assertEquals(expectedResult, actualResult)
        coVerify(exactly = 1) { tokenPreference.isKeyExist() }
    }
}
