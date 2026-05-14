package com.gyleedev.domain

import androidx.paging.PagingData
import androidx.paging.testing.asSnapshot
import com.gyleedev.githubsearch.domain.model.UserModel
import com.gyleedev.githubsearch.domain.repository.GitHubRepository
import com.gyleedev.githubsearch.domain.usecase.GetUsersUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class GetUsersTest {
    private val repository: GitHubRepository = mockk()
    private lateinit var useCase: GetUsersUseCase
    private lateinit var mockUsers: MutableList<UserModel>

    @Before
    fun setUp() {
        useCase = GetUsersUseCase(repository)
        mockUsers = mutableListOf()
        var count = 10L
        while (count >= 0) {
            mockUsers.add(
                UserModel(
                    id = count,
                    name = "name_$count",
                    login = "login_$count",
                    followers = count.toInt(),
                    following = count.toInt(),
                    avatar = "avatar_$count",
                    company = "company_$count",
                    email = "email_$count",
                    bio = "bio_$count",
                    repoCount = count.toInt(),
                    createdDate = "createdDate_$count",
                    updatedDate = "updatedDate_$count",
                    reposAddress = "reposAddress_$count",
                    blogUrl = "blogUrl_$count",
                    favorite = count.toInt() / 2 == 0,
                ),
            )
            count--
        }
    }

    @Test
    fun `유저 목록이 존재할 때 페이징 데이터를 정상적으로 반환한다`() = runTest {
        // Given
        val expectedData = mockUsers
        val expectedPaging = PagingData.from(expectedData)

        coEvery { repository.getUsers() } returns flowOf(expectedPaging)

        // When
        val resultFlow = useCase()
        val actualResult = resultFlow.asSnapshot()

        // Then
        assertEquals(expectedData, actualResult)
        coVerify(exactly = 1) { repository.getUsers().ignoreUnused() }
    }

    @Test
    fun `유저 목록이 없을 때 빈 페이징 데이터를 반환한다`() = runTest {
        // Given
        val expectedData = emptyList<UserModel>()
        val expectedPaging = PagingData.from(expectedData)

        coEvery { repository.getUsers() } returns flowOf(expectedPaging)

        // When
        val resultFlow = useCase()
        val actualResult = resultFlow.asSnapshot()

        // Then
        assertEquals(expectedData, actualResult)
        coVerify(exactly = 1) { repository.getUsers().ignoreUnused() }
    }
}
