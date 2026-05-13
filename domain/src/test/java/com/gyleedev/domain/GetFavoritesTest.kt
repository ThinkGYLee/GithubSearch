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
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class GetFavoritesTest {
    private val repository: GitHubRepository = mockk()
    private lateinit var useCase: GetFavoritesUseCase
    private lateinit var mockUsers: MutableList<UserModel>

    @Before
    fun setUp() {
        useCase = GetFavoritesUseCase(repository)
        mockUsers = mutableListOf()
        var count = 100L
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
    fun `User가 존재하고 FilterState 가 ALL일때`() = runTest {
        val filter = FilterStatus.ALL
        val expectedValue = PagingData.from(mockUsers)

        coEvery { repository.getFavorites(filter) } returns flowOf(expectedValue)

        val resultFlow = useCase(filter)
        val resultData = resultFlow.asSnapshot()
        assertEquals(mockUsers, resultData)

        coVerify(exactly = 1) { repository.getFavorites(filter).ignoreUnused() }
    }

    @Test
    fun `User가 존재하고 FilterState 가 REPO 일때`() = runTest {
        val filter = FilterStatus.REPO
        val expectedData = mockUsers.filter { it.repoCount > 0 }
        val expectedPaging = PagingData.from(expectedData)

        coEvery { repository.getFavorites(filter) } returns flowOf(expectedPaging)

        val resultFlow = useCase(filter)
        val resultData = resultFlow.asSnapshot()
        assertEquals(expectedData, resultData)

        coVerify(exactly = 1) { repository.getFavorites(filter).ignoreUnused() }
    }

    @Test
    fun `User가 존재하고 FilterState 가 NOREPO 일때`() = runTest {
        val filter = FilterStatus.NOREPO
        val expectedData = mockUsers.filter { it.repoCount == 0 }
        val expectedPaging = PagingData.from(expectedData)

        coEvery { repository.getFavorites(filter) } returns flowOf(expectedPaging)

        val resultFlow = useCase(filter)
        val resultData = resultFlow.asSnapshot()
        assertEquals(expectedData, resultData)

        coVerify(exactly = 1) { repository.getFavorites(filter).ignoreUnused() }
    }

    @Test
    fun `User가 존재하지 않고 Filter가 All 일때`() = runTest {
        val filter = FilterStatus.ALL
        val expectedValue = PagingData.from(emptyList<UserModel>())

        coEvery { repository.getFavorites(filter) } returns flowOf(expectedValue)

        val resultFlow = useCase(filter)
        val resultData = resultFlow.asSnapshot()
        assertEquals(emptyList<UserModel>(), resultData)

        coVerify(exactly = 1) { repository.getFavorites(filter).ignoreUnused() }
    }

    @Test
    fun `User가 존재하지 않고 Filter가 Repo 일때`() = runTest {
        val filter = FilterStatus.REPO
        val expectedValue = PagingData.from(emptyList<UserModel>())

        coEvery { repository.getFavorites(filter) } returns flowOf(expectedValue)

        val resultFlow = useCase(filter)
        val resultData = resultFlow.asSnapshot()
        assertEquals(emptyList<UserModel>(), resultData)

        coVerify(exactly = 1) { repository.getFavorites(filter).ignoreUnused() }
    }

    @Test
    fun `User가 존재하지 않고 Filter가 NoRepo 일때`() = runTest {
        val filter = FilterStatus.NOREPO
        val expectedValue = PagingData.from(emptyList<UserModel>())

        coEvery { repository.getFavorites(filter) } returns flowOf(expectedValue)

        val resultFlow = useCase(filter)
        val resultData = resultFlow.asSnapshot()
        assertEquals(emptyList<UserModel>(), resultData)

        coVerify(exactly = 1) { repository.getFavorites(filter).ignoreUnused() }
    }
}
