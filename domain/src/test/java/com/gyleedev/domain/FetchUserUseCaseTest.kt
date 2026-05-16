package com.gyleedev.domain

import com.gyleedev.githubsearch.domain.model.SearchStatus
import com.gyleedev.githubsearch.domain.model.UserFetchResult
import com.gyleedev.githubsearch.domain.model.UserModel
import com.gyleedev.githubsearch.domain.repository.GitHubRepository
import com.gyleedev.githubsearch.domain.usecase.FetchUserUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class FetchUserUseCaseTest {
    private val repository: GitHubRepository = mockk()
    private lateinit var useCase: FetchUserUseCase

    private val userModel = UserModel(
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

    @Before
    fun setUp() {
        useCase = FetchUserUseCase(repository)
    }

    @Test
    fun `검색 결과가 성공일 때 유저 정보를 저장하고 성공 상태를 반환한다`() = runTest {
        // Given
        val query = "android"
        val expectedFetchResult = UserFetchResult.Success(user = userModel)
        val expectedStatus = SearchStatus.SUCCESS

        coEvery { repository.fetchUser(query) } returns expectedFetchResult
        coEvery { repository.insertUser(userModel) } just runs
        coEvery { repository.upsertAccessTime(id = 0L, githubId = query, isRepoFetched = false) } just runs

        // When
        val actual = useCase(query)

        // Then
        assertEquals(expectedStatus, actual)
        coVerify(exactly = 1) { repository.fetchUser(query) }
        coVerify(exactly = 1) { repository.insertUser(userModel) }
        coVerify(exactly = 1) { repository.upsertAccessTime(id = 0L, githubId = query, isRepoFetched = false) }
    }

    @Test
    fun `검색 결과가 존재하지 않는 유저일 때 유효한 실패 상태를 반환한다`() = runTest {
        // Given
        val query = "android"
        val expectedFetchResult = UserFetchResult.NoSuchUser
        val expectedStatus = SearchStatus.NO_SUCH_USER

        coEvery { repository.fetchUser(query) } returns expectedFetchResult
        coEvery { repository.insertUser(any()) } just runs
        coEvery { repository.upsertAccessTime(any(), any(), any()) } just runs

        // When
        val actual = useCase(query)

        // Then
        assertEquals(expectedStatus, actual)
        coVerify(exactly = 1) { repository.fetchUser(query) }
        coVerify(exactly = 0) { repository.insertUser(any()) }
        coVerify(exactly = 0) { repository.upsertAccessTime(any(), any(), any()) }
    }

    @Test
    fun `할당량이 초과되었을 때 인증 필요 상태를 반환한다`() = runTest {
        // Given
        val query = "android"
        val expectedFetchResult = UserFetchResult.ExceedQuota
        val expectedStatus = SearchStatus.NEED_AUTHENTICATION

        coEvery { repository.fetchUser(query) } returns expectedFetchResult
        coEvery { repository.insertUser(any()) } just runs
        coEvery { repository.upsertAccessTime(any(), any(), any()) } just runs

        // When
        val actual = useCase(query)

        // Then
        assertEquals(expectedStatus, actual)
        coVerify(exactly = 1) { repository.fetchUser(query) }
        coVerify(exactly = 0) { repository.insertUser(any()) }
        coVerify(exactly = 0) { repository.upsertAccessTime(any(), any(), any()) }
    }

    @Test
    fun `알 수 없는 에러가 발생했을 때 실패 상태를 반환한다`() = runTest {
        // Given
        val query = "android"
        val expectedFetchResult = UserFetchResult.UnknownError
        val expectedStatus = SearchStatus.UNKNOWN_FAIL

        coEvery { repository.fetchUser(query) } returns expectedFetchResult
        coEvery { repository.insertUser(any()) } just runs
        coEvery { repository.upsertAccessTime(any(), any(), any()) } just runs

        // When
        val actual = useCase(query)

        // Then
        assertEquals(expectedStatus, actual)
        coVerify(exactly = 1) { repository.fetchUser(query) }
        coVerify(exactly = 0) { repository.insertUser(any()) }
        coVerify(exactly = 0) { repository.upsertAccessTime(any(), any(), any()) }
    }

    @Test
    fun `유저 페치 중 예외가 발생하면 네트워크 에러 상태를 반환한다`() = runTest {
        // Given
        val query = "android"
        val expectedStatus = SearchStatus.BAD_NETWORK

        coEvery { repository.fetchUser(query) } throws Exception("Unknown Exception")
        coEvery { repository.insertUser(any()) } just runs
        coEvery { repository.upsertAccessTime(any(), any(), any()) } just runs

        // When
        val actual = useCase(query)

        // Then
        assertEquals(expectedStatus, actual)
        coVerify(exactly = 1) { repository.fetchUser(query) }
        coVerify(exactly = 0) { repository.insertUser(any()) }
        coVerify(exactly = 0) { repository.upsertAccessTime(any(), any(), any()) }
    }

    @Test
    fun `유저 정보 저장 중 예외가 발생하면 네트워크 에러 상태를 반환한다`() = runTest {
        // Given
        val query = "android"
        val expectedFetchResult = UserFetchResult.Success(user = userModel)
        val expectedStatus = SearchStatus.BAD_NETWORK

        coEvery { repository.fetchUser(query) } returns expectedFetchResult
        coEvery { repository.insertUser(userModel) } throws Exception("DB Exception")
        coEvery { repository.upsertAccessTime(any(), any(), any()) } just runs

        // When
        val actual = useCase(query)

        // Then
        assertEquals(expectedStatus, actual)
        coVerify(exactly = 1) { repository.fetchUser(query) }
        coVerify(exactly = 1) { repository.insertUser(userModel) }
        coVerify(exactly = 0) { repository.upsertAccessTime(any(), any(), any()) }
    }

    @Test
    fun `접근 시간 갱신 중 예외가 발생하면 네트워크 에러 상태를 반환한다`() = runTest {
        // Given
        val query = "android"
        val expectedFetchResult = UserFetchResult.Success(user = userModel)
        val expectedStatus = SearchStatus.BAD_NETWORK

        coEvery { repository.fetchUser(query) } returns expectedFetchResult
        coEvery { repository.insertUser(userModel) } just runs
        coEvery { repository.upsertAccessTime(any(), any(), any()) } throws Exception("DB Exception")

        // When
        val actual = useCase(query)

        // Then
        assertEquals(expectedStatus, actual)
        coVerify(exactly = 1) { repository.fetchUser(query) }
        coVerify(exactly = 1) { repository.insertUser(userModel) }
        coVerify(exactly = 1) { repository.upsertAccessTime(any(), any(), any()) }
    }
}
