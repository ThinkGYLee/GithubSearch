package com.gyleedev.domain

import com.gyleedev.githubsearch.domain.model.RepositoryModel
import com.gyleedev.githubsearch.domain.repository.GitHubRepository
import com.gyleedev.githubsearch.domain.usecase.GetReposWithFlowUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class GetReposWithFlowTest {
    private val repository: GitHubRepository = mockk()
    private lateinit var useCase: GetReposWithFlowUseCase
    private lateinit var mockRepos: MutableList<RepositoryModel>

    @Before
    fun setUp() {
        useCase = GetReposWithFlowUseCase(repository)
        mockRepos = mutableListOf()
        var count = 100L
        while (count >= 0) {
            mockRepos.add(
                RepositoryModel(
                    name = "name_$count",
                    userGithubId = "userGithubId_${count / 4}",
                    description = "description_$count",
                    language = "language_$count",
                    stargazer = count.toInt(),
                ),
            )
            count--
        }
    }

    @Test
    fun `repo item 이 존재할 때`() = runTest {
        val userId = "userGithubId_0L"
        val expectedResult = mockRepos.filter { it.userGithubId == userId }

        coEvery { repository.getReposWithFlow(userId) } returns flowOf(expectedResult)

        val actual = useCase(userId)
        assertEquals(expectedResult, actual.first())

        coVerify(exactly = 1) { repository.getReposWithFlow(userId).ignoreUnused() }
    }

    @Test
    fun `repo item 이 없을 때`() = runTest {
        val userId = "userGithubId_0L"
        val expectedResult = emptyList<RepositoryModel>()

        coEvery { repository.getReposWithFlow(userId) } returns flowOf(expectedResult)

        val actual = useCase(userId)
        assertEquals(expectedResult, actual.first())

        coVerify(exactly = 1) { repository.getReposWithFlow(userId).ignoreUnused() }
    }

    @Test
    fun `Exception 이 날 때`() = runTest {
        val userId = "userGithubId_0L"
        val exception = Exception("Unknown Exception")
        val expect = emptyList<RepositoryModel>()

        coEvery { repository.getReposWithFlow(userId) } throws exception

        val actual = useCase(userId)
        assertEquals(expect, actual.first())

        coVerify(exactly = 1) { repository.getReposWithFlow(userId).ignoreUnused() }
    }
}
