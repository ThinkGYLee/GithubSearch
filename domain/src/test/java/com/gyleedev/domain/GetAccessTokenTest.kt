package com.gyleedev.domain

import com.gyleedev.githubsearch.domain.model.GetAccessTokenRepositoryResult
import com.gyleedev.githubsearch.domain.model.GetAccessTokenUseCaseResult
import com.gyleedev.githubsearch.domain.repository.GitHubRepository
import com.gyleedev.githubsearch.domain.usecase.GetAccessTokenUseCase
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.coVerifySequence
import io.mockk.just
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class GetAccessTokenTest {
    private val repository: GitHubRepository = mockk()
    private lateinit var useCase: GetAccessTokenUseCase

    @Before
    fun setUp() {
        useCase = GetAccessTokenUseCase(repository)
    }

    @Test
    fun `AccessToken 가져오기 성공`() = runTest {
        // Given
        val token = "token"
        val code = "code"
        val expectedValue = GetAccessTokenRepositoryResult.Success(token = token)
        val expectedResult = GetAccessTokenUseCaseResult.Success

        // Repository 동작 정의
        coEvery { repository.getAccessToken(code) } returns expectedValue
        coEvery { repository.saveAccessToken(token) } just Runs

        // When
        val actualResult = useCase(code)

        // Then

        assertEquals(expectedResult, actualResult)
        coVerifySequence {
            repository.getAccessToken(code)
            repository.saveAccessToken(token)
        }
    }

    @Test
    fun `AccessToken 가져오기 실패`() = runTest {
        // Given
        val code = "code"
        val expectedValue = GetAccessTokenRepositoryResult.Fail
        val expectedResult = GetAccessTokenUseCaseResult.Fail

        // Repository 동작 정의
        coEvery { repository.getAccessToken(code) } returns expectedValue
        coEvery { repository.saveAccessToken(any()) } just Runs

        // When
        val actualResult = useCase(code)

        // Then
        assertEquals(expectedResult, actualResult)
        coVerify(exactly = 1) { repository.getAccessToken(code) }
        coVerify(exactly = 0) { repository.saveAccessToken(any()) }
    }

    @Test
    fun `Exception 나왔을때`() = runTest {
        // Given
        val exception = Exception("Unknown Exception")
        val code = "code"
        val expectedResult = GetAccessTokenUseCaseResult.Fail

        // Repository 동작 정의
        coEvery { repository.getAccessToken(code) } throws exception
        coEvery { repository.saveAccessToken(any()) } just Runs

        // When
        val actualResult = useCase(code)

        // Then
        assertEquals(expectedResult, actualResult)
        coVerify(exactly = 1) { repository.getAccessToken(code) }
        coVerify(exactly = 0) { repository.saveAccessToken(any()) }
    }
}

