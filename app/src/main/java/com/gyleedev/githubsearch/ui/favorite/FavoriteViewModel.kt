package com.gyleedev.githubsearch.ui.favorite

import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.gyleedev.githubsearch.core.BaseViewModel
import com.gyleedev.githubsearch.domain.model.FilterStatus
import com.gyleedev.githubsearch.domain.model.UserModel
import com.gyleedev.githubsearch.domain.usecase.FavoriteGetFavoritesUseCase
import com.gyleedev.githubsearch.domain.usecase.FavoriteUpdateFavoriteStatusUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoriteViewModel @Inject constructor(
    private val updateFavoriteUseCase: FavoriteUpdateFavoriteStatusUseCase,
    private val favoriteGetFavoritesUseCase: FavoriteGetFavoritesUseCase
) : BaseViewModel() {

    private val filterState = MutableStateFlow(FilterStatus.ALL)

    @OptIn(ExperimentalCoroutinesApi::class)
    val items = filterState
        .flatMapLatest {
            favoriteGetFavoritesUseCase(it)
        }.cachedIn(viewModelScope)

    fun userFilterAll() {
        viewModelScope.launch {
            filterState.emit(FilterStatus.ALL)
        }
    }

    fun userFilterHasRepos() {
        viewModelScope.launch {
            filterState.emit(FilterStatus.REPO)
        }
    }

    fun userFilterNoRepos() {
        viewModelScope.launch {
            filterState.emit(FilterStatus.NOREPO)
        }
    }

    fun updateFavoriteStatus(user: UserModel) {
        viewModelScope.launch {
            updateFavoriteUseCase(user.login)
        }
    }
}
