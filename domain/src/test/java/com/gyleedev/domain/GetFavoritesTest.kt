package com.gyleedev.domain

import androidx.paging.PagingData
import androidx.paging.testing.asSnapshot
import com.gyleedev.githubsearch.domain.model.FilterStatus
import com.gyleedev.githubsearch.domain.model.UserModel
import com.gyleedev.githubsearch.domain.repository.GitHubRepository
import com.gyleedev.githubsearch.domain.usecase.GetFavoritesUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class GetFavoritesTest {
    private val repository: GitHubRepository = mockk()
    private lateinit var useCase: GetFavoritesUseCase
    private val mockUsers = listOf(
        createDummyUser(id = 1, repoCount = 5),
        createDummyUser(id = 2, repoCount = 0)
    )

    @Before
    fun setUp() {
        useCase = GetFavoritesUseCase(repository)
    }

    @Test
    fun `필터가 ALL일 때 모든 즐겨찾기 목록을 반환한다`() = runTest {
        // Given
        val filter = FilterStatus.ALL
        val expectedData = mockUsers
        val expectedPaging = PagingData.from(expectedData)
        coEvery { repository.getFavorites(filter) } returns flowOf(expectedPaging)

        // When
        val resultFlow = useCase(filter)
        val actualResult = resultFlow.asSnapshot()

        // Then
        assertEquals(expectedData, actualResult)
        coVerify(exactly = 1) { repository.getFavorites(filter) }
    }

    @Test
    fun `필터가 REPO일 때 레포지토리가 있는 유저만 반환한다`() = runTest {
        // Given
        val filter = FilterStatus.REPO
        val expectedData = mockUsers.filter { it.repoCount > 0 }
        val expectedPaging = PagingData.from(expectedData)
        coEvery { repository.getFavorites(filter) } returns flowOf(expectedPaging)

        // When
        val resultFlow = useCase(filter)
        val actualResult = resultFlow.asSnapshot()

        // Then
        assertEquals(expectedData, actualResult)
    }

    @Test
    fun `즐겨찾기 목록이 없을 때 빈 목록을 반환한다`() = runTest {
        // Given
        val filter = FilterStatus.ALL
        val expectedResult = emptyList<UserModel>()
        val expectedPaging = PagingData.from(expectedResult)
        coEvery { repository.getFavorites(filter) } returns flowOf(expectedPaging)

        // When
        val resultFlow = useCase(filter)
        val actualResult = resultFlow.asSnapshot()

        // Then
        assertEquals(expectedResult, actualResult)
    }

    private fun createDummyUser(id: Long, repoCount: Int): UserModel = UserModel(
        id = id,
        name = "name_$id",
        login = "login_$id",
        followers = 0,
        following = 0,
        avatar = "",
        company = null,
        email = null,
        bio = null,
        repoCount = repoCount,
        createdDate = null,
        updatedDate = null,
        reposAddress = "",
        blogUrl = null,
        favorite = true,
    )
}
