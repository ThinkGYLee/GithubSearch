package com.gyleedev.githubsearch.feature.setting.component

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.gyleedev.githubsearch.feature.setting.R as SettingR

@Composable
fun TwoButtonDialog(
    onDismissRequest: () -> Unit,
    onEventRequest: () -> Unit,
    type: Boolean?,
    modifier: Modifier = Modifier,
) {
    val titleResource: Int
    val contentResource: Int

    when (type) {
        true -> {
            titleResource = SettingR.string.dialog_log_out_title
            contentResource = SettingR.string.dialog_log_out_content
        }

        false -> {
            titleResource = SettingR.string.dialog_log_in_title
            contentResource = SettingR.string.dialog_log_in_content
        }

        null -> {
            titleResource = SettingR.string.dialog_reset_title
            contentResource = SettingR.string.dialog_reset_content
        }
    }

    AlertDialog(
        onDismissRequest = { onDismissRequest() },
        title = { Text(text = stringResource(id = titleResource)) },
        text = { Text(text = stringResource(id = contentResource)) },
        confirmButton = {
            TextButton(
                onClick = {
                    onDismissRequest()
                    onEventRequest()
                },
            ) {
                Text(
                    text = stringResource(id = SettingR.string.dialog_answer_yes),
                    color = MaterialTheme.colorScheme.primary,
                )
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onDismissRequest()
                },
            ) {
                Text(
                    text = stringResource(id = SettingR.string.dialog_answer_no),
                    color = MaterialTheme.colorScheme.error,
                )
            }
        },
        modifier = modifier,
    )
}
