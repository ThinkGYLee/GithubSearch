package com.gyleedev.githubsearch.feature.setting.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEach
import com.gyleedev.githubsearch.core.designsystem.component.LiquidNavBarDefaults
import com.gyleedev.githubsearch.feature.setting.model.SettingEvent
import com.gyleedev.githubsearch.feature.setting.model.SettingItem

@Composable
fun SettingMainBlock(
    items: List<SettingItem>,
    isDark: Boolean,
    onClick: (SettingEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
            modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
    ) {
        items.fastForEach { item ->
            when (item) {
                is SettingItem.Title -> {
                    SettingTitle(item)
                }

                is SettingItem.Card -> {
                    SettingCard(
                        isDark = isDark,
                        items = item,
                        onClick = onClick,
                    )
                }
            }
        }

        Spacer(
            modifier =
                Modifier
                    .navigationBarsPadding()
                    .height(LiquidNavBarDefaults.Height + LiquidNavBarDefaults.BottomMargin + 16.dp),
        )
    }
}
