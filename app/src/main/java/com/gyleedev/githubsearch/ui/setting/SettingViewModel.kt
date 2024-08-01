package com.gyleedev.githubsearch.ui.setting

import androidx.lifecycle.viewModelScope
import com.gyleedev.githubsearch.core.BaseViewModel
import com.gyleedev.githubsearch.domain.usecase.ResetDataUseCase
import com.gyleedev.githubsearch.domain.usecase.RevokeApplicationUseCase
import com.gyleedev.githubsearch.util.PreferenceUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingViewModel @Inject constructor(
    private val resetDataUseCase: ResetDataUseCase,
    private val revokeApplicationUseCase: RevokeApplicationUseCase,
    private val preferenceUtil: PreferenceUtil
) : BaseViewModel() {

    fun resetData() {
        viewModelScope.launch {
            resetDataUseCase()
        }
    }

    fun isKeyExists(): Boolean {
        return preferenceUtil.isKeyExist()
    }

    fun deleteKey() {
        viewModelScope.launch {
            revokeApplicationUseCase()
            preferenceUtil.deleteKey()
        }
    }
}
