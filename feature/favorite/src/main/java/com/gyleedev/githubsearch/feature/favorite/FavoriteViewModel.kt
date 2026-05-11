package com.gyleedev.githubsearch.feature.favorite

import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.gyleedev.githubsearch.domain.model.FilterStatus
import com.gyleedev.githubsearch.domain.model.UserModel
import com.gyleedev.githubsearch.domain.usecase.GetFavoritesUseCase
import com.gyleedev.githubsearch.domain.usecase.UpdateFavoriteStatusUseCase
import com.gyleedev.ui.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/*
필터, 소트 종류가 많아지면 uiState 사용 고려
지금은 보일러 플레이트가 늘 뿐
1. update filterState
2. favorite 리스트 가져오는거
 */
@HiltViewModel
class FavoriteViewModel @Inject constructor(
    private val updateFavoriteUseCase: UpdateFavoriteStatusUseCase,
    private val getFavoritesUseCase: GetFavoritesUseCase,
) : BaseViewModel() {
    private val filterState = MutableStateFlow(FilterStatus.ALL)
    private val showFavoriteDialog = MutableStateFlow(false)
    private val showFilterDialog = MutableStateFlow(false)

    private val focusedUser = MutableStateFlow<UserModel?>(null)

    val uiState = combine(filterState, showFavoriteDialog, showFilterDialog) { filterState, showFavorite, showFilter ->
        FavoriteUiState.Success(
            favoriteDialogState = showFavorite,
            filterDialogState = showFilter,
            filterState = filterState,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = FavoriteUiState.Loading,
    )

    @OptIn(ExperimentalCoroutinesApi::class)
    val items =
        filterState
            .flatMapLatest {
                getFavoritesUseCase(it)
            }.cachedIn(viewModelScope)

    fun updateFilter(status: FilterStatus) {
        viewModelScope.launch {
            filterState.emit(status)
        }
    }

    fun showFavoriteDialog(user: UserModel) {
        viewModelScope.launch {
            focusedUser.emit(user)
            updateShowFavoriteDialog()
        }
    }

    fun updateFavoriteStatus() {
        viewModelScope.launch {
            focusedUser.value?.let {
                updateFavoriteUseCase(it)
            }
            focusedUser.emit(null)
            updateShowFavoriteDialog()
        }
    }

    fun updateShowFavoriteDialog() {
        viewModelScope.launch {
            showFavoriteDialog.emit(!showFavoriteDialog.value)
        }
    }

    fun updateShowFilterDialog() {
        viewModelScope.launch {
            showFilterDialog.emit(!showFilterDialog.value)
        }
    }
}
