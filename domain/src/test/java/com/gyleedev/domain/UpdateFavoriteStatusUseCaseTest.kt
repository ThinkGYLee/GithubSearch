package com.gyleedev.domain

import com.gyleedev.githubsearch.domain.model.UpdateFavoriteResult
import com.gyleedev.githubsearch.domain.model.UserModel
import com.gyleedev.githubsearch.domain.repository.GitHubRepository
import com.gyleedev.githubsearch.domain.usecase.UpdateFavoriteStatusUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class UpdateFavoriteStatusUseCaseTest {
    private val repository: GitHubRepository = mockk()
    private lateinit var useCase: UpdateFavoriteStatusUseCase

    @Before
    fun setUp() {
        useCase = UpdateFavoriteStatusUseCase(repository)
    }

    @Test
    fun `즐겨찾기 상태 변경 성공 시 성공 결과를 반환한다`() = runTest {
        // Given
        val user = createDummyUser(id = 1L, favorite = false)
        val expectedResult = UpdateFavoriteResult.Success
        val expectedUpsertId = 1L

        coEvery { repository.upsertUser(any()) } returns expectedUpsertId

        // When
        val actualResult = useCase(user)

        // Then
        assertEquals(expectedResult, actualResult)
        coVerify(exactly = 1) { repository.upsertUser(any()) }
    }

    @Test
    fun `업데이트된 ID가 기존과 다를 경우 실패 결과를 반환한다`() = runTest {
        // Given
        val user = createDummyUser(id = 1L, favorite = false)
        val expectedResult = UpdateFavoriteResult.Fail
        val expectedUpsertId = 2L

        coEvery { repository.upsertUser(any()) } returns expectedUpsertId

        // When
        val actualResult = useCase(user)

        // Then
        assertEquals(expectedResult, actualResult)
        coVerify(exactly = 1) { repository.upsertUser(any()) }
    }

    @Test
    fun `즐겨찾기 상태 변경 중 예외가 발생하면 실패 결과를 반환한다`() = runTest {
        // Given
        val user = createDummyUser(id = 1L, favorite = false)
        val expectedResult = UpdateFavoriteResult.Fail

        coEvery { repository.upsertUser(any()) } throws Exception("Database Error")

        // When
        val actualResult = useCase(user)

        // Then
        assertEquals(expectedResult, actualResult)
        coVerify(exactly = 1) { repository.upsertUser(any()) }
    }

    private fun createDummyUser(id: Long, favorite: Boolean): UserModel = UserModel(
        id = id,
        name = "test",
        login = "test",
        followers = 0,
        following = 0,
        avatar = "",
        company = null,
        email = null,
        bio = null,
        repoCount = 0,
        createdDate = null,
        updatedDate = null,
        reposAddress = "",
        blogUrl = null,
        favorite = favorite,
    )
}
