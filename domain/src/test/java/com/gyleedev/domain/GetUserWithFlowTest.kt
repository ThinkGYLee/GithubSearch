package com.gyleedev.domain

import com.gyleedev.githubsearch.domain.model.UserModel
import com.gyleedev.githubsearch.domain.repository.GitHubRepository
import com.gyleedev.githubsearch.domain.usecase.GetUserWithFlowUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class GetUserWithFlowTest {
    private val repository: GitHubRepository = mockk()
    private lateinit var useCase: GetUserWithFlowUseCase

    @Before
    fun setUp() {
        useCase = GetUserWithFlowUseCase(repository)
    }

    @Test
    fun `유저 정보가 존재할 때 유저 데이터를 Flow로 반환한다`() = runTest {
        // Given
        val userId = "googleAndroid"
        val expectedResult = UserModel(
            id = 0L,
            name = "Android",
            login = "googleAndroid",
            followers = 300000,
            following = 800000,
            avatar = "url",
            company = "google",
            email = "google@gmail.com",
            bio = "world is changed google",
            repoCount = 80,
            createdDate = "",
            updatedDate = "",
            reposAddress = "repoUrl",
            blogUrl = "blogUrl",
            favorite = false,
        )
        coEvery { repository.getUserWithFlow(userId) } returns flowOf(expectedResult)

        // When
        val actualFlow = useCase(userId)
        val actualResult = actualFlow.first()

        // Then
        assertEquals(expectedResult, actualResult)
        coVerify(exactly = 1) { repository.getUserWithFlow(userId) }
    }

    @Test
    fun `유저 정보가 없을 때 null을 Flow로 반환한다`() = runTest {
        // Given
        val userId = "googleAndroid"
        val expectedResult = null
        coEvery { repository.getUserWithFlow(userId) } returns flowOf(expectedResult)

        // When
        val actualFlow = useCase(userId)
        val actualResult = actualFlow.first()

        // Then
        assertEquals(expectedResult, actualResult)
        coVerify(exactly = 1) { repository.getUserWithFlow(userId) }
    }

    @Test
    fun `유저 정보 조회 중 예외가 발생하면 null을 Flow로 반환한다`() = runTest {
        // Given
        val userId = "googleAndroid"
        val expectedResult = null
        coEvery { repository.getUserWithFlow(userId) } throws Exception("Database Error")

        // When
        val actualFlow = useCase(userId)
        val actualResult = actualFlow.first()

        // Then
        assertEquals(expectedResult, actualResult)
    }
}
