package com.gyleedev.githubsearch.ui

import androidx.lifecycle.viewModelScope
import com.gyleedev.githubsearch.core.common.BaseViewModel
import com.gyleedev.githubsearch.domain.usecase.GetAccessTokenUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/*
getAccessTokenUseCase -> repositoryGetAccessToken 있으면 repositorySaveToken
하고 리턴
 */
@HiltViewModel
class MainViewModel @Inject constructor(
    private val getAccessTokenUseCase: GetAccessTokenUseCase,
) : BaseViewModel() {
    private val _alertLoginSuccess = MutableSharedFlow<Boolean>()
    val alertLoginSuccess: SharedFlow<Boolean> = _alertLoginSuccess

    fun getAccessToken(code: String) {
        viewModelScope.launch {
            val result = getAccessTokenUseCase(code)
            _alertLoginSuccess.emit(result)
        }
    }
}
