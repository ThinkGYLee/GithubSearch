package com.gyleedev.domain

import com.gyleedev.githubsearch.core.testing.ignoreUnused
import com.gyleedev.githubsearch.domain.repository.GitHubRepository
import com.gyleedev.githubsearch.domain.usecase.CheckLoginStatusUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest

class CheckLoginStatusUseCaseTest {
    private val repository: GitHubRepository = mockk()
    private lateinit var useCase: CheckLoginStatusUseCase

    @Before
    fun setUp() {
        useCase = CheckLoginStatusUseCase(repository)
    }

    @Test
    fun `로그인 상태일 때 true를 Flow로 반환한다`() =
        runTest {
            // Given
            val expectedValue = true
            coEvery { repository.hasAccessToken() } returns flowOf(expectedValue)

            // When
            val actualFlow = useCase()
            val actualValue = actualFlow.first()

            // Then
            assertEquals(expectedValue, actualValue)
            coVerify(exactly = 1) { repository.hasAccessToken().ignoreUnused() }
        }

    @Test
    fun `로그아웃 상태일 때 false를 Flow로 반환한다`() =
        runTest {
            // Given
            val expectedValue = false
            coEvery { repository.hasAccessToken() } returns flowOf(expectedValue)

            // When
            val actualFlow = useCase()
            val actualValue = actualFlow.first()

            // Then
            assertEquals(expectedValue, actualValue)
            coVerify(exactly = 1) { repository.hasAccessToken().ignoreUnused() }
        }
}
