package com.gyleedev.githubsearch.feature.home

import android.os.Build
import androidx.annotation.RequiresExtension
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.gyleedev.githubsearch.domain.model.SearchStatus
import com.gyleedev.githubsearch.domain.model.UserDeleteResult
import com.gyleedev.githubsearch.domain.model.UserModel
import com.gyleedev.githubsearch.domain.usecase.DeleteSelectedUsersUseCase
import com.gyleedev.githubsearch.domain.usecase.FetchUserUseCase
import com.gyleedev.githubsearch.domain.usecase.GetUserWithFlowUseCase
import com.gyleedev.githubsearch.domain.usecase.GetUsersUseCase
import com.gyleedev.ui.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
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
    val deleteSelectedUsersUseCase: DeleteSelectedUsersUseCase,
    private val getUserWithFlowUseCase: GetUserWithFlowUseCase,
    private val fetchUserUseCase: FetchUserUseCase,
) : BaseViewModel() {
    private val searchQuery = MutableStateFlow("")
    private val isLoading = MutableStateFlow(false)
    private val mode = MutableStateFlow(HomeMode.DEFAULT)
    private val selectedUsers = MutableStateFlow<Set<String>>(emptySet())
    private val showRequestAuthDialog = MutableStateFlow(false)
    private val showDeleteDialog = MutableStateFlow(false)

    @OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
    private val searchedUser: StateFlow<UserModel?> = searchQuery
        .debounce(300L)
        .distinctUntilChanged()
        .flatMapLatest { query ->
            if (query.isBlank()) {
                flowOf(null)
            } else {
                getUserWithFlowUseCase(query)
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000),
            initialValue = null,
        )

    val users = getUsersUseCase().cachedIn(viewModelScope)

    val uiState = combine(
        searchQuery,
        searchedUser,
        isLoading,
        mode,
        selectedUsers,
        showRequestAuthDialog,
        showDeleteDialog,
    ) { args ->
        val query = args[0] as String
        val user = args[1] as UserModel?
        val loading = args[2] as Boolean
        val mode = args[3] as HomeMode
        val selected = args[4] as Set<String>
        val showAuth = args[5] as Boolean
        val showDelete = args[6] as Boolean

        val searchResult = if (user == null) {
            SearchUiState.Empty
        } else {
            SearchUiState.Success(
                login = user.login,
                avatar = user.avatar,
                name = user.name,
                bio = user.bio,
            )
        }

        HomeUiState.Success(
            searchQuery = query,
            isLoading = loading,
            searchState = searchResult,
            mode = mode,
            selectedUsers = selected,
            showRequestAuthDialog = showAuth,
            showDeleteDialog = showDelete,
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
            if (status == SearchStatus.NEED_AUTHENTICATION) {
                changeAuthDialogState(true)
            }
            isLoading.emit(false)
        }
    }

    @RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
    fun updateSearchId(id: String) {
        viewModelScope.launch(exceptionHandler) {
            searchQuery.emit(id)
        }
    }

    @RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
    fun changeSearchBarState(isActive: Boolean) {
        viewModelScope.launch(exceptionHandler) {
            val emitState = if (isActive) {
                HomeMode.SEARCH
            } else {
                HomeMode.DEFAULT
            }
            mode.emit(emitState)
        }
    }

    @RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
    fun changeSelectionState(login: String) {
        viewModelScope.launch(exceptionHandler) {
            val current = selectedUsers.value
            if (current.contains(login)) {
                selectedUsers.emit(current - login)
            } else {
                selectedUsers.emit(current + login)
                if (mode.value != HomeMode.SELECT) {
                    mode.emit(HomeMode.SELECT)
                }
            }
        }
    }

    @RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
    fun clearSelection() {
        viewModelScope.launch(exceptionHandler) {
            selectedUsers.emit(emptySet())
            mode.emit(HomeMode.DEFAULT)
        }
    }

    @RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
    fun selectCheckBox(logins: List<String>) {
        viewModelScope.launch(exceptionHandler) {
            val currentSelected = selectedUsers.value
            if (logins.all { currentSelected.contains(it) }) {
                selectedUsers.emit(currentSelected - logins.toSet())
            } else {
                selectedUsers.emit(currentSelected + logins.toSet())
            }
        }
    }

    @RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
    fun changeAuthDialogState(state: Boolean) {
        viewModelScope.launch(exceptionHandler) {
            showRequestAuthDialog.emit(state)
        }
    }

    @RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
    fun changeDeleteDialogState(state: Boolean) {
        viewModelScope.launch(exceptionHandler) {
            showDeleteDialog.emit(state)
        }
    }

    @RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
    fun deleteSelectedUsers() {
        viewModelScope.launch(exceptionHandler) {
            val result = deleteSelectedUsersUseCase(selectedUsers.value)
            if (result is UserDeleteResult.Success) {
                clearSelection()
            }
            changeDeleteDialogState(false)
        }
    }

    @RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
    fun updateSelectedFavorite() {
        viewModelScope.launch(exceptionHandler) {
        }
    }
}
