package com.gyleedev.githubsearch.ui.setting


import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.automirrored.outlined.Help
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Backup
import androidx.compose.material.icons.outlined.ConnectingAirports
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.Feedback
import androidx.compose.material.icons.outlined.FolderOpen
import androidx.compose.material.icons.outlined.Language
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material.icons.outlined.Payment
import androidx.compose.material.icons.outlined.SafetyCheck
import androidx.compose.material.icons.outlined.Security
import androidx.compose.material.icons.outlined.Storage
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingScreen(

    modifier: Modifier = Modifier,

    ) {

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Setting") },
                actions = {
                },
                modifier = Modifier,
            )

        },
        modifier = modifier
            .fillMaxSize()
    ) { paddingValues ->
        var mode by rememberSaveable {
            mutableStateOf(false)
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .padding(paddingValues)
        ) {

            list.forEach {
                when (it.type) {
                    SettingItemEnum.HEADER -> {
                        item {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp)
                                    .height(48.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = it.value,
                                    style = MaterialTheme.typography.titleLarge
                                )
                            }
                        }

                    }

                    SettingItemEnum.CONTENT -> {
                        item {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .heightIn(min = 48.dp)
                                    .background(MaterialTheme.colorScheme.surfaceContainer)
                                    .clickable { }
                                    .padding(vertical = 16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                it.iconImage?.let { it1 ->
                                    Icon(
                                        imageVector = it1,
                                        contentDescription = it.value,
                                        modifier = Modifier.padding(start = 16.dp)
                                    )
                                }
                                Text(
                                    text = it.value,
                                    style = MaterialTheme.typography.titleLarge,
                                    modifier = Modifier.padding(horizontal = 16.dp)
                                )
                                Spacer(modifier = Modifier.weight(1f))
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                                    contentDescription = null,
                                    modifier = Modifier
                                        .padding(end = 8.dp)
                                )
                            }
                        }

                    }

                    else -> {
                        item {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .heightIn(min = 48.dp)
                                    .background(MaterialTheme.colorScheme.surfaceContainer)
                                    .clickable { }
                                    .padding(vertical = 8.dp)
                            ) {
                                it.iconImage?.let { it1 ->
                                    Icon(
                                        imageVector = it1,
                                        contentDescription = it.value,
                                        modifier = Modifier.padding(start = 16.dp)
                                    )
                                }
                                Text(
                                    text = it.value,
                                    style = MaterialTheme.typography.titleLarge,
                                    modifier = Modifier.padding(horizontal = 16.dp)
                                )
                                Spacer(modifier = Modifier.weight(1f))
                                Switch(
                                    checked = mode,
                                    onCheckedChange = { mode = !mode },
                                    modifier = Modifier.padding(end = 16.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

enum class SettingItemEnum {
    HEADER,
    CONTENT,
    THEME
}


data class SettingItem(
    val type: SettingItemEnum,
    val value: String,
    val iconImage: ImageVector?
)

val list = listOf(
    SettingItem(
        SettingItemEnum.HEADER,
        "Account",
        null
    ),
    SettingItem(
        SettingItemEnum.CONTENT,
        "Profile&Accounts",
        Icons.Outlined.AccountCircle
    ),
    SettingItem(
        SettingItemEnum.CONTENT,
        "Security",
        Icons.Outlined.Security
    ),
    SettingItem(
        SettingItemEnum.CONTENT,
        "Privacy&Security",
        Icons.Outlined.Lock
    ),
    SettingItem(
        SettingItemEnum.CONTENT,
        "Billing&Subscription",
        Icons.Outlined.Payment
    ),
    SettingItem(
        SettingItemEnum.HEADER,
        "Personalization",
        null
    ),
    SettingItem(
        SettingItemEnum.THEME,
        "Dark Mode",
        Icons.Outlined.DarkMode
    ),
    SettingItem(
        SettingItemEnum.CONTENT,
        "Appearance",
        Icons.Outlined.Palette
    ),
    SettingItem(
        SettingItemEnum.CONTENT,
        "Language",
        Icons.Outlined.Language
    ),
    SettingItem(
        SettingItemEnum.CONTENT,
        "Themes Organize",
        Icons.Outlined.Description
    ),
    SettingItem(
        SettingItemEnum.HEADER,
        "Data&Storage",
        null
    ),
    SettingItem(
        SettingItemEnum.CONTENT,
        "Data&Storage",
        Icons.Outlined.Storage
    ),
    SettingItem(
        SettingItemEnum.CONTENT,
        "Backup&Restore",
        Icons.Outlined.Backup
    ),
    SettingItem(
        SettingItemEnum.CONTENT,
        "Connections",
        Icons.Outlined.ConnectingAirports
    ),
    SettingItem(
        SettingItemEnum.CONTENT,
        "ManageContent",
        Icons.Outlined.FolderOpen
    ),
    SettingItem(
        SettingItemEnum.HEADER,
        "Accessibility",
        null
    ),
    SettingItem(
        SettingItemEnum.CONTENT,
        "Help&Feedback",
        Icons.Outlined.Feedback
    ),
    SettingItem(
        SettingItemEnum.CONTENT,
        "Permissions",
        Icons.Outlined.SafetyCheck
    ),
    SettingItem(
        SettingItemEnum.CONTENT,
        "About",
        Icons.AutoMirrored.Outlined.Help
    ),
    SettingItem(
        SettingItemEnum.CONTENT,
        "Terms&Service",
        Icons.Outlined.Description
    ),
    SettingItem(
        SettingItemEnum.CONTENT,
        "Support us",
        Icons.Outlined.Favorite
    ),
)


