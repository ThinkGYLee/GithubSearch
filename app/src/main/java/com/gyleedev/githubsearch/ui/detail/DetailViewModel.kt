package com.gyleedev.githubsearch.ui.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.gyleedev.githubsearch.core.BaseViewModel
import com.gyleedev.githubsearch.domain.model.DetailFeed
import com.gyleedev.githubsearch.domain.usecase.DetailFeedUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val useCase: DetailFeedUseCase,
    savedStateHandle: SavedStateHandle
): BaseViewModel() {

    private val _itemList = MutableStateFlow<List<DetailFeed>>(emptyList())
    val itemList: StateFlow<List<DetailFeed>> = _itemList

    private val _linkEvent = MutableSharedFlow<String>()
    val linkEvent: SharedFlow<String> = _linkEvent

    init {
        val id = savedStateHandle.get<String>("id")
        viewModelScope.launch {
            if (id != null) {
                println("id $id")
                getItems(id)
            }
        }
    }
    fun getItems(user: String) {
        viewModelScope.launch {
            _itemList.emit(useCase.invoke(user))
        }
    }

    fun linkClicked(link: String) {
        viewModelScope.launch {
            _linkEvent.emit(link)
        }
    }

}