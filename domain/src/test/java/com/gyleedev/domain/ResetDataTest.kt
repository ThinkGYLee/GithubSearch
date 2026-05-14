package com.gyleedev.domain

import com.gyleedev.githubsearch.domain.model.ResetDataResult
import com.gyleedev.githubsearch.domain.repository.GitHubRepository
import com.gyleedev.githubsearch.domain.usecase.ResetDataUseCase
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class ResetDataTest {
    private val repository: GitHubRepository = mockk()
    private lateinit var useCase: ResetDataUseCase

    @Before
    fun setUp() {
        useCase = ResetDataUseCase(repository)
    }

    @Test
    fun `모든 작업이 성공했을 때`() = runTest {
        // Given
        val expected = ResetDataResult.Success
        // returns Unit 대신 just runs를 사용하여 실행만 됨을 명시
        coEvery { repository.resetUser() } just runs

        // When
        val result = useCase()

        // Then
        assertEquals(expected, result)

        // 호출 횟수 검증
        coVerify(exactly = 1) { repository.resetUser() }
    }

    @Test
    fun `유저 정보 초기화가 실패하면 Fail을 반환해야 한다`() = runTest {
        val expected = ResetDataResult.Fail
        // Given
        coEvery { repository.resetUser() } throws Exception("User DB Error")

        // When
        val result = useCase()

        // Then
        assertEquals(expected, result)
        coVerify(exactly = 1) { repository.resetUser() }
    }
}
