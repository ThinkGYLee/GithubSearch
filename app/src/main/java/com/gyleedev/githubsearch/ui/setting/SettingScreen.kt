package com.gyleedev.githubsearch.ui.setting

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
            .padding(4.dp)
    ) { paddingValues ->

        Row(
            modifier = modifier.padding(paddingValues),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(text = "Setting Screen")
        }


    }
}
