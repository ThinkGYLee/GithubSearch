package com.gyleedev.githubsearch.feature.detail

import android.os.Build
import androidx.annotation.RequiresExtension
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.gyleedev.githubsearch.domain.model.RepositoryModel
import com.gyleedev.githubsearch.domain.model.UserModel
import com.gyleedev.githubsearch.domain.usecase.GetReposWithFlowUseCase
import com.gyleedev.githubsearch.domain.usecase.GetUserWithFlowUseCase
import com.gyleedev.githubsearch.domain.usecase.UpdateFavoriteStatusUseCase
import com.gyleedev.githubsearch.domain.usecase.UpdateUserFromGithubUseCase
import com.gyleedev.ui.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/*
1. user Flow로 받아오는 거
2. repos flow로 받아오는 거
3. user, repo 업데이트(싱크 맞추는 기능)
4. favoriteState 업데이트
 */
@HiltViewModel
class DetailViewModel @Inject constructor(
    private val updateUserFromGithubUseCase: UpdateUserFromGithubUseCase,
    private val getUserWithFlowUseCase: GetUserWithFlowUseCase,
    private val getReposWithFlowUseCase: GetReposWithFlowUseCase,
    private val updateFavoriteStatusUseCase: UpdateFavoriteStatusUseCase,
    savedStateHandle: SavedStateHandle,
) : BaseViewModel() {
    private val userId = MutableStateFlow("")

    init {
        val id = savedStateHandle.get<String>("id")
        viewModelScope.launch {
            if (!id.isNullOrBlank()) {
                userId.emit(id)
                updateUserFromGithubUseCase(id)
            }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
    val user: StateFlow<UserModel?> = userId
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

    @OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
    val repo: StateFlow<List<RepositoryModel>> = userId
        .flatMapLatest { query ->
            if (query.isBlank()) {
                flowOf(emptyList())
            } else {
                getReposWithFlowUseCase(query)
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000),
            initialValue = emptyList(),
        )

    @RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
    fun updateFavoriteStatus() {
        viewModelScope.launch(exceptionHandler) {
            if (user.value != null) {
                updateFavoriteStatusUseCase(user.value as UserModel)
            }
        }
    }
}
