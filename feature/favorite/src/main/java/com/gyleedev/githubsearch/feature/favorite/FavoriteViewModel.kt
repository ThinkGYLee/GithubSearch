package com.gyleedev.githubsearch.feature.favorite

import android.os.Build
import androidx.annotation.RequiresExtension
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.gyleedev.githubsearch.domain.model.FilterStatus
import com.gyleedev.githubsearch.domain.model.UserModel
import com.gyleedev.githubsearch.domain.usecase.GetFavoritesUseCase
import com.gyleedev.githubsearch.domain.usecase.UpdateFavoriteStatusUseCase
import com.gyleedev.ui.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/*
필터, 소트 종류가 많아지면 uiState 사용 고려
지금은 보일러 플레이트가 늘 뿐
1. update filterState
2. favorite 리스트 가져오는거
 */
@HiltViewModel
class FavoriteViewModel
    @Inject
    constructor(
        private val updateFavoriteUseCase: UpdateFavoriteStatusUseCase,
        private val getFavoritesUseCase: GetFavoritesUseCase,
    ) : BaseViewModel() {
        private val filterState = MutableStateFlow(FilterStatus.ALL)
        private val showFavoriteDialog = MutableStateFlow(false)
        private val showFilterDialog = MutableStateFlow(false)

        private val focusedUser = MutableStateFlow<UserModel?>(null)

        val uiState =
            combine(filterState, showFavoriteDialog, showFilterDialog) { filterState, showFavorite, showFilter ->
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

        @RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
        fun updateFilter(status: FilterStatus) {
            viewModelScope.launch(exceptionHandler) {
                filterState.emit(status)
            }
        }

        @RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
        fun showFavoriteDialog(user: UserModel) {
            viewModelScope.launch(exceptionHandler) {
                focusedUser.emit(user)
                updateShowFavoriteDialog()
            }
        }

        @RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
        fun updateFavoriteStatus() {
            viewModelScope.launch(exceptionHandler) {
                focusedUser.value?.let {
                    updateFavoriteUseCase(it)
                }
                focusedUser.emit(null)
                updateShowFavoriteDialog()
            }
        }

        @RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
        fun updateShowFavoriteDialog() {
            viewModelScope.launch(exceptionHandler) {
                showFavoriteDialog.emit(!showFavoriteDialog.value)
            }
        }

        @RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
        fun updateShowFilterDialog() {
            viewModelScope.launch(exceptionHandler) {
                showFilterDialog.emit(!showFilterDialog.value)
            }
        }
    }
