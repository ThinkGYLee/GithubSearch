package com.gyleedev.githubsearch.ui.setting

import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.lifecycle.viewModelScope
import com.gyleedev.githubsearch.R
import com.gyleedev.githubsearch.core.BaseViewModel
import com.gyleedev.githubsearch.ui.setting.model.Language
import com.gyleedev.githubsearch.ui.setting.model.SettingDialogEnum
import com.gyleedev.githubsearch.ui.setting.model.SettingFeed
import com.gyleedev.githubsearch.ui.setting.model.Theme
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingViewModel @Inject constructor() : BaseViewModel() {

    private val _items = MutableStateFlow<List<SettingFeed>>(emptyList())
    val item: StateFlow<List<SettingFeed>> = _items

    private val _currentLocale = MutableStateFlow<LocaleListCompat?>(null)
    val currentLocale: StateFlow<LocaleListCompat?> = _currentLocale

    private val _currentTheme = MutableStateFlow(0)
    val currentTheme: StateFlow<Int> = _currentTheme

    private val _clickedEnum = MutableStateFlow<SettingDialogEnum?>(null)
    val clickedEnum: StateFlow<SettingDialogEnum?> = _clickedEnum

    private val _showThemeDialog = MutableStateFlow(false)
    val showThemeDialog: StateFlow<Boolean> = _showThemeDialog

    private val _showLanguageDialog = MutableStateFlow(false)
    val showLanguageDialog: StateFlow<Boolean> = _showLanguageDialog

    private val _showResetDialog = MutableStateFlow(false)
    val showResetDialog: StateFlow<Boolean> = _showResetDialog

    private val _updateTheme = MutableSharedFlow<Theme>()
    val updateTheme: SharedFlow<Theme> = _updateTheme

    private val _updateLanguage = MutableSharedFlow<Language>()
    val updateLanguage: SharedFlow<Language> = _updateLanguage

    init {
        // 글을 쓰고 되돌아 왔을때 뷰모델이 안죽었으면 갱신이 안된다.
        viewModelScope.launch {
            refresh()
            setList()
        }
    }

    fun refresh() {
        getTheme()
        getLocale()
    }

    fun getTheme() {
        viewModelScope.launch {
            _currentTheme.emit(AppCompatDelegate.getDefaultNightMode())
        }
    }

    fun getLocale() {
        viewModelScope.launch {
            _currentLocale.value = AppCompatDelegate.getApplicationLocales()
        }
    }

    fun changeThemeDialogState() {
        viewModelScope.launch {
            _showThemeDialog.emit(!_showThemeDialog.value)
        }
    }

    fun changeLanguageDialogState() {
        viewModelScope.launch {
            _showLanguageDialog.emit(!_showLanguageDialog.value)
        }
    }

    fun changeResetDialogState() {
        viewModelScope.launch {
            _showResetDialog.emit(!_showResetDialog.value)
        }
    }

    fun updateTheme(theme: Theme) {
        viewModelScope.launch {
            _updateTheme.emit(theme)
        }
    }

    fun updateLanguage(language: Language) {
        viewModelScope.launch {
            _updateLanguage.emit(language)
        }
    }

    fun notifyClick(enum: SettingDialogEnum) {
        viewModelScope.launch {
            _clickedEnum.emit(enum)
        }
    }

    fun resetEnum() {
        viewModelScope.launch {
            _clickedEnum.emit(null)
        }
    }

    fun resetData() {
        viewModelScope.launch {

        }
    }

    private suspend fun setList() {
        val itemsList = mutableListOf(
            SettingFeed.Title(R.string.setting_title),
            SettingFeed.Dialog(R.string.setting_theme, SettingDialogEnum.THEME),
            SettingFeed.Dialog(R.string.setting_language, SettingDialogEnum.LANGUAGE),
            SettingFeed.Dialog(R.string.setting_reset, SettingDialogEnum.RESET),
            SettingFeed.Title(R.string.setting_information),
            SettingFeed.Version(R.string.setting_version),
            SettingFeed.Dialog(R.string.setting_term, SettingDialogEnum.TERM)
        )
        _items.emit(itemsList)
    }
}