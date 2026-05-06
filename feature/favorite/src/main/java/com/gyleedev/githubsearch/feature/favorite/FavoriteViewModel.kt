package com.gyleedev.githubsearch.feature.favorite

import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.gyleedev.githubsearch.core.common.BaseViewModel
import com.gyleedev.githubsearch.domain.model.FilterStatus
import com.gyleedev.githubsearch.domain.model.UserModel
import com.gyleedev.githubsearch.domain.usecase.GetFavoritesUseCase
import com.gyleedev.githubsearch.domain.usecase.UpdateFavoriteStatusUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
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
    private val _filterState = MutableStateFlow(FilterStatus.ALL)
    val filterState: StateFlow<FilterStatus> = _filterState

    @OptIn(ExperimentalCoroutinesApi::class)
    val items =
        _filterState
            .flatMapLatest {
                getFavoritesUseCase(it)
            }.cachedIn(viewModelScope)

    fun updateFilter(status: FilterStatus) {
        viewModelScope.launch {
            _filterState.emit(status)
        }
    }

    fun updateFavoriteStatus(user: UserModel) {
        viewModelScope.launch {
            updateFavoriteUseCase(user)
        }
    }
}
