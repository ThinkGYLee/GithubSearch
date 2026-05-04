package com.gyleedev.githubsearch.ui.setting

import androidx.lifecycle.viewModelScope
import com.gyleedev.githubsearch.core.BaseViewModel
import com.gyleedev.githubsearch.domain.usecase.CheckLoginStatusUseCase
import com.gyleedev.githubsearch.domain.usecase.ResetDataUseCase
import com.gyleedev.githubsearch.domain.usecase.RevokeApplicationUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

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
