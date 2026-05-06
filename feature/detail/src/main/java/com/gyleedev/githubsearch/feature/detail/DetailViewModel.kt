package com.gyleedev.githubsearch.feature.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.gyleedev.githubsearch.core.common.BaseViewModel
import com.gyleedev.githubsearch.domain.model.DetailFeed
import com.gyleedev.githubsearch.domain.model.RepositoryModel
import com.gyleedev.githubsearch.domain.model.UserModel
import com.gyleedev.githubsearch.domain.usecase.GetRepositoryUseCase
import com.gyleedev.githubsearch.domain.usecase.GetUserUseCase
import com.gyleedev.githubsearch.domain.usecase.UpdateFavoriteStatusUseCase
import com.gyleedev.githubsearch.domain.usecase.UpdateUserFromGithubUseCase
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

@HiltViewModel
class DetailViewModel
@Inject
constructor(
    private val updateUserFromGithubUseCase: UpdateUserFromGithubUseCase,
    private val getUserUseCase: GetUserUseCase,
    private val getRepositoryUseCase: GetRepositoryUseCase,
    private val updateFavoriteStatusUseCase: UpdateFavoriteStatusUseCase,
    savedStateHandle: SavedStateHandle,
) : BaseViewModel() {
    private val userId = MutableStateFlow("")

    init {
        val id = savedStateHandle.get<String>("id")
        viewModelScope.launch {
            if (id != null) {
                userId.emit(id)
                updateUserAndRepositoryData()
            }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
    val user: StateFlow<UserModel?> = userId
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

    @OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
    val repo: StateFlow<List<RepositoryModel>> = userId
        .flatMapLatest { query ->
            if (query.isBlank()) {
                flowOf(emptyList())
            } else {
                getRepositoryUseCase(query)
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000),
            initialValue = emptyList(),
        )

    private suspend fun updateUserAndRepositoryData() {
        updateUserFromGithubUseCase(userId.value)
    }

    fun updateFavoriteStatus() {
        viewModelScope.launch {
            updateFavoriteStatusUseCase(id = userId.value)
        }
    }
}
