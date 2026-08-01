package com.gyleedev.githubsearch.feature.setting.component

import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.gyleedev.githubsearch.core.designsystem.R as DesignSystemR
import com.gyleedev.githubsearch.feature.setting.model.SettingDialogItem

@Composable
fun RadioButtonDialog(
    onDismissRequest: () -> Unit,
    onEventRequest: (Int) -> Unit,
    items: SettingDialogItem,
    modifier: Modifier,
) {
    val stringResourceList: List<Int>

    val defaultIndex =
        when (items) {
            is SettingDialogItem.Theme -> {
                val data = items.content
                stringResourceList = data.map { it.content }
                data.indexOf(
                    data.find {
                        it.type == AppCompatDelegate.getDefaultNightMode()
                    },
                )
            }

            is SettingDialogItem.Language -> {
                val data = items.content
                stringResourceList = data.map { it.content }
                data.indexOf(
                    data.find {
                        it.type == AppCompatDelegate.getApplicationLocales()
                    },
                )
            }
        }

    val selectedIndex =
        remember {
            mutableIntStateOf(defaultIndex)
        }

    AlertDialog(
        title = {
            Text(
                text = stringResource(id = DesignSystemR.string.text_filter_title),
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
            )
        },
        text = {
            Column {
                Text(
                    text = stringResource(id = DesignSystemR.string.text_filter_content),
                    modifier = Modifier.padding(bottom = 5.dp),
                    style = MaterialTheme.typography.titleMedium,
                )
                RadioButtons(
                    selectedIndex = selectedIndex.intValue,
                    stringResourceList,
                    onIndexChange = { selectedIndex.intValue = it },
                )
            }
        },
        onDismissRequest = { onDismissRequest() },
        dismissButton = {
            TextButton(onClick = {
                onDismissRequest()
            }) {
                Text(text = stringResource(id = DesignSystemR.string.text_filter_cancel))
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onEventRequest(selectedIndex.intValue)
                },
            ) {
                Text(text = stringResource(id = DesignSystemR.string.text_filter_confirm))
            }
        },
        modifier = modifier,
    )
}

@Composable
fun RadioButtons(
    selectedIndex: Int,
    items: List<Int>,
    onIndexChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.padding(top = 10.dp)) {
        items.forEach { item ->
            Row(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .heightIn(min = 48.dp)
                        .selectable(
                            selected = selectedIndex == items.indexOf(item),
                            onClick = {
                                onIndexChange(items.indexOf(item))
                            },
                            role = Role.RadioButton,
                        ).padding(bottom = 3.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                RadioButton(
                    selected = selectedIndex == items.indexOf(item),
                    onClick = null,
                    modifier = Modifier.padding(end = 5.dp),
                )
                Text(text = stringResource(id = item), style = MaterialTheme.typography.labelMedium)
            }
        }
    }
}
