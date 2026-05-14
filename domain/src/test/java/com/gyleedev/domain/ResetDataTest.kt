package com.gyleedev.domain

import com.gyleedev.githubsearch.domain.model.ResetDataResult
import com.gyleedev.githubsearch.domain.repository.GitHubRepository
import com.gyleedev.githubsearch.domain.usecase.ResetDataUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
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
    fun `데이터 초기화 성공 시 성공 결과를 반환한다`() = runTest {
        // Given
        val expected = ResetDataResult.Success
        coEvery { repository.resetUser() } just runs

        // When
        val result = useCase()

        // Then
        assertEquals(expected, result)
        coVerify(exactly = 1) { repository.resetUser() }
    }

    @Test
    fun `데이터 초기화 중 예외가 발생하면 실패 결과를 반환한다`() = runTest {
        // Given
        val expected = ResetDataResult.Fail
        coEvery { repository.resetUser() } throws Exception("Database Error")

        // When
        val result = useCase()

        // Then
        assertEquals(expected, result)
        coVerify(exactly = 1) { repository.resetUser() }
    }
}
