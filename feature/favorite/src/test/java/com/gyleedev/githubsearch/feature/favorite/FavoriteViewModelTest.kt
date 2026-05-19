package com.gyleedev.githubsearch.feature.favorite

import androidx.paging.PagingData
import com.gyleedev.githubsearch.core.testing.CoroutineRule
import com.gyleedev.githubsearch.core.testing.createDummyUser
import com.gyleedev.githubsearch.core.testing.ignoreUnused
import com.gyleedev.githubsearch.domain.model.FilterStatus
import com.gyleedev.githubsearch.domain.model.UpdateFavoriteResult
import com.gyleedev.githubsearch.domain.usecase.GetFavoritesUseCase
import com.gyleedev.githubsearch.domain.usecase.UpdateFavoriteStatusUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import io.mockk.junit4.MockKRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
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
class FavoriteViewModelTest {

    @get:Rule
    val mockkRule = MockKRule(this)

    @get:Rule
    val coroutineRule = CoroutineRule()

    private lateinit var viewModel: FavoriteViewModel

    @MockK
    lateinit var updateFavoriteUseCase: UpdateFavoriteStatusUseCase

    @MockK
    lateinit var getFavoritesUseCase: GetFavoritesUseCase

    @Before
    fun setUp() {
        coEvery { updateFavoriteUseCase(any()) } returns UpdateFavoriteResult.Success
        coEvery { getFavoritesUseCase(any()) } returns flowOf(PagingData.empty())
        viewModel = FavoriteViewModel(
            updateFavoriteUseCase = updateFavoriteUseCase,
            getFavoritesUseCase = getFavoritesUseCase,
        )
    }

    private fun TestScope.collectViewModelFlows() {
        backgroundScope.launch { viewModel.uiState.collect() }
        backgroundScope.launch { viewModel.items.collect() }
    }

    @Test
    fun `초기화 시 기본 상태가 ALL이며 ALL 필터로 아이템을 요청한다`() = runTest {
        // Given
        val expectedUiState = FavoriteUiState.Success(
            favoriteDialogState = false,
            filterDialogState = false,
            filterState = FilterStatus.ALL,
        )

        // When
        collectViewModelFlows()
        runCurrent()

        // Then
        coVerify(exactly = 1) { getFavoritesUseCase(FilterStatus.ALL).ignoreUnused() }
        coVerify(exactly = 0) { updateFavoriteUseCase(any()) }
        assertEquals(expectedUiState, viewModel.uiState.value)
    }

    @Test
    fun `필터를 업데이트하면 상태가 변경되고 해당 필터로 아이템을 요청한다`() = runTest {
        // Given
        val targetFilter = FilterStatus.REPO
        val expectedUiState = FavoriteUiState.Success(
            favoriteDialogState = false,
            filterDialogState = false,
            filterState = targetFilter,
        )

        collectViewModelFlows()
        runCurrent()

        // When
        viewModel.updateFilter(targetFilter)
        runCurrent()

        // Then
        coVerify(exactly = 1) { getFavoritesUseCase(FilterStatus.ALL).ignoreUnused() } // 초기
        coVerify(exactly = 1) { getFavoritesUseCase(targetFilter).ignoreUnused() } // 업데이트 후
        coVerify(exactly = 0) { updateFavoriteUseCase(any()) }
        assertEquals(expectedUiState, viewModel.uiState.value)
    }

    @Test
    fun `즐겨찾기 다이얼로그를 띄우면 상태가 반전되고 타겟 유저가 저장된다`() = runTest {
        // Given
        val targetUser = createDummyUser("test")
        val expectedDialogState = true

        collectViewModelFlows()
        runCurrent()

        // When
        viewModel.showFavoriteDialog(targetUser)
        runCurrent()

        // Then
        coVerify(exactly = 1) { getFavoritesUseCase(FilterStatus.ALL).ignoreUnused() }
        coVerify(exactly = 0) { updateFavoriteUseCase(any()) }
        val currentState = viewModel.uiState.first() as FavoriteUiState.Success
        assertEquals(expectedDialogState, currentState.favoriteDialogState)
    }

    @Test
    fun `즐겨찾기 상태를 업데이트하면 저장된 유저로 UseCase를 호출하고 다이얼로그를 닫는다`() = runTest {
        // Given
        val targetUser = createDummyUser("test")
        val expectedDialogState = false

        collectViewModelFlows()
        runCurrent()
        viewModel.showFavoriteDialog(targetUser) // 다이얼로그 열기
        runCurrent()

        // When
        viewModel.updateFavoriteStatus()
        runCurrent()

        // Then
        coVerify(exactly = 1) { getFavoritesUseCase(FilterStatus.ALL).ignoreUnused() }
        coVerify(exactly = 1) { updateFavoriteUseCase(targetUser) }

        val currentState = viewModel.uiState.first() as FavoriteUiState.Success
        assertEquals(expectedDialogState, currentState.favoriteDialogState)
    }

    @Test
    fun `필터 다이얼로그 상태를 토글하면 반전된다`() = runTest {
        // Given
        val expectedDialogState = true

        collectViewModelFlows()
        runCurrent()

        // When
        viewModel.updateShowFilterDialog()
        runCurrent()

        // Then
        coVerify(exactly = 1) { getFavoritesUseCase(FilterStatus.ALL).ignoreUnused() }
        coVerify(exactly = 0) { updateFavoriteUseCase(any()) }

        val currentState = viewModel.uiState.first() as FavoriteUiState.Success
        assertEquals(expectedDialogState, currentState.filterDialogState)
    }
}
