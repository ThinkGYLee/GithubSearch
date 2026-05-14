package com.gyleedev.domain

import com.gyleedev.githubsearch.domain.model.RepositoryModel
import com.gyleedev.githubsearch.domain.repository.GitHubRepository
import com.gyleedev.githubsearch.domain.usecase.GetReposWithFlowUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class GetReposWithFlowTest {
    private val repository: GitHubRepository = mockk()
    private lateinit var useCase: GetReposWithFlowUseCase
    private val mockRepos = listOf(
        RepositoryModel(
            name = "name_1",
            userGithubId = "testUser",
            description = "desc_1",
            language = "Kotlin",
            stargazer = 10,
        ),
        RepositoryModel(
            name = "name_2",
            userGithubId = "testUser",
            description = "desc_2",
            language = "Java",
            stargazer = 20,
        )
    )

    @Before
    fun setUp() {
        useCase = GetReposWithFlowUseCase(repository)
    }

    @Test
    fun `레포지토리 목록이 존재할 때 리스트를 Flow로 반환한다`() = runTest {
        // Given
        val userId = "testUser"
        val expectedResult = mockRepos
        coEvery { repository.getReposWithFlow(userId) } returns flowOf(expectedResult)

        // When
        val actualFlow = useCase(userId)
        val actualResult = actualFlow.first()

        // Then
        assertEquals(expectedResult, actualResult)
        coVerify(exactly = 1) { repository.getReposWithFlow(userId) }
    }

    @Test
    fun `레포지토리 목록이 없을 때 빈 리스트를 Flow로 반환한다`() = runTest {
        // Given
        val userId = "testUser"
        val expectedResult = emptyList<RepositoryModel>()
        coEvery { repository.getReposWithFlow(userId) } returns flowOf(expectedResult)

        // When
        val actualFlow = useCase(userId)
        val actualResult = actualFlow.first()

        // Then
        assertEquals(expectedResult, actualResult)
        coVerify(exactly = 1) { repository.getReposWithFlow(userId) }
    }

    @Test
    fun `레포지토리 조회 중 예외가 발생하면 빈 리스트를 Flow로 반환한다`() = runTest {
        // Given
        val userId = "testUser"
        val expectedResult = emptyList<RepositoryModel>()
        coEvery { repository.getReposWithFlow(userId) } throws Exception("Database Error")

        // When
        val actualFlow = useCase(userId)
        val actualResult = actualFlow.first()

        // Then
        assertEquals(expectedResult, actualResult)
    }
}
