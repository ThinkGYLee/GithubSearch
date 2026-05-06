package com.gyleedev.githubsearch.feature.home

import android.os.Build
import androidx.annotation.RequiresExtension
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.gyleedev.githubsearch.core.common.BaseViewModel
import com.gyleedev.githubsearch.domain.model.SearchStatus
import com.gyleedev.githubsearch.domain.model.UserModel
import com.gyleedev.githubsearch.domain.usecase.FetchUserUseCase
import com.gyleedev.githubsearch.domain.usecase.GetUserUseCase
import com.gyleedev.githubsearch.domain.usecase.GetUsersUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/*
    BaseViewModel의 에러처리법 보고 에러처리 개선안 고안
    1. UserList 가져오는거
    2. Search 했을 때 query 가지고 debounce 로 가져오는거
    3. web fetch 해서 User 가져오는거
 */
@HiltViewModel
class HomeViewModel @Inject constructor(
    getUsersUseCase: GetUsersUseCase,
    private val getUserUseCase: GetUserUseCase,
    private val fetchUserUseCase: FetchUserUseCase,
) : BaseViewModel() {
    private val searchQuery = MutableStateFlow("")
    private val isLoading = MutableStateFlow(false)

    @OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
    private val searchedUser: StateFlow<UserModel?> = searchQuery
        .debounce(300L)
        .distinctUntilChanged()
        .flatMapLatest { query ->
            if (query.isBlank()) {
                flowOf(null)
            } else {
                getUserUseCase(query)
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000),
            initialValue = null,
        )

    val users = getUsersUseCase().cachedIn(viewModelScope)

    private val _errorAlert = MutableSharedFlow<SearchStatus>()
    val errorAlert: SharedFlow<SearchStatus> = _errorAlert

    private val _requestAuthentication = MutableSharedFlow<Unit>()
    val requestAuthentication: SharedFlow<Unit> = _requestAuthentication

    val uiState = combine(searchQuery, searchedUser, isLoading) { query, user, isLoading ->
        HomeUiState.Success(
            searchQuery = query,
            searchedUser = user,
            isLoading = isLoading,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5_000),
        initialValue = HomeUiState.Loading,
    )

    @RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
    fun searchUser(id: String) {
        viewModelScope.launch(exceptionHandler) {
            isLoading.emit(true)
            val status = fetchUserUseCase(id)
            if (status != SearchStatus.SUCCESS) {
                alertResponseFail(status)
            }
            isLoading.emit(false)
        }
    }

    private suspend fun alertResponseFail(status: SearchStatus) {
        when (status) {
            SearchStatus.NEED_AUTHENTICATION -> {
                _requestAuthentication.emit(Unit)
            }

            else -> {
                _errorAlert.emit(status)
            }
        }
    }

    fun updateSearchId(id: String) {
        viewModelScope.launch {
            searchQuery.emit(id)
        }
    }
}
