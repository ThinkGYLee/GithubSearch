package com.gyleedev.githubsearch.feature.setting

import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Help
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Language
import androidx.compose.material.icons.outlined.Storage
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.core.os.LocaleListCompat
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import com.gyleedev.githubsearch.domain.model.ResetDataResult
import com.gyleedev.githubsearch.domain.model.RevokeResult
import com.gyleedev.githubsearch.feature.setting.component.RadioButtonDialog
import com.gyleedev.githubsearch.feature.setting.component.SettingMainBlock
import com.gyleedev.githubsearch.feature.setting.component.TwoButtonDialog
import com.gyleedev.githubsearch.feature.setting.model.LanguageItem
import com.gyleedev.githubsearch.feature.setting.model.SettingDialogItem
import com.gyleedev.githubsearch.feature.setting.model.SettingEvent
import com.gyleedev.githubsearch.feature.setting.model.SettingItem
import com.gyleedev.githubsearch.feature.setting.model.SettingRowItem
import com.gyleedev.githubsearch.feature.setting.model.ThemeItem
import kotlinx.coroutines.flow.collectLatest
import java.util.Locale
import com.gyleedev.githubsearch.feature.setting.R as SettingR

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingScreen(
    modifier: Modifier = Modifier,
    requestAuthentication: () -> Unit,
    viewModel: SettingViewModel = hiltViewModel(),
) {
    val isDark = isSystemInDarkTheme()
    val themeData = remember { SettingDialogItem.Theme(themeList) }
    val languageData = remember { SettingDialogItem.Language(languageList) }
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val lifecycleOwner = LocalLifecycleOwner.current
    val snackBarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    val revokeSuccessMessage = stringResource(id = SettingR.string.revoke_success_message)
    val revokeFailMessage = stringResource(id = SettingR.string.revoke_fail_message)
    val revokeNoKeyMessage = stringResource(id = SettingR.string.revoke_no_item_message)
    val resetSuccessMessage = stringResource(id = SettingR.string.reset_success_message)
    val resetFailMessage = stringResource(id = SettingR.string.reset_fail_message)

    LaunchedEffect(viewModel.showRevokeResult, lifecycleOwner, context) {
        lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.showRevokeResult.collectLatest { result ->
                val message = when (result) {
                    RevokeResult.SUCCESS -> revokeSuccessMessage
                    RevokeResult.FAIL -> revokeFailMessage
                    RevokeResult.NO_KEY -> revokeNoKeyMessage
                }

                snackBarHostState.showSnackbar(
                    message = message,
                    duration = SnackbarDuration.Short,
                )
            }
        }
    }

    LaunchedEffect(viewModel.showResetResult, lifecycleOwner, context) {
        lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.showResetResult.collectLatest { result ->
                val message = when (result) {
                    ResetDataResult.Success -> resetSuccessMessage
                    ResetDataResult.Fail -> resetFailMessage
                }

                snackBarHostState.showSnackbar(
                    message = message,
                    duration = SnackbarDuration.Short,
                )
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(id = SettingR.string.title_setting)) },
                modifier = Modifier,
            )
        },
        snackbarHost = { SnackbarHost(snackBarHostState) },
        modifier = modifier.fillMaxSize(),
    ) { paddingValues ->

        if (uiState is SettingUiState.Success) {
            val state = uiState as SettingUiState.Success
            SettingMainBlock(
                items = settingList,
                isDark = isDark,
                onClick = viewModel::changDialogState,
                modifier = modifier
                    .fillMaxSize()
                    .padding(paddingValues),
            )

            if (state.showLanguageDialog) {
                RadioButtonDialog(
                    onDismissRequest = { viewModel.changDialogState(SettingEvent.LANGUAGE) },
                    onEventRequest = { index ->
                        AppCompatDelegate.setApplicationLocales(languageData.content[index].type)
                        viewModel.changDialogState(SettingEvent.LANGUAGE)
                    },
                    items = languageData,
                    modifier = Modifier,
                )
            }

            if (state.showThemeDialog) {
                RadioButtonDialog(
                    onDismissRequest = { viewModel.changDialogState(SettingEvent.THEME) },
                    onEventRequest = { index ->
                        AppCompatDelegate.setDefaultNightMode(themeData.content[index].type)
                        viewModel.changDialogState(SettingEvent.THEME)
                    },
                    items = themeData,
                    modifier = Modifier,
                )
            }

            if (state.showResetDialog) {
                TwoButtonDialog(
                    onDismissRequest = { viewModel.changDialogState(SettingEvent.RESET) },
                    onEventRequest = {
                        viewModel.resetData()
                        viewModel.changDialogState(SettingEvent.RESET)
                    },
                    modifier = Modifier,
                    type = null,
                )
            }

            if (state.showLoginDialog) {
                TwoButtonDialog(
                    onDismissRequest = { viewModel.changDialogState(SettingEvent.LOGIN) },
                    onEventRequest = {
                        requestAuthentication()
                        viewModel.changDialogState(SettingEvent.LOGIN)
                    },
                    modifier = Modifier,
                    type = false,
                )
            }

            if (state.showLogoutDialog) {
                TwoButtonDialog(
                    onDismissRequest = { viewModel.changDialogState(SettingEvent.LOGOUT) },
                    onEventRequest = {
                        viewModel.revokeApplication()
                        viewModel.changDialogState(SettingEvent.LOGOUT)
                    },
                    modifier = Modifier,
                    type = true,
                )
            }
        }
    }
}

val settingList = listOf(
    SettingItem.Title(
        text = SettingR.string.setting_title,
    ),
    SettingItem.Card(
        items = listOf(
            SettingRowItem.ClickableItem(
                icon = Icons.Outlined.DarkMode,
                content = SettingR.string.setting_theme,
                event = SettingEvent.THEME,
            ),
            SettingRowItem.ClickableItem(
                icon = Icons.Outlined.Language,
                content = SettingR.string.setting_language,
                event = SettingEvent.LANGUAGE,
            ),
            SettingRowItem.ClickableItem(
                icon = Icons.Outlined.AccountCircle,
                content = SettingR.string.setting_login,
                event = SettingEvent.INFORMATION,
            ),
            SettingRowItem.ClickableItem(
                icon = Icons.Outlined.Storage,
                content = SettingR.string.setting_reset,
                event = SettingEvent.RESET,
            ),
        ),
    ),
    SettingItem.Title(
        text = SettingR.string.setting_information,
    ),
    SettingItem.Card(
        items = listOf(
            SettingRowItem.TextItem(
                icon = Icons.AutoMirrored.Outlined.Help,
                content = SettingR.string.setting_version,
                event = SettingEvent.NONE,
                text = BuildConfig.VERSION_NAME,
            ),
            SettingRowItem.ClickableItem(
                icon = Icons.Outlined.Description,
                content = SettingR.string.setting_term,
                event = SettingEvent.POLICY,
            ),
        ),
    ),
)

val themeList =
    listOf(
        ThemeItem(
            AppCompatDelegate.MODE_NIGHT_YES,
            SettingR.string.filter_dark_theme,
        ),
        ThemeItem(
            AppCompatDelegate.MODE_NIGHT_NO,
            SettingR.string.filter_light_theme,
        ),
        ThemeItem(
            AppCompatDelegate.MODE_NIGHT_UNSPECIFIED,
            SettingR.string.filter_default_theme,
        ),
    )

val languageList =
    listOf(
        LanguageItem(
            LocaleListCompat.create(Locale.KOREA),
            SettingR.string.setting_korean,
        ),
        LanguageItem(
            LocaleListCompat.create(Locale.ENGLISH),
            SettingR.string.setting_english,
        ),
    )
