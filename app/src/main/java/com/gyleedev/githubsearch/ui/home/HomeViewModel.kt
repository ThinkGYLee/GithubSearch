package com.gyleedev.githubsearch.ui.home

import android.os.Build
import androidx.annotation.RequiresExtension
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.gyleedev.githubsearch.BuildConfig
import com.gyleedev.githubsearch.core.BaseViewModel
import com.gyleedev.githubsearch.data.remote.response.GithubAccessResponse
import com.gyleedev.githubsearch.data.repository.GitHubRepository
import com.gyleedev.githubsearch.domain.model.UserModel
import com.gyleedev.githubsearch.domain.usecase.HomeGetUsersUseCase
import com.gyleedev.githubsearch.domain.usecase.HomeSearchUserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    getUsersUseCase: HomeGetUsersUseCase,
    private val searchUserUseCase: HomeSearchUserUseCase,
    private val repository: GitHubRepository
) : BaseViewModel() {
    private val _searchId = MutableStateFlow("")

    private val _userInfo = MutableStateFlow<UserModel?>(null)
    val userInfo: StateFlow<UserModel?> = _userInfo

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    val users = getUsersUseCase().cachedIn(viewModelScope)

    private val _saveToken = MutableSharedFlow<String>()
    val saveToken: SharedFlow<String> = _saveToken

    @RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
    fun getUser() {
        viewModelScope.launch(exceptionHandler) {
            changeLoadingState()
            _userInfo.emit(searchUserUseCase(_searchId.value))
            changeLoadingState()
        }
    }

    suspend fun getAccessToken(code: String) {
        withContext(Dispatchers.IO) {
            val response = repository.getAccessToken(
                id = BuildConfig.GIT_ID,
                secret = BuildConfig.GIT_SECRET,
                code = code
            )

            if (response.isSuccessful) {
                response.body().let {
                    if (it != null) {
                        emitAccessToken(githubAccessResponse = it)
                    }
                }
            }
        }
    }

    private suspend fun emitAccessToken(githubAccessResponse: GithubAccessResponse) {
        _saveToken.emit(githubAccessResponse.accessToken)
    }

    suspend fun changeLoadingState() {
        _loading.emit(!_loading.value)
    }

    fun updateSearchId(id: String) {
        viewModelScope.launch {
            _searchId.emit(id)
        }
    }

    fun resetUser() {
        viewModelScope.launch {
            _userInfo.emit(null)
        }
    }
}