package com.gyleedev.githubsearch.ui

import android.net.Uri
import androidx.lifecycle.viewModelScope
import com.gyleedev.githubsearch.BuildConfig
import com.gyleedev.githubsearch.domain.model.GetAccessTokenUseCaseResult
import com.gyleedev.githubsearch.domain.usecase.GetAccessTokenUseCase
import com.gyleedev.ui.BaseViewModel
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
    private val _alertLoginSuccess = MutableSharedFlow<GetAccessTokenUseCaseResult>()
    val alertLoginSuccess: SharedFlow<GetAccessTokenUseCaseResult> = _alertLoginSuccess

    private val _launchOAuthEvent = MutableSharedFlow<String>()
    val launchOAuthEvent: SharedFlow<String> = _launchOAuthEvent

    fun getAccessToken(code: String) {
        viewModelScope.launch {
            val result = getAccessTokenUseCase(code)
            _alertLoginSuccess.emit(result)
        }
    }

    fun requestGithubLogin() {
        val clientId = BuildConfig.CLIENT_ID
        val loginUrl =
            Uri.Builder()
                .scheme("https")
                .authority("github.com")
                .appendPath("login")
                .appendPath("oauth")
                .appendPath("authorize")
                .appendQueryParameter("client_id", clientId)
                .build()
                .toString()

        viewModelScope.launch {
            _launchOAuthEvent.emit(loginUrl)
        }
    }
}
