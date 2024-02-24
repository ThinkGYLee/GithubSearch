package com.gyleedev.githubsearch.ui.home

import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.gyleedev.githubsearch.core.BaseViewModel
import com.gyleedev.githubsearch.domain.model.UserModel
import com.gyleedev.githubsearch.domain.usecase.GetUserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val useCase: GetUserUseCase
) : BaseViewModel() {
    private val _searchId = MutableStateFlow("")

    private val _userInfo = MutableStateFlow<UserModel?>(null)
    val userInfo: StateFlow<UserModel?> = _userInfo
    fun getUsers(): Flow<PagingData<UserModel>> = useCase.getUsers().cachedIn(viewModelScope)
    fun getUser() {
        viewModelScope.launch {
            _userInfo.emit(useCase.getUserAtHome(_searchId.value))
        }
    }

    fun updateSearchId(id: String) {
        viewModelScope.launch {
            _searchId.emit(id)
        }
    }

    fun resetUser() {
        viewModelScope.launch {
            _userInfo.emit(null)
        }
    }
}