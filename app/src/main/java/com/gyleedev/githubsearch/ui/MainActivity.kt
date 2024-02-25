package com.gyleedev.githubsearch.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.gyleedev.githubsearch.ui.theme.GithubSearchTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GithubSearchTheme {
                // A surface container using the 'background' color from the theme
                GithubSearchApp()
            }
        }
    }
}
