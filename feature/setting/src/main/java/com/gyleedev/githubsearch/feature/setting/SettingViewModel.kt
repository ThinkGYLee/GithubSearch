package com.gyleedev.githubsearch.feature.setting

import androidx.lifecycle.viewModelScope
import com.gyleedev.githubsearch.domain.model.ResetDataResult
import com.gyleedev.githubsearch.domain.model.RevokeResult
import com.gyleedev.githubsearch.domain.usecase.CheckLoginStatusUseCase
import com.gyleedev.githubsearch.domain.usecase.ResetDataUseCase
import com.gyleedev.githubsearch.domain.usecase.RevokeApplicationUseCase
import com.gyleedev.githubsearch.feature.setting.model.SettingEvent
import com.gyleedev.ui.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/*
1. 데이터 리셋
2. 권한 리셋
3. 로그인 상태 체크
 */
@HiltViewModel
class SettingViewModel @Inject constructor(
    private val resetDataUseCase: ResetDataUseCase,
    private val revokeApplicationUseCase: RevokeApplicationUseCase,
    private val checkLoginStatusUseCase: CheckLoginStatusUseCase,
) : BaseViewModel() {

    private val showLanguageDialog = MutableStateFlow(false)
    private val showThemeDialog = MutableStateFlow(false)
    private val showResetDialog = MutableStateFlow(false)
    private val showLoginDialog = MutableStateFlow(false)
    private val showLogoutDialog = MutableStateFlow(false)
    private val _showRevokeResult = MutableSharedFlow<RevokeResult>()
    val showRevokeResult: SharedFlow<RevokeResult> = _showRevokeResult
    private val _showResetResult = MutableSharedFlow<ResetDataResult>()
    val showResetResult: SharedFlow<ResetDataResult> = _showResetResult

    val uiState = combine(showThemeDialog, showLanguageDialog, showLoginDialog, showLogoutDialog, showResetDialog) { theme, lang, login, logout, reset ->
        SettingUiState.Success(
            showLanguageDialog = lang,
            showThemeDialog = theme,
            showResetDialog = reset,
            showLoginDialog = login,
            showLogoutDialog = logout,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000),
        initialValue = SettingUiState.Loading,
    )

    fun resetData() {
        viewModelScope.launch {
            _showResetResult.emit(resetDataUseCase())
        }
    }

    fun revokeApplication() {
        viewModelScope.launch {
            _showRevokeResult.emit(revokeApplicationUseCase())
        }
    }

    fun changDialogState(settingEvent: SettingEvent) {
        viewModelScope.launch {
            when (settingEvent) {
                SettingEvent.THEME -> {
                    showThemeDialog.emit(
                        !showThemeDialog.value,
                    )
                }

                SettingEvent.LANGUAGE -> {
                    showLanguageDialog.emit(
                        !showLanguageDialog.value,
                    )
                }

                SettingEvent.INFORMATION -> {
                    val result = checkLoginStatusUseCase().first()
                    if (result) {
                        showLogoutDialog.emit(true)
                    } else {
                        showLoginDialog.emit(true)
                    }
                }

                SettingEvent.LOGIN -> {
                    showLoginDialog.emit(false)
                }

                SettingEvent.LOGOUT -> {
                    showLogoutDialog.emit(false)
                }

                SettingEvent.RESET -> {
                    showResetDialog.emit(
                        !showResetDialog.value,
                    )
                }

                else -> {}
            }
        }
    }
}
