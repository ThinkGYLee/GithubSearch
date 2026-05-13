package com.gyleedev.domain

import com.gyleedev.githubsearch.domain.model.UserModel
import com.gyleedev.githubsearch.domain.repository.GitHubRepository
import com.gyleedev.githubsearch.domain.usecase.GetUserWithFlowUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
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
    fun `User가 존재할 때`() = runTest {
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

        val actual = useCase(userId)
        assertEquals(expectedResult, actual.first())

        coVerify(exactly = 1) { repository.getUserWithFlow(userId).ignoreUnused() }
    }

    @Test
    fun `User가 없을 때`() = runTest {
        val userId = "googleAndroid"
        val expectedResult = null

        coEvery { repository.getUserWithFlow(userId) } returns flowOf(expectedResult)

        val actual = useCase(userId)
        assertEquals(expectedResult, actual.first())

        coVerify(exactly = 1) { repository.getUserWithFlow(userId).ignoreUnused() }
    }

    @Test
    fun `Exception 이 날 때`() = runTest {
        val userId = "googleAndroid"
        val exception = Exception("Unknown Exception")
        val expectedResult = null

        coEvery { repository.getUserWithFlow(userId) } throws exception

        val actual = useCase(userId)
        assertEquals(expectedResult, actual.first())

        coVerify(exactly = 1) { repository.getUserWithFlow(userId).ignoreUnused() }
    }
}
