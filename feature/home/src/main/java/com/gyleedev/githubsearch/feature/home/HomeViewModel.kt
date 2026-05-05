package com.gyleedev.githubsearch.feature.home

import android.os.Build
import androidx.annotation.RequiresExtension
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.gyleedev.githubsearch.core.common.BaseViewModel
import com.gyleedev.githubsearch.domain.model.SearchStatus
import com.gyleedev.githubsearch.domain.model.UserModel
import com.gyleedev.githubsearch.domain.model.UserSearchResult
import com.gyleedev.githubsearch.domain.usecase.GetUsersUseCase
import com.gyleedev.githubsearch.domain.usecase.SearchUserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/*
   홈 문제점 개편안
   1. stability 해결
   2. searchbar deprecate 된거 말고 m3 expressive 로 수정
   3. 기능 수정
    3-1. 서치바 debounce 걸어서 검색어 넣었을 때 결과물 있으면 결과 물 넣어.
    3-2. 웹 찔러서 결과물 받아왔으면 repo 정보 상관 없이 일단 넣어. repo는 detail 에서
    3-3. 검색 버튼을 넣던가 뭐 해야지 엔터로만 검색이 웬말?
    3-4. 뭔가 보여주는 정보를 추가해
    3-5. baseviewmodel의 에러처리법 보고 에러 처리법 다시 익혀. 저거 왜쓰나 몰라
    3-6. 홈에 정렬을 넣던가 해야할듯
 */
@HiltViewModel
class HomeViewModel
@Inject
constructor(
    getUsersUseCase: GetUsersUseCase,
    private val searchUserUseCase: SearchUserUseCase,
) : BaseViewModel() {
    private val searchQuery = MutableStateFlow("")
    private val searchedUser = MutableStateFlow<UserModel?>(null)
    private val isLoading = MutableStateFlow(false)

    val users = getUsersUseCase().cachedIn(viewModelScope)

    private val _errorAlert = MutableSharedFlow<SearchStatus>()
    val errorAlert: SharedFlow<SearchStatus> = _errorAlert

    private val _requestAuthentication = MutableSharedFlow<Unit>()
    val requestAuthentication: SharedFlow<Unit> = _requestAuthentication

    val uiState = combine(searchQuery, searchedUser, isLoading) { query, user, isLoading ->
        HomeUiState.Success(
            searchQuery = query,
            searchedUser = user,
            isLoading = isLoading
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5_000),
        initialValue = HomeUiState.Loading,
    )

    @RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
    fun getUser(id: String) {
        viewModelScope.launch(exceptionHandler) {
            isLoading.emit(true)
            val result = searchUserUseCase(id)
            when (result) {
                is UserSearchResult.FromDatabase -> {
                    searchedUser.emit(result.data)
                }

                is UserSearchResult.Success -> {
                    searchedUser.emit(result.data)
                }

                is UserSearchResult.Failure -> {
                    alertResponseFail(result.status)
                }
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

    fun resetQuery() {
        viewModelScope.launch {
            searchedUser.emit(null)
        }
    }
}
