package com.gyleedev.githubsearch.ui.favorite

import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.gyleedev.githubsearch.core.BaseViewModel
import com.gyleedev.githubsearch.domain.model.UserModel
import com.gyleedev.githubsearch.domain.usecase.FavoriteUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoriteViewModel @Inject constructor(
    private val useCase: FavoriteUseCase
) : BaseViewModel() {

    fun getUsers(): Flow<PagingData<UserModel>> = useCase.getFavorites().cachedIn(viewModelScope)

    fun updateFavoriteStatus(user: UserModel) {
        viewModelScope.launch {
            useCase.update(user.login)
        }
    }
}