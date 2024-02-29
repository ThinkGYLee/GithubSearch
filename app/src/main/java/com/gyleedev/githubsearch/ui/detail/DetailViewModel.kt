package com.gyleedev.githubsearch.ui.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.gyleedev.githubsearch.core.BaseViewModel
import com.gyleedev.githubsearch.domain.model.DetailFeed
import com.gyleedev.githubsearch.domain.usecase.DetailGetFeedUseCase
import com.gyleedev.githubsearch.domain.usecase.DetailUpdateFavoriteStatusUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val detailGetFeedUseCase: DetailGetFeedUseCase,
    private val detailUpdateFavoriteStatusUseCase: DetailUpdateFavoriteStatusUseCase,
    savedStateHandle: SavedStateHandle
) : BaseViewModel() {

    private val _itemList = MutableStateFlow<List<DetailFeed>>(emptyList())
    val itemList: StateFlow<List<DetailFeed>> = _itemList

    private val _favoriteStatus = MutableStateFlow(false)
    val favoriteStatus: StateFlow<Boolean> = _favoriteStatus

    init {
        val id = savedStateHandle.get<String>("id")
        viewModelScope.launch {
            if (id != null) {
                getItems(id)
            }
        }
    }

    private fun getItems(user: String) {
        viewModelScope.launch {
            _itemList.emit(detailGetFeedUseCase(user))
            setInitialFavoriteStatus(_itemList.value)
        }
    }

    private fun setInitialFavoriteStatus(list: List<DetailFeed>) {
        val user = if (list[0] is DetailFeed.UserProfile) {
            list[0] as DetailFeed.UserProfile
        } else {
            null
        }
        viewModelScope.launch {
            if (user != null) {
                _favoriteStatus.emit(user.userModel.favorite)
            }
        }
    }

    fun updateFavoriteStatus() {
        viewModelScope.launch {
            if (itemList.value.isNotEmpty() && itemList.value[0] is DetailFeed.UserProfile) {
                val userProfile = itemList.value[0] as DetailFeed.UserProfile
                _itemList.emit(detailUpdateFavoriteStatusUseCase(userProfile.userModel.login))
                _favoriteStatus.emit(!_favoriteStatus.value)
            }
        }
    }

}