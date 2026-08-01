package com.gyleedev.githubsearch.feature.home

import androidx.paging.PagingData
import com.gyleedev.githubsearch.core.testing.CoroutineRule
import com.gyleedev.githubsearch.core.testing.createDummyUser
import com.gyleedev.githubsearch.core.testing.ignoreUnused
import com.gyleedev.githubsearch.domain.model.SearchStatus
import com.gyleedev.githubsearch.domain.usecase.DeleteSelectedUsersUseCase
import com.gyleedev.githubsearch.domain.usecase.FetchUserUseCase
import com.gyleedev.githubsearch.domain.usecase.GetUserWithFlowUseCase
import com.gyleedev.githubsearch.domain.usecase.GetUsersUseCase
import com.gyleedev.githubsearch.domain.usecase.UpdateFavoriteBySetUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import io.mockk.junit4.MockKRule
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {
    @get:Rule
    val mockkRule = MockKRule(this)

    @get:Rule
    val coroutineRule = CoroutineRule()

    private lateinit var viewModel: HomeViewModel

    @MockK
    lateinit var getUsersUseCase: GetUsersUseCase

    @MockK
    lateinit var getUserWithFlowUseCase: GetUserWithFlowUseCase

    @MockK
    lateinit var fetchUserUseCase: FetchUserUseCase

    @MockK
    lateinit var deleteSelectedUsersUseCase: DeleteSelectedUsersUseCase

    @MockK
    lateinit var updateFavoriteBySetUseCase: UpdateFavoriteBySetUseCase

    @Before
    fun setUp() {
        coEvery { getUsersUseCase() } returns flowOf(PagingData.empty())
        coEvery { getUserWithFlowUseCase(any()) } returns flowOf(null)
        coEvery { fetchUserUseCase(any()) } returns SearchStatus.SUCCESS
        viewModel =
            HomeViewModel(
                getUsersUseCase = getUsersUseCase,
                getUserWithFlowUseCase = getUserWithFlowUseCase,
                fetchUserUseCase = fetchUserUseCase,
                deleteSelectedUsersUseCase = deleteSelectedUsersUseCase,
                updateFavoriteBySetUseCase = updateFavoriteBySetUseCase,
            )
    }

    private fun TestScope.collectViewModelFlows() {
        backgroundScope.launch { viewModel.uiState.collect() }
        backgroundScope.launch { viewModel.users.collect() }
    }

    @Test
    fun `초기화 시 유저 리스트를 가져온다`() =
        runTest {
            // Given
            val expectedUiState =
                HomeUiState.Success(
                    searchQuery = "",
                    isLoading = false,
                    searchState = SearchUiState.Empty,
                    selectedUsers = emptySet<String>(),
                    mode = HomeMode.DEFAULT,
                    showRequestAuthDialog = false,
                    showDeleteDialog = false,
                )

            // When
            collectViewModelFlows()
            runCurrent()

            // Then
            coVerify(exactly = 1) { getUsersUseCase().ignoreUnused() }
            coVerify(exactly = 0) { getUserWithFlowUseCase(any()).ignoreUnused() }
            coVerify(exactly = 0) { fetchUserUseCase(any()) }
            assertEquals(expectedUiState, viewModel.uiState.value)
        }

    @Test
    fun `검색어 입력 후 300ms가 지나기 전에는 검색 API가 호출되지 않는다`() =
        runTest {
            // Given
            val query = "test"

            collectViewModelFlows()
            runCurrent()

            // When
            viewModel.updateSearchId(query)
            advanceTimeBy(299L)
            runCurrent()

            // Then
            coVerify(exactly = 1) { getUsersUseCase().ignoreUnused() }
            coVerify(exactly = 0) { getUserWithFlowUseCase(any()).ignoreUnused() }
            coVerify(exactly = 0) { fetchUserUseCase(any()) }
        }

    @Test
    fun `검색어 입력 후 300ms가 지나면 검색 API가 호출되고 결과가 반영된다`() =
        runTest {
            // Given
            val query = "test"
            val expectedUser = createDummyUser(query)
            val expectedSearchState =
                SearchUiState.Success(
                    login = expectedUser.login,
                    avatar = expectedUser.avatar,
                    name = expectedUser.name,
                    bio = expectedUser.bio,
                )
            val expectedUiState =
                HomeUiState.Success(
                    searchQuery = query,
                    isLoading = false,
                    searchState = expectedSearchState,
                    selectedUsers = emptySet<String>(),
                    mode = HomeMode.DEFAULT,
                    showRequestAuthDialog = false,
                    showDeleteDialog = false,
                )

            coEvery { getUserWithFlowUseCase(query) } returns flowOf(expectedUser)

            collectViewModelFlows()
            runCurrent()

            // When
            viewModel.updateSearchId(query)
            runCurrent()
            advanceTimeBy(300L)
            runCurrent()

            // Then
            coVerify(exactly = 1) { getUsersUseCase().ignoreUnused() }
            coVerify(exactly = 1) { getUserWithFlowUseCase(query).ignoreUnused() }
            coVerify(exactly = 0) { fetchUserUseCase(any()) }
            assertEquals(expectedUiState, viewModel.uiState.value)
        }

    @Test
    fun `검색어가 빈 칸이면 API를 호출하지 않고 상태가 Empty가 된다`() =
        runTest {
            // Given
            val query = "  "
            val expectedUiState =
                HomeUiState.Success(
                    searchQuery = query,
                    isLoading = false,
                    searchState = SearchUiState.Empty,
                    selectedUsers = emptySet<String>(),
                    mode = HomeMode.DEFAULT,
                    showRequestAuthDialog = false,
                    showDeleteDialog = false,
                )

            collectViewModelFlows()
            runCurrent()

            // When
            viewModel.updateSearchId(query)
            runCurrent()
            advanceTimeBy(300L)
            runCurrent()

            // Then
            coVerify(exactly = 1) { getUsersUseCase().ignoreUnused() }
            coVerify(exactly = 0) { getUserWithFlowUseCase(any()).ignoreUnused() }
            coVerify(exactly = 0) { fetchUserUseCase(any()) }
            assertEquals(expectedUiState, viewModel.uiState.value)
        }

    @Test
    fun `검색 요청이 성공하면 로딩 상태가 true가 되었다가 완료 후 false가 된다`() =
        runTest {
            // Given
            // 테스트에 사용할 검색어 정의
            val query = "test"

            // UseCase 호출 시 인위적으로 1초의 지연을 발생시켜 로딩 상태를 유지함
            coEvery { fetchUserUseCase(query) } coAnswers {
                delay(1000)
                SearchStatus.SUCCESS
            }

            // When
            // 유저 검색 함수 호출 (로딩 시작 -> UseCase 호출 -> 로딩 종료 흐름 시작)
            viewModel.searchUser(query)
            // 현재 시점(0초)에서 즉시 실행 가능한 코드(isLoading = true)를 실행함
            runCurrent()

            // Then
            // 수집된 상태들 중 isLoading이 true인 상태가 있는지 확인 (로딩 바 노출 여부)
            val secondState = viewModel.uiState.first() as HomeUiState.Success
            // 로딩 상태가 실제로 존재했는지 검증
            assertEquals(true, secondState.isLoading)

            // 가상 시간을 1.001초 흐르게 하여 UseCase의 delay(1초)를 만료시킴
            advanceTimeBy(1001)
            // 지연이 끝난 후의 코드(isLoading = false)가 실행되도록 밀어줌
            runCurrent()
            // 가장 마지막으로 수집된(최종) UI 상태를 가져옴
            val finalState = viewModel.uiState.first() as HomeUiState.Success
            // 최종적으로 로딩 상태가 해제되었는지 검증
            assertEquals(false, finalState.isLoading)
        }

    @Test
    fun `검색 요청 시 인증이 필요하면 다이얼로그 상태가 true가 된다`() =
        runTest {
            // Given
            val query = "test"

            coEvery { fetchUserUseCase(query) } returns SearchStatus.NEED_AUTHENTICATION

            collectViewModelFlows()
            runCurrent()

            // When
            viewModel.searchUser(query)
            runCurrent()

            // Then
            coVerify(exactly = 1) { getUsersUseCase().ignoreUnused() }
            coVerify(exactly = 0) { getUserWithFlowUseCase(any()).ignoreUnused() }
            coVerify(exactly = 1) { fetchUserUseCase(query) }

            val currentUiState = viewModel.uiState.value as HomeUiState.Success
            assertEquals(false, currentUiState.isLoading)
            assertEquals(true, currentUiState.showRequestAuthDialog)
        }
}
