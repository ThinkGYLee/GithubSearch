package com.gyleedev.githubsearch.ui

import androidx.lifecycle.viewModelScope
import com.gyleedev.githubsearch.BuildConfig
import com.gyleedev.githubsearch.core.BaseViewModel
import com.gyleedev.githubsearch.data.repository.GitHubRepository
import com.gyleedev.githubsearch.util.PreferenceUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: GitHubRepository,
    private val preferenceUtil: PreferenceUtil
) : BaseViewModel() {

    private val _alertLoginSuccess = MutableSharedFlow<Boolean>()
    val alertLoginSuccess: SharedFlow<Boolean> = _alertLoginSuccess

    fun getAccessToken(code: String) {
        viewModelScope.launch {
            val response = repository.getAccessToken(
                id = BuildConfig.GIT_ID,
                secret = BuildConfig.GIT_SECRET,
                code = code
            )

            if (response.isSuccessful && response.code() == 200) {
                response.body().let {
                    if (it != null) {
                        preferenceUtil.setString(str = it.accessToken)
                        _alertLoginSuccess.emit(true)
                    }
                }
            } else {
                _alertLoginSuccess.emit(false)
            }
        }
    }
}