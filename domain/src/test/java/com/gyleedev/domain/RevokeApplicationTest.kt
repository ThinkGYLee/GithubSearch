package com.gyleedev.domain

import com.gyleedev.githubsearch.domain.model.RevokeResult
import com.gyleedev.githubsearch.domain.repository.GitHubRepository
import com.gyleedev.githubsearch.domain.usecase.RevokeApplicationUseCase
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.just
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class RevokeApplicationTest {
    private val repository: GitHubRepository = mockk()
    private lateinit var useCase: RevokeApplicationUseCase

    @Before
    fun setUp() {
        useCase = RevokeApplicationUseCase(repository)
    }

    @Test
    fun `Access Token이 존재하고 Revoke 에 성공했을 때`() = runTest {
        // Given
        val expected = RevokeResult.SUCCESS

        // Repository 동작 정의
        coEvery { repository.revokeApplication() } returns expected
        coEvery { repository.deleteAccessToken() } just Runs

        // When
        val actual = useCase()

        // Then
        assertEquals(expected, actual)
        coVerify(exactly = 1) { repository.deleteAccessToken() }
    }

    @Test
    fun `Access Token이 존재하고 Revoke 에 실패했을 때`() = runTest {
        // Given
        val expected = RevokeResult.FAIL

        // Repository 동작 정의
        coEvery { repository.revokeApplication() } returns expected

        // When
        val actual = useCase()

        // Then
        assertEquals(expected, actual)
        coVerify(exactly = 0) { repository.deleteAccessToken() }
    }

    @Test
    fun `Access Token이 존재하지 않을 때`() = runTest {
        // Given
        val expected = RevokeResult.NO_KEY

        // Repository 동작 정의
        coEvery { repository.revokeApplication() } returns expected

        // When
        val actual = useCase()

        // Then
        assertEquals(expected, actual)
        coVerify(exactly = 0) { repository.deleteAccessToken() }
    }

    @Test
    fun `repository function 을 호출했을 때 Exception이 발생했을 때`() = runTest {
        // Given
        val expected = RevokeResult.FAIL

        // Repository 동작 정의
        coEvery { repository.revokeApplication() } throws Exception("Unknown Exception")

        // When
        val actual = useCase()

        // Then
        assertEquals(expected, actual)
        coVerify(exactly = 0) { repository.deleteAccessToken() }
    }
}
