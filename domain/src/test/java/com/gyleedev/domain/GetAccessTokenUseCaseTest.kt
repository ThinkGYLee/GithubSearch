package com.gyleedev.domain

import com.gyleedev.githubsearch.domain.model.GetAccessTokenRepositoryResult
import com.gyleedev.githubsearch.domain.model.GetAccessTokenUseCaseResult
import com.gyleedev.githubsearch.domain.repository.GitHubRepository
import com.gyleedev.githubsearch.domain.usecase.GetAccessTokenUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import kotlinx.coroutines.test.runTest

class GetAccessTokenUseCaseTest {
    private val repository: GitHubRepository = mockk()
    private lateinit var useCase: GetAccessTokenUseCase

    @Before
    fun setUp() {
        useCase = GetAccessTokenUseCase(repository)
    }

    @Test
    fun `액세스 토큰 발급 및 저장 성공 시 성공 결과를 반환한다`() =
        runTest {
            // Given
            val code = "code"
            val expectedToken = "token"
            val expectedRepoResult = GetAccessTokenRepositoryResult.Success(token = expectedToken)
            val expectedUseCaseResult = GetAccessTokenUseCaseResult.Success

            coEvery { repository.getAccessToken(code) } returns expectedRepoResult
            coEvery { repository.saveAccessToken(expectedToken) } just runs

            // When
            val actualResult = useCase(code)

            // Then
            assertEquals(expectedUseCaseResult, actualResult)
            coVerify(exactly = 1) { repository.getAccessToken(code) }
            coVerify(exactly = 1) { repository.saveAccessToken(expectedToken) }
        }

    @Test
    fun `액세스 토큰 발급 실패 시 실패 결과를 반환한다`() =
        runTest {
            // Given
            val code = "code"
            val expectedRepoResult = GetAccessTokenRepositoryResult.Fail
            val expectedUseCaseResult = GetAccessTokenUseCaseResult.Fail

            coEvery { repository.getAccessToken(code) } returns expectedRepoResult
            coEvery { repository.saveAccessToken(any()) } just runs

            // When
            val actualResult = useCase(code)

            // Then
            assertEquals(expectedUseCaseResult, actualResult)
            coVerify(exactly = 1) { repository.getAccessToken(code) }
            coVerify(exactly = 0) { repository.saveAccessToken(any()) }
        }

    @Test
    fun `액세스 토큰 발급 중 예외가 발생하면 실패 결과를 반환한다`() =
        runTest {
            // Given
            val code = "code"
            val expectedUseCaseResult = GetAccessTokenUseCaseResult.Fail

            coEvery { repository.getAccessToken(code) } throws Exception("Network Error")
            coEvery { repository.saveAccessToken(any()) } just runs

            // When
            val actualResult = useCase(code)

            // Then
            assertEquals(expectedUseCaseResult, actualResult)
            coVerify(exactly = 1) { repository.getAccessToken(code) }
            coVerify(exactly = 0) { repository.saveAccessToken(any()) }
        }

    @Test
    fun `액세스 토큰 저장 중 예외가 발생하면 실패 결과를 반환한다`() =
        runTest {
            // Given
            val code = "code"
            val expectedToken = "token"
            val expectedRepoResult = GetAccessTokenRepositoryResult.Success(token = expectedToken)
            val expectedUseCaseResult = GetAccessTokenUseCaseResult.Fail

            coEvery { repository.getAccessToken(code) } returns expectedRepoResult
            coEvery { repository.saveAccessToken(expectedToken) } throws Exception("Preference Error")

            // When
            val actualResult = useCase(code)

            // Then
            assertEquals(expectedUseCaseResult, actualResult)
            coVerify(exactly = 1) { repository.getAccessToken(code) }
            coVerify(exactly = 1) { repository.saveAccessToken(expectedToken) }
        }
}
