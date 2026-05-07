package com.gyleedev.githubsearch.feature.setting

import androidx.lifecycle.viewModelScope
import com.gyleedev.githubsearch.domain.usecase.CheckLoginStatusUseCase
import com.gyleedev.githubsearch.domain.usecase.ResetDataUseCase
import com.gyleedev.githubsearch.domain.usecase.RevokeApplicationUseCase
import com.gyleedev.ui.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.first
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
    private val _loginStatus = MutableSharedFlow<Boolean>()
    val loginStatus: SharedFlow<Boolean> = _loginStatus

    fun resetData() {
        viewModelScope.launch {
            resetDataUseCase()
        }
    }

    fun isKeyExists() {
        viewModelScope.launch {
            val result = checkLoginStatusUseCase().first()
            _loginStatus.emit(result)
        }
    }

    fun deleteKey() {
        viewModelScope.launch {
            revokeApplicationUseCase()
        }
    }
}
