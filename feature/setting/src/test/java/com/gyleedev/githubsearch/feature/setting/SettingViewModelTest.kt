package com.gyleedev.githubsearch.feature.setting

import com.gyleedev.githubsearch.core.testing.CoroutineRule
import com.gyleedev.githubsearch.core.testing.collectIn
import com.gyleedev.githubsearch.core.testing.ignoreUnused
import com.gyleedev.githubsearch.domain.model.ResetDataResult
import com.gyleedev.githubsearch.domain.model.RevokeResult
import com.gyleedev.githubsearch.domain.usecase.CheckLoginStatusUseCase
import com.gyleedev.githubsearch.domain.usecase.ResetDataUseCase
import com.gyleedev.githubsearch.domain.usecase.RevokeApplicationUseCase
import com.gyleedev.githubsearch.feature.setting.SettingUiState.Success
import com.gyleedev.githubsearch.feature.setting.model.SettingEvent
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import io.mockk.junit4.MockKRule
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest

@OptIn(ExperimentalCoroutinesApi::class)
class SettingViewModelTest {
    @get:Rule
    val mockkRule = MockKRule(this)

    @get:Rule
    val coroutineRule = CoroutineRule()

    private lateinit var viewModel: SettingViewModel

    @MockK
    lateinit var resetDataUseCase: ResetDataUseCase

    @MockK
    lateinit var revokeApplicationUseCase: RevokeApplicationUseCase

    @MockK
    lateinit var checkLoginStatusUseCase: CheckLoginStatusUseCase

    @Before
    fun setUp() {
        coEvery { resetDataUseCase() } returns ResetDataResult.Success
        coEvery { revokeApplicationUseCase() } returns RevokeResult.SUCCESS
        coEvery { checkLoginStatusUseCase() } returns flowOf(false)
        viewModel =
            SettingViewModel(
                resetDataUseCase = resetDataUseCase,
                revokeApplicationUseCase = revokeApplicationUseCase,
                checkLoginStatusUseCase = checkLoginStatusUseCase,
            )
    }

    private fun TestScope.collectViewModelFlows() {
        backgroundScope.launch { viewModel.uiState.collect() }
    }

    @Test
    fun `테마 다이얼로그 이벤트를 받으면 상태가 양방향으로 토글된다`() =
        runTest {
            // Given
            val expectedThemeDialogTrue = true
            val expectedThemeDialogFalse = false

            collectViewModelFlows()
            runCurrent()

            // When 1
            viewModel.changDialogState(SettingEvent.THEME)
            runCurrent()

            // Then 1
            var currentState = viewModel.uiState.first() as Success
            assertEquals(expectedThemeDialogTrue, currentState.showThemeDialog)

            // When 2
            viewModel.changDialogState(SettingEvent.THEME)
            runCurrent()

            // Then 2
            currentState = viewModel.uiState.first() as Success
            assertEquals(expectedThemeDialogFalse, currentState.showThemeDialog)

            coVerify(exactly = 0) { checkLoginStatusUseCase().ignoreUnused() }
            coVerify(exactly = 0) { resetDataUseCase() }
            coVerify(exactly = 0) { revokeApplicationUseCase() }
        }

    @Test
    fun `언어 다이얼로그 이벤트를 받으면 상태가 양방향으로 토글된다`() =
        runTest {
            // Given
            val expectedLanguageDialogTrue = true
            val expectedLanguageDialogFalse = false

            collectViewModelFlows()
            runCurrent()

            // When 1
            viewModel.changDialogState(SettingEvent.LANGUAGE)
            runCurrent()

            // Then 1
            var currentState = viewModel.uiState.first() as Success
            assertEquals(expectedLanguageDialogTrue, currentState.showLanguageDialog)

            // When 2
            viewModel.changDialogState(SettingEvent.LANGUAGE)
            runCurrent()

            // Then 2
            currentState = viewModel.uiState.first() as Success
            assertEquals(expectedLanguageDialogFalse, currentState.showLanguageDialog)

            coVerify(exactly = 0) { checkLoginStatusUseCase().ignoreUnused() }
            coVerify(exactly = 0) { resetDataUseCase() }
            coVerify(exactly = 0) { revokeApplicationUseCase() }
        }

    @Test
    fun `리셋 다이얼로그 이벤트를 받으면 상태가 양방향으로 토글된다`() =
        runTest {
            // Given
            val expectedResetDialogTrue = true
            val expectedResetDialogFalse = false

            collectViewModelFlows()
            runCurrent()

            // When 1
            viewModel.changDialogState(SettingEvent.RESET)
            runCurrent()

            // Then 1
            var currentState = viewModel.uiState.first() as Success
            assertEquals(expectedResetDialogTrue, currentState.showResetDialog)

            // When 2
            viewModel.changDialogState(SettingEvent.RESET)
            runCurrent()

            // Then 2
            currentState = viewModel.uiState.first() as Success
            assertEquals(expectedResetDialogFalse, currentState.showResetDialog)

            coVerify(exactly = 0) { checkLoginStatusUseCase().ignoreUnused() }
            coVerify(exactly = 0) { resetDataUseCase() }
            coVerify(exactly = 0) { revokeApplicationUseCase() }
        }

    @Test
    fun `정의되지 않은 이벤트를 받으면 상태가 변경되지 않는다`() =
        runTest {
            // Given
            collectViewModelFlows()
            runCurrent()

            val initialState = viewModel.uiState.first() as Success

            // When
            viewModel.changDialogState(SettingEvent.POLICY)
            viewModel.changDialogState(SettingEvent.NONE)
            runCurrent()

            // Then
            coVerify(exactly = 0) { checkLoginStatusUseCase().ignoreUnused() }
            coVerify(exactly = 0) { resetDataUseCase() }
            coVerify(exactly = 0) { revokeApplicationUseCase() }

            val currentState = viewModel.uiState.first() as Success
            assertEquals(initialState, currentState)
        }

    @Test
    fun `권한 이벤트를 받을 때 로그인 상태이면 로그아웃 다이얼로그가 열린다`() =
        runTest {
            // Given
            val expectedLogoutDialogState = true
            coEvery { checkLoginStatusUseCase() } returns flowOf(true)

            collectViewModelFlows()
            runCurrent()

            // When
            viewModel.changDialogState(SettingEvent.AUTH)
            runCurrent()

            // Then
            coVerify(exactly = 1) { checkLoginStatusUseCase().ignoreUnused() }
            coVerify(exactly = 0) { resetDataUseCase() }
            coVerify(exactly = 0) { revokeApplicationUseCase() }

            val currentState = viewModel.uiState.first() as Success
            assertEquals(expectedLogoutDialogState, currentState.showLogoutDialog)
        }

    @Test
    fun `권한 이벤트를 받을 때 비로그인 상태이면 로그인 다이얼로그가 열린다`() =
        runTest {
            // Given
            val expectedLoginDialogState = true
            coEvery { checkLoginStatusUseCase() } returns flowOf(false)

            collectViewModelFlows()
            runCurrent()

            // When
            viewModel.changDialogState(SettingEvent.AUTH)
            runCurrent()

            // Then
            coVerify(exactly = 1) { checkLoginStatusUseCase().ignoreUnused() }
            coVerify(exactly = 0) { resetDataUseCase() }
            coVerify(exactly = 0) { revokeApplicationUseCase() }

            val currentState = viewModel.uiState.first() as Success
            assertEquals(expectedLoginDialogState, currentState.showLoginDialog)
        }

    @Test
    fun `로그인 또는 로그아웃 다이얼로그 닫기 이벤트를 받으면 명시적으로 false가 된다`() =
        runTest {
            // Given
            val expectedLoginDialogState = false
            val expectedLogoutDialogState = false

            // 시나리오 1: 로그아웃 다이얼로그 닫기
            coEvery { checkLoginStatusUseCase() } returns flowOf(true)
            collectViewModelFlows()
            runCurrent()

            viewModel.changDialogState(SettingEvent.AUTH) // 로그아웃 다이얼로그 오픈
            runCurrent()
            assertEquals(true, (viewModel.uiState.first() as Success).showLogoutDialog)

            // When 1
            viewModel.changDialogState(SettingEvent.LOGOUT)
            runCurrent()

            // Then 1
            var currentState = viewModel.uiState.first() as Success
            assertEquals(expectedLogoutDialogState, currentState.showLogoutDialog)

            // 시나리오 2: 로그인 다이얼로그 닫기
            coEvery { checkLoginStatusUseCase() } returns flowOf(false)
            collectViewModelFlows()
            runCurrent()

            viewModel.changDialogState(SettingEvent.AUTH) // 로그인 다이얼로그 오픈
            runCurrent()
            assertEquals(true, (viewModel.uiState.first() as Success).showLoginDialog)

            // When 2
            viewModel.changDialogState(SettingEvent.LOGIN)
            runCurrent()

            // Then 2
            currentState = viewModel.uiState.first() as Success
            assertEquals(expectedLoginDialogState, currentState.showLoginDialog)
        }

    @Test
    fun `데이터 리셋 요청 시 UseCase를 호출하고 결과를 SharedFlow로 방출한다`() =
        runTest {
            // Given
            val expectedResult = ResetDataResult.Success
            // TODO: Turbine 라이브러리가 도입되면, 명시적인 List 수집 코드를 삭제하고 .test { awaitItem() } 블록으로 대체할 것.
            // 현재는 Turbine 없이 SharedFlow(replay=0)의 "정확히 1회 방출" 여부와 유실 방지를 검증하기 위해 미리 List에 수집하는 정석 패턴 사용.
            val resultList = mutableListOf<ResetDataResult>()
            coEvery { resetDataUseCase() } returns expectedResult

            collectViewModelFlows()
            viewModel.showResetResult.collectIn(backgroundScope, resultList)
            runCurrent()

            // When
            viewModel.resetData()
            runCurrent()

            // Then
            coVerify(exactly = 0) { checkLoginStatusUseCase().ignoreUnused() }
            coVerify(exactly = 1) { resetDataUseCase() }
            coVerify(exactly = 0) { revokeApplicationUseCase() }

            assertEquals(1, resultList.size)
            assertEquals(expectedResult, resultList.first())
        }

    @Test
    fun `권한 취소 요청 시 UseCase를 호출하고 결과를 SharedFlow로 방출한다`() =
        runTest {
            // Given
            val expectedResult = RevokeResult.SUCCESS
            // TODO: Turbine 라이브러리가 도입되면, 명시적인 List 수집 코드를 삭제하고 .test { awaitItem() } 블록으로 대체할 것.
            // 현재는 Turbine 없이 SharedFlow(replay=0)의 "정확히 1회 방출" 여부와 유실 방지를 검증하기 위해 미리 List에 수집하는 정석 패턴 사용.
            val resultList = mutableListOf<RevokeResult>()

            coEvery { revokeApplicationUseCase() } returns expectedResult

            collectViewModelFlows()
            backgroundScope.launch { viewModel.showRevokeResult.collect { resultList.add(it) } }
            runCurrent()

            // When
            viewModel.revokeApplication()
            runCurrent()

            // Then
            coVerify(exactly = 0) { checkLoginStatusUseCase().ignoreUnused() }
            coVerify(exactly = 0) { resetDataUseCase() }
            coVerify(exactly = 1) { revokeApplicationUseCase() }

            assertEquals(1, resultList.size)
            assertEquals(expectedResult, resultList.first())
        }
}
