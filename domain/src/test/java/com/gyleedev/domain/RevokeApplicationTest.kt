package com.gyleedev.domain

import com.gyleedev.githubsearch.domain.model.RevokeResult
import com.gyleedev.githubsearch.domain.repository.GitHubRepository
import com.gyleedev.githubsearch.domain.usecase.RevokeApplicationUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
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
    fun `앱 권한 철회 성공 시 토큰을 삭제하고 성공 결과를 반환한다`() = runTest {
        // Given
        val expected = RevokeResult.SUCCESS
        coEvery { repository.revokeApplication() } returns expected
        coEvery { repository.deleteAccessToken() } just runs

        // When
        val actual = useCase()

        // Then
        assertEquals(expected, actual)
        coVerify(exactly = 1) { repository.deleteAccessToken() }
    }

    @Test
    fun `앱 권한 철회 실패 시 실패 결과를 반환한다`() = runTest {
        // Given
        val expected = RevokeResult.FAIL
        coEvery { repository.revokeApplication() } returns expected

        // When
        val actual = useCase()

        // Then
        assertEquals(expected, actual)
        coVerify(exactly = 0) { repository.deleteAccessToken() }
    }

    @Test
    fun `액세스 토큰이 없어 철회할 수 없을 때 토큰 없음 결과를 반환한다`() = runTest {
        // Given
        val expected = RevokeResult.NO_KEY
        coEvery { repository.revokeApplication() } returns expected

        // When
        val actual = useCase()

        // Then
        assertEquals(expected, actual)
    }

    @Test
    fun `앱 권한 철회 중 예외가 발생하면 실패 결과를 반환한다`() = runTest {
        // Given
        val expected = RevokeResult.FAIL
        coEvery { repository.revokeApplication() } throws Exception("Network Error")

        // When
        val actual = useCase()

        // Then
        assertEquals(expected, actual)
    }

    @Test
    fun `토큰 삭제 중 예외가 발생하면 실패 결과를 반환한다`() = runTest {
        // Given
        val expected = RevokeResult.FAIL
        coEvery { repository.revokeApplication() } returns RevokeResult.SUCCESS
        coEvery { repository.deleteAccessToken() } throws Exception("Preference Error")

        // When
        val actual = useCase()

        // Then
        assertEquals(expected, actual)
    }
}
