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

class UpdateFavoriteStatusTest {
    private val repository: GitHubRepository = mockk()
    private lateinit var useCase: UpdateFavoriteStatusUseCase

    @Before
    fun setUp() {
        useCase = UpdateFavoriteStatusUseCase(repository)
    }

    @Test
    fun `업데이트가 성공하여 return된 id와 기존 id가 일치할때`() = runTest {
        // Given
        val user = createDummyUser(id = 1L, favorite = false)
        val expected = UpdateFavoriteResult.Success
        coEvery { repository.upsertUser(user) } returns 1L

        // When
        val result = useCase(user)

        // Then
        assertEquals(expected, result)
        coVerify(exactly = 1) { repository.upsertUser(user) }
    }

    @Test
    fun `업데이트 대상이 없어서 return된 id와 기존 id가 다를때(새로 insert 됐을 때)`() = runTest {
        // Given
        val user = createDummyUser(id = 1L, favorite = false)
        val expected = UpdateFavoriteResult.Fail
        coEvery { repository.upsertUser(user) } returns 2L

        // When
        val result = useCase(user)

        // Then
        assertEquals(expected, result)
        coVerify(exactly = 1) { repository.upsertUser(user) }
    }

    @Test
    fun `에러가 나서 job이 cancel 됐을 때`() = runTest {
        // Given
        val user = createDummyUser(id = 1L, favorite = false)
        val expected = UpdateFavoriteResult.Fail
        coEvery { repository.upsertUser(user) } throws Exception("Database Error")

        // When
        val result = useCase(user)

        // Then
        assertEquals(expected, result)
        coVerify(exactly = 1) { repository.upsertUser(user) }
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
