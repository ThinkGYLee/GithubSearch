package com.gyleedev.githubsearch.ui.setting


import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Language
import androidx.compose.material.icons.outlined.Storage
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.gyleedev.githubsearch.BuildConfig
import com.gyleedev.githubsearch.R
import com.gyleedev.githubsearch.ui.setting.model.Language
import com.gyleedev.githubsearch.ui.setting.model.Theme


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingScreen(
    modifier: Modifier = Modifier,
    viewModel: SettingViewModel = hiltViewModel(),
) {

    val currentTheme by viewModel.currentTheme.collectAsStateWithLifecycle()
    val currentLocale by viewModel.currentLocale.collectAsStateWithLifecycle()

    val showLanguageDialog by viewModel.showLanguageDialog.collectAsStateWithLifecycle()
    val showThemeDialog by viewModel.showThemeDialog.collectAsStateWithLifecycle()
    val showResetDialog by viewModel.showResetDialog.collectAsStateWithLifecycle()

    val version = BuildConfig.VERSION_NAME

    val themeList = listOf(
        stringResource(id = R.string.filter_default_theme),
        stringResource(id = R.string.filter_light_theme),
        stringResource(id = R.string.filter_dark_theme),
    )

    val languageList = listOf(
        stringResource(id = R.string.setting_korean),
        stringResource(id = R.string.setting_english)
    )

    LaunchedEffect(Unit) {
        viewModel.updateLanguage.collect { language ->
            println("language, $language")
            when (language) {
                Language.KOREAN -> {
                    val enLocale: LocaleListCompat =
                        LocaleListCompat.forLanguageTags("ko-KR")
                    AppCompatDelegate.setApplicationLocales(enLocale)
                }

                Language.ENGLISH -> {
                    val enLocale: LocaleListCompat =
                        LocaleListCompat.forLanguageTags("en-US")
                    AppCompatDelegate.setApplicationLocales(enLocale)
                }
            }
            viewModel.refresh()
        }
    }

    LaunchedEffect(Unit) {
        viewModel.updateTheme.collect { theme ->
            println("theme, $theme")
            when (theme) {
                Theme.DEFAULT -> {
                    println("default")
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)//디폴트
                }

                Theme.LIGHT -> {
                    println("light")
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)//라이트모드
                }

                Theme.DARK -> {
                    println("dark")
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)//다크모드
                }
            }
            viewModel.refresh()
        }
    }


    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Setting") },
                modifier = Modifier,
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
                modifier = Modifier.fillMaxWidth()
            ) {
                SettingRow(
                    leadingIcon = Icons.Outlined.DarkMode,
                    text = stringResource(R.string.setting_theme),
                    onClick = viewModel::changeThemeDialogState
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                        contentDescription = null,
                    )
                }

                SettingRow(
                    leadingIcon = Icons.Outlined.Language,
                    text = stringResource(R.string.setting_language),
                    onClick = viewModel::changeLanguageDialogState
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                        contentDescription = null,
                    )
                }
                SettingRow(
                    leadingIcon = Icons.Outlined.Storage,
                    text = stringResource(R.string.setting_reset),
                    onClick = viewModel::changeResetDialogState
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                        contentDescription = null,
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

            Card(modifier = Modifier.fillMaxWidth()) {
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
                        contentDescription = null,
                    )
                }
            }
        }

        if (showThemeDialog) {
            AlertDialog(
                selectedItemId = when (currentTheme) {
                    -1 -> 0 // 디폴트
                    1 -> 1 // 라이트모드
                    else -> 2 //다크모드
                },
                onChangeState = { viewModel.changeThemeDialogState() },
                onDialogResultEmit = {
                    val theme = when (it) {
                        themeList[0] -> Theme.DEFAULT
                        themeList[1] -> Theme.LIGHT
                        else -> Theme.DARK
                    }
                    viewModel.updateTheme(theme)
                },
                list = themeList
            )
        }

        if (showLanguageDialog) {
            AlertDialog(
                selectedItemId = when (currentLocale.toString()) {
                    "[ko_KR]" -> {
                        0
                    }

                    else -> {
                        1
                    }
                },
                onChangeState = { viewModel.changeLanguageDialogState() },
                onDialogResultEmit = {
                    val language = when (it) {
                        languageList[0] -> Language.KOREAN
                        else -> Language.ENGLISH
                    }
                    viewModel.updateLanguage(language)
                },
                list = languageList
            )
        }

        if (showResetDialog) {
            androidx.compose.material3.AlertDialog(
                onDismissRequest = { viewModel.changeResetDialogState() },
                title = { Text(text = stringResource(id = R.string.setting_reset)) },
                text = { Text(text = stringResource(id = R.string.dialog_reset)) },
                confirmButton = {
                    Button(
                        onClick = {
                            viewModel.changeResetDialogState()
                            viewModel.resetData()
                        }) {
                        Text(stringResource(id = R.string.reset_yes))
                    }
                },
                dismissButton = {
                    Button(
                        onClick = {
                            viewModel.changeResetDialogState()
                        }) {
                        Text(stringResource(id = R.string.reset_no))
                    }
                }
            )
        }
    }
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
                contentDescription = null,
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(text = text)
        }
        trailingContent()
    }
}

@Composable
fun AlertDialog(
    selectedItemId: Int,
    onChangeState: () -> Unit,
    onDialogResultEmit: (String) -> Unit,
    list: List<String>
) {
    val selectedItem = remember {
        mutableStateOf(list[selectedItemId])
    }


    androidx.compose.material3.AlertDialog(
        onDismissRequest = { onChangeState() },
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
                    selectedItemId = selectedItemId,
                    RadioItems(list),
                    selectedItem = { string ->
                        list.indexOf(string).also {
                            selectedItem.value = string
                        }
                    })
            }
        },
        dismissButton = {
            TextButton(onClick = {
                onChangeState()
            }) {
                Text(text = stringResource(id = R.string.text_filter_cancel))
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onDialogResultEmit(selectedItem.value)
                    onChangeState()
                }) {
                Text(text = stringResource(id = R.string.text_filter_confirm))
            }
        }
    )
}

@Composable
fun RadioButtons(
    selectedItemId: Int,
    items: RadioItems,
    selectedItem: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val selectedId = remember {
        mutableIntStateOf(selectedItemId)
    }
    val selectedValue = remember { mutableStateOf("") }
    val isSelectedItem: (String) -> Boolean = {
        selectedValue.value == it
    }
    val onChangeState: (String) -> Unit = {
        selectedValue.value = it
        selectedItem(selectedValue.value)
    }

    Column(modifier = modifier.padding(top = 10.dp)) {
        val declaration = items.list
        declaration.forEach { item ->
            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 48.dp)
                        .selectable(
                            selected = isSelectedItem(item),
                            onClick = {
                                onChangeState(item)
                                selectedId.intValue = declaration.indexOf(item)
                            },
                            role = Role.RadioButton
                        )
                        .padding(bottom = 3.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = declaration[selectedId.intValue] == item,
                        onClick = null,
                        modifier = Modifier.padding(end = 5.dp)
                    )
                    Text(text = item, style = MaterialTheme.typography.labelMedium)
                }
            }
        }
    }
}


data class RadioItems(
    val list: List<String>
)


