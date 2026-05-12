package com.gyleedev.domain

import com.gyleedev.githubsearch.domain.repository.GitHubRepository
import com.gyleedev.githubsearch.domain.usecase.CheckLoginStatusUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class CheckLoginStatusTest {
    private val repository: GitHubRepository = mockk()
    private lateinit var useCase: CheckLoginStatusUseCase

    @Before
    fun setUp() {
        useCase = CheckLoginStatusUseCase(repository)
    }

    @Test
    fun `login 되어 있을 때`() = runTest {
        // Given
        val expectedValue = true
        val expectedFlow = flowOf(expectedValue)

        // Repository 동작 정의
        coEvery { repository.hasAccessToken() } returns expectedFlow

        // When
        val actualFlow = useCase()

        // Then
        val actualValue = actualFlow.first()

        assertEquals(expectedValue, actualValue)
        coVerify(exactly = 1) { repository.hasAccessToken().ignoreUnused() }
    }

    @Test
    fun `login 되어있지 않을 때`() = runTest {
        // Given
        val expectedValue = false
        val expectedFlow = flowOf(expectedValue)

        // Repository 동작 정의
        coEvery { repository.hasAccessToken() } returns expectedFlow

        // When
        val actualFlow = useCase()

        // Then
        val actualValue = actualFlow.first()

        assertEquals(expectedValue, actualValue)
        coVerify(exactly = 1) { repository.hasAccessToken().ignoreUnused() }
    }
}
