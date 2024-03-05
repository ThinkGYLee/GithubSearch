package com.gyleedev.githubsearch.ui.home

import android.os.Build
import androidx.annotation.RequiresExtension
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.gyleedev.githubsearch.core.BaseViewModel
import com.gyleedev.githubsearch.domain.model.UserModel
import com.gyleedev.githubsearch.domain.usecase.HomeGetUsersUseCase
import com.gyleedev.githubsearch.domain.usecase.HomeSearchUserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    getUsersUseCase: HomeGetUsersUseCase,
    private val searchUserUseCase: HomeSearchUserUseCase,
) : BaseViewModel() {
    private val _searchId = MutableStateFlow("")

    private val _userInfo = MutableStateFlow<UserModel?>(null)
    val userInfo: StateFlow<UserModel?> = _userInfo

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    val users = getUsersUseCase().cachedIn(viewModelScope)

    @RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
    fun getUser() {
        viewModelScope.launch(exceptionHandler) {
            changeLoadingState()
            _userInfo.emit(searchUserUseCase(_searchId.value))
            changeLoadingState()
        }
    }

    fun changeLoadingState() {
        viewModelScope.launch {
            _loading.emit(!_loading.value)
        }
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