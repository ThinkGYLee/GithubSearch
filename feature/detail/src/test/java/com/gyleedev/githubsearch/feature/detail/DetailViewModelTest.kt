package com.gyleedev.githubsearch.feature.detail

import androidx.lifecycle.SavedStateHandle
import com.gyleedev.githubsearch.core.testing.CoroutineRule
import com.gyleedev.githubsearch.core.testing.createDummyUser
import com.gyleedev.githubsearch.core.testing.ignoreUnused
import com.gyleedev.githubsearch.domain.model.RepositoryModel
import com.gyleedev.githubsearch.domain.model.UpdateFavoriteResult
import com.gyleedev.githubsearch.domain.model.UserUpdateResult
import com.gyleedev.githubsearch.domain.usecase.GetReposWithFlowUseCase
import com.gyleedev.githubsearch.domain.usecase.GetUserWithFlowUseCase
import com.gyleedev.githubsearch.domain.usecase.UpdateFavoriteStatusUseCase
import com.gyleedev.githubsearch.domain.usecase.UpdateUserFromGithubUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import io.mockk.junit4.MockKRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class DetailViewModelTest {

    @get:Rule
    val mockkRule = MockKRule(this)

    @get:Rule
    val coroutineRule = CoroutineRule()

    private lateinit var viewModel: DetailViewModel

    @MockK
    lateinit var updateUserFromGithubUseCase: UpdateUserFromGithubUseCase

    @MockK
    lateinit var getUserWithFlowUseCase: GetUserWithFlowUseCase

    @MockK
    lateinit var getReposWithFlowUseCase: GetReposWithFlowUseCase

    @MockK
    lateinit var updateFavoriteStatusUseCase: UpdateFavoriteStatusUseCase

    @Before
    fun setUp() {
        coEvery { updateUserFromGithubUseCase(any()) } returns UserUpdateResult.Success
        coEvery { getUserWithFlowUseCase(any()) } returns flowOf(null)
        coEvery { getReposWithFlowUseCase(any()) } returns flowOf(emptyList())
        coEvery { updateFavoriteStatusUseCase(any()) } returns UpdateFavoriteResult.Success
    }

    // id를 받아오는가 못받아오는가에 따라서도 수행해야할 동작이 있어서 따로 뺌
    private fun createViewModel(id: String? = null) {
        val savedStateHandle = SavedStateHandle().apply {
            id?.let { set("id", it) }
        }
        viewModel = DetailViewModel(
            updateUserFromGithubUseCase = updateUserFromGithubUseCase,
            getUserWithFlowUseCase = getUserWithFlowUseCase,
            getReposWithFlowUseCase = getReposWithFlowUseCase,
            updateFavoriteStatusUseCase = updateFavoriteStatusUseCase,
            savedStateHandle = savedStateHandle,
        )
    }

    private fun TestScope.collectViewModelFlows() {
        backgroundScope.launch { viewModel.user.collect() }
        backgroundScope.launch { viewModel.repo.collect() }
    }

    private fun createDummyRepo(userGithubId: String) = RepositoryModel(
        name = "Repo Name",
        userGithubId = userGithubId,
        description = "Description",
        language = "Kotlin",
        stargazer = 100,
    )

    @Test
    fun `초기화 시 id가 전달되면 GitHub로부터 데이터 업데이트를 시도하고 UI 상태를 갱신한다`() = runTest {
        // Given
        val expectedId = "test_user"
        val expectedUser = createDummyUser(expectedId)
        val expectedRepos = listOf(createDummyRepo(expectedId))

        coEvery { getUserWithFlowUseCase(expectedId) } returns flowOf(expectedUser)
        coEvery { getReposWithFlowUseCase(expectedId) } returns flowOf(expectedRepos)

        // When
        createViewModel(id = expectedId)
        collectViewModelFlows()
        runCurrent()

        // Then
        coVerify(exactly = 1) { updateUserFromGithubUseCase(expectedId) }
        coVerify(exactly = 1) { getUserWithFlowUseCase(expectedId).ignoreUnused() }
        coVerify(exactly = 1) { getReposWithFlowUseCase(expectedId).ignoreUnused() }
        coVerify(exactly = 0) { updateFavoriteStatusUseCase(any()) }
        assertEquals(expectedUser, viewModel.user.value)
        assertEquals(expectedRepos, viewModel.repo.value)
    }

    @Test
    fun `초기화 시 id가 없으면 데이터 업데이트를 수행하지 않는다`() = runTest {
        // Given
        val expectedId = null

        // When
        createViewModel(id = expectedId)
        collectViewModelFlows()
        runCurrent()

        // Then
        coVerify(exactly = 0) { updateUserFromGithubUseCase(any()) }
        coVerify(exactly = 0) { getUserWithFlowUseCase(any()).ignoreUnused() }
        coVerify(exactly = 0) { getReposWithFlowUseCase(any()).ignoreUnused() }
        coVerify(exactly = 0) { updateFavoriteStatusUseCase(any()) }
        assertEquals(null, viewModel.user.value)
        assertEquals(emptyList<RepositoryModel>(), viewModel.repo.value)
    }

    @Test
    fun `초기화 시 id가 빈 값이면 데이터 업데이트를 수행하지 않는다`() = runTest {
        // Given
        val expectedId = ""

        // When
        createViewModel(id = expectedId)
        collectViewModelFlows()
        runCurrent()

        // Then
        coVerify(exactly = 0) { updateUserFromGithubUseCase(any()) }
        coVerify(exactly = 0) { getUserWithFlowUseCase(any()).ignoreUnused() }
        coVerify(exactly = 0) { getReposWithFlowUseCase(any()).ignoreUnused() }
        coVerify(exactly = 0) { updateFavoriteStatusUseCase(any()) }
        assertEquals(null, viewModel.user.value)
        assertEquals(emptyList<RepositoryModel>(), viewModel.repo.value)
    }

    @Test
    fun `즐겨찾기 상태를 업데이트하면 현재 user 값을 사용하여 UseCase를 호출한다`() = runTest {
        // Given
        val expectedId = "test_user"
        val expectedUser = createDummyUser(expectedId)

        coEvery { getUserWithFlowUseCase(expectedId) } returns flowOf(expectedUser)

        createViewModel(id = expectedId)
        collectViewModelFlows()
        runCurrent()

        // When
        viewModel.updateFavoriteStatus()
        runCurrent()

        // Then
        coVerify(exactly = 1) { updateUserFromGithubUseCase(expectedId) }
        coVerify(exactly = 1) { getUserWithFlowUseCase(expectedId).ignoreUnused() }
        coVerify(exactly = 1) { getReposWithFlowUseCase(expectedId).ignoreUnused() }
        coVerify(exactly = 1) { updateFavoriteStatusUseCase(expectedUser) }
    }

    @Test
    fun `현재 user 값이 null일 때 즐겨찾기 상태를 업데이트하면 UseCase를 호출하지 않는다`() = runTest {
        // Given
        val expectedId = "test_user"

        coEvery { getUserWithFlowUseCase(expectedId) } returns flowOf(null)

        createViewModel(id = expectedId)
        collectViewModelFlows()
        runCurrent()

        // When
        viewModel.updateFavoriteStatus()
        runCurrent()

        // Then
        coVerify(exactly = 1) { updateUserFromGithubUseCase(expectedId) }
        coVerify(exactly = 1) { getUserWithFlowUseCase(expectedId).ignoreUnused() }
        coVerify(exactly = 1) { getReposWithFlowUseCase(expectedId).ignoreUnused() }
        coVerify(exactly = 0) { updateFavoriteStatusUseCase(any()) }
    }
}
