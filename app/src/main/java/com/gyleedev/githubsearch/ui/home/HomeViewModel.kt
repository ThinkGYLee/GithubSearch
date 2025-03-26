package com.gyleedev.githubsearch.ui.home

import android.os.Build
import androidx.annotation.RequiresExtension
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.gyleedev.githubsearch.core.BaseViewModel
import com.gyleedev.githubsearch.domain.model.SearchStatus
import com.gyleedev.githubsearch.domain.model.UserModel
import com.gyleedev.githubsearch.domain.model.UserWrapper
import com.gyleedev.githubsearch.domain.usecase.HomeGetUsersUseCase
import com.gyleedev.githubsearch.domain.usecase.HomeSearchUserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    getUsersUseCase: HomeGetUsersUseCase,
    private val searchUserUseCase: HomeSearchUserUseCase
) : BaseViewModel() {
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _userInfo = MutableStateFlow<UserModel?>(null)
    val userInfo: StateFlow<UserModel?> = _userInfo

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    val users = getUsersUseCase().cachedIn(viewModelScope)

    private val _errorAlert = MutableSharedFlow<SearchStatus>()
    val errorAlert: SharedFlow<SearchStatus> = _errorAlert

    private val _requestAuthentication = MutableSharedFlow<Unit>()
    val requestAuthentication: SharedFlow<Unit> = _requestAuthentication

    @RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
    fun getUser(id: String) {
        viewModelScope.launch(exceptionHandler) {
            when (val userWrapper = searchUserUseCase(id)) {
                is UserWrapper.FromDatabase -> {
                    _userInfo.emit(userWrapper.data)
                }

                is UserWrapper.Success -> {
                    _userInfo.emit(userWrapper.data)
                }

                is UserWrapper.Failure -> {
                    alertResponseFail(userWrapper)
                }
            }
            _loading.emit(false)
        }
    }

    private suspend fun alertResponseFail(userWrapper: UserWrapper) {
        val wrapper = userWrapper as UserWrapper.Failure
        when (wrapper.status) {
            SearchStatus.NEED_AUTHENTICATION -> {
                _requestAuthentication.emit(Unit)
            }

            else -> {
                _errorAlert.emit(wrapper.status)
            }
        }
    }

    suspend fun changeLoadingState() {
        _loading.emit(!_loading.value)
    }

    suspend fun stopLoading() {
        _loading.emit(false)
    }

    fun updateSearchId(id: String) {
        viewModelScope.launch {
            _searchQuery.emit(id)
        }
    }

    fun resetUser() {
        viewModelScope.launch {
            _userInfo.emit(null)
        }
    }
}
