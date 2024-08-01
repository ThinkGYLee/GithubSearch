package com.gyleedev.githubsearch.ui.setting

import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.automirrored.outlined.Help
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Language
import androidx.compose.material.icons.outlined.Storage
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.os.LocaleListCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.gyleedev.githubsearch.BuildConfig
import com.gyleedev.githubsearch.R
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingScreen(
    modifier: Modifier = Modifier,
    requestAuthentication: () -> Unit,
    viewModel: SettingViewModel = hiltViewModel()
) {
    val isDark = isSystemInDarkTheme()
    val version = BuildConfig.VERSION_NAME

    val themeData = SettingDialogItem.Theme(themeList)
    val languageData = SettingDialogItem.Language(languageList)

    val showLanguageDialog = remember { mutableStateOf(false) }
    val showThemeDialog = remember { mutableStateOf(false) }
    val showResetDialog = remember { mutableStateOf(false) }
    val showLoginDialog = remember { mutableStateOf(false) }
    val showLogoutDialog = remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(id = R.string.title_setting)) },
                modifier = Modifier
            )
        },
        modifier = modifier.fillMaxSize()
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .padding(paddingValues)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .height(48.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.setting_title),
                    style = MaterialTheme.typography.titleMedium
                )
            }

            Card(
                colors = if (isDark) {
                    CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainer
                    )
                } else {
                    CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainerLowest
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.elevatedCardElevation()
            ) {
                SettingRow(
                    leadingIcon = Icons.Outlined.DarkMode,
                    text = stringResource(R.string.setting_theme),
                    onClick = { showThemeDialog.value = true }
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                        contentDescription = null
                    )
                }

                SettingRow(
                    leadingIcon = Icons.Outlined.Language,
                    text = stringResource(R.string.setting_language),
                    onClick = { showLanguageDialog.value = true }
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                        contentDescription = null
                    )
                }

                SettingRow(
                    leadingIcon = Icons.Outlined.AccountCircle,
                    text = stringResource(R.string.setting_login),
                    onClick = {
                        if (viewModel.isKeyExists()) {
                            showLogoutDialog.value = true
                        } else {
                            showLoginDialog.value = true
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                        contentDescription = null
                    )
                }

                SettingRow(
                    leadingIcon = Icons.Outlined.Storage,
                    text = stringResource(R.string.setting_reset),
                    onClick = { showResetDialog.value = true }
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                        contentDescription = null
                    )
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .height(48.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.setting_information),
                    style = MaterialTheme.typography.titleMedium
                )
            }

            Card(
                colors = if (isDark) {
                    CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainer
                    )
                } else {
                    CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainerLowest
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.elevatedCardElevation()
            ) {
                SettingRow(
                    leadingIcon = Icons.AutoMirrored.Outlined.Help,
                    text = stringResource(R.string.setting_version),
                    onClick = {}
                ) {
                    Text(
                        text = version,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                SettingRow(
                    leadingIcon = Icons.Outlined.Description,
                    text = stringResource(R.string.setting_term),
                    onClick = {}
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                        contentDescription = null
                    )
                }
            }
        }

        if (showLanguageDialog.value) {
            RadioButtonDialog(
                onDismissRequest = { showLanguageDialog.value = false },
                onEventRequest = { index ->
                    AppCompatDelegate.setApplicationLocales(languageData.content[index].type)
                    showLanguageDialog.value = false
                },
                items = languageData,
                modifier = Modifier
            )
        }

        if (showThemeDialog.value) {
            RadioButtonDialog(
                onDismissRequest = { showThemeDialog.value = false },
                onEventRequest = { index ->
                    AppCompatDelegate.setDefaultNightMode(themeData.content[index].type)
                    showThemeDialog.value = false
                },
                items = themeData,
                modifier = Modifier
            )
        }

        if (showResetDialog.value) {
            TwoButtonDialog(
                onDismissRequest = { showResetDialog.value = false },
                onEventRequest = { viewModel.resetData() },
                modifier = Modifier,
                type = null
            )
        }

        if (showLoginDialog.value) {
            TwoButtonDialog(
                onDismissRequest = { showLoginDialog.value = false },
                onEventRequest = { requestAuthentication() },
                modifier = Modifier,
                type = false
            )
        }

        if (showLogoutDialog.value) {
            TwoButtonDialog(
                onDismissRequest = { showLogoutDialog.value = false },
                onEventRequest = { viewModel.deleteKey() },
                modifier = Modifier,
                type = true
            )
        }
    }
}

@Composable
private fun TwoButtonDialog(
    onDismissRequest: () -> Unit,
    onEventRequest: () -> Unit,
    modifier: Modifier,
    type: Boolean?
) {
    val titleResource: Int
    val contentResource: Int

    when (type) {
        true -> {
            titleResource = R.string.dialog_log_out_title
            contentResource = R.string.dialog_log_out_content
        }

        false -> {
            titleResource = R.string.dialog_log_in_title
            contentResource = R.string.dialog_log_in_content
        }

        null -> {
            titleResource = R.string.dialog_reset_title
            contentResource = R.string.dialog_reset_content
        }
    }

    AlertDialog(
        onDismissRequest = { onDismissRequest() },
        title = { Text(text = stringResource(id = titleResource)) },
        text = { Text(text = stringResource(id = contentResource)) },
        confirmButton = {
            Button(
                onClick = {
                    onDismissRequest()
                    onEventRequest()
                }
            ) {
                Text(stringResource(id = R.string.dialog_answer_yes))
            }
        },
        dismissButton = {
            Button(
                onClick = {
                    onDismissRequest()
                }
            ) {
                Text(stringResource(id = R.string.dialog_answer_no))
            }
        },
        modifier = modifier
    )
}

@Composable
private fun SettingRow(
    leadingIcon: ImageVector,
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    trailingContent: @Composable () -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 56.dp)
            .clickable(onClick = onClick, role = Role.Button)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row {
            Icon(
                imageVector = leadingIcon,
                contentDescription = null
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(text = text)
        }
        trailingContent()
    }
}

/*
아이디어 생각
컨피규레이션 체인지가 안 일어 난다면 뭔지 바뀔일은 없다.
최상위에 현재 스테이트를 쓰고 내린다.
 */
@Composable
private fun RadioButtonDialog(
    onDismissRequest: () -> Unit,
    onEventRequest: (Int) -> Unit,
    items: SettingDialogItem,
    modifier: Modifier
) {
    val stringResourceList: List<Int>

    val defaultIndex = when (items) {
        is SettingDialogItem.Theme -> {
            val data = items.content
            stringResourceList = data.map { it.content }
            data.indexOf(
                data.find {
                    it.type == AppCompatDelegate.getDefaultNightMode()
                }
            )
        }

        is SettingDialogItem.Language -> {
            val data = items.content
            stringResourceList = data.map { it.content }
            data.indexOf(
                data.find {
                    it.type == AppCompatDelegate.getApplicationLocales()
                }
            )
        }
    }

    val selectedIndex = remember {
        mutableIntStateOf(defaultIndex)
    }

    AlertDialog(
        title = {
            Text(
                text = stringResource(id = R.string.text_filter_title),
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        },
        text = {
            Column {
                Text(
                    text = stringResource(id = R.string.text_filter_content),
                    modifier = Modifier.padding(bottom = 5.dp),
                    style = MaterialTheme.typography.titleMedium
                )
                RadioButtons(
                    selectedIndex = selectedIndex.intValue,
                    stringResourceList,
                    onIndexChange = { selectedIndex.intValue = it }
                )
            }
        },
        onDismissRequest = { onDismissRequest() },
        dismissButton = {
            TextButton(onClick = {
                onDismissRequest()
            }) {
                Text(text = stringResource(id = R.string.text_filter_cancel))
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onEventRequest(selectedIndex.intValue)
                    onDismissRequest()
                }
            ) {
                Text(text = stringResource(id = R.string.text_filter_confirm))
            }
        },
        modifier = modifier
    )
}

@Composable
fun RadioButtons(
    selectedIndex: Int,
    items: List<Int>,
    onIndexChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.padding(top = 10.dp)) {
        items.forEach { item ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 48.dp)
                    .selectable(
                        selected = selectedIndex == items.indexOf(item),
                        onClick = {
                            onIndexChange(items.indexOf(item))
                        },
                        role = Role.RadioButton
                    )
                    .padding(bottom = 3.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = selectedIndex == items.indexOf(item),
                    onClick = null,
                    modifier = Modifier.padding(end = 5.dp)
                )
                Text(text = stringResource(id = item), style = MaterialTheme.typography.labelMedium)
            }
        }
    }
}

sealed interface SettingDialogItem {
    data class Theme(
        val content: List<ThemeItem>
    ) : SettingDialogItem

    data class Language(
        val content: List<LanguageItem>
    ) : SettingDialogItem
}

data class ThemeItem(
    val type: Int,
    val content: Int
)

data class LanguageItem(
    val type: LocaleListCompat,
    val content: Int
)

val themeList = listOf(
    ThemeItem(
        AppCompatDelegate.MODE_NIGHT_YES,
        R.string.filter_dark_theme
    ),
    ThemeItem(
        AppCompatDelegate.MODE_NIGHT_NO,
        R.string.filter_light_theme
    ),
    ThemeItem(
        AppCompatDelegate.MODE_NIGHT_UNSPECIFIED,
        R.string.filter_default_theme
    )
)

val languageList = listOf(
    LanguageItem(
        LocaleListCompat.create(Locale.KOREA),
        R.string.setting_korean
    ),
    LanguageItem(
        LocaleListCompat.create(Locale.ENGLISH),
        R.string.setting_english
    )
)
