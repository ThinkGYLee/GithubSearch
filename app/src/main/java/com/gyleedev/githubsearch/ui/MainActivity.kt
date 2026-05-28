package com.gyleedev.githubsearch.ui

import android.os.Build
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresExtension
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import com.gyleedev.githubsearch.core.designsystem.theme.GithubSearchTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    @RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)

        var isSplashReady = false
        lifecycleScope.launch {
            delay(1500L)
            isSplashReady = true
        }

        splashScreen.setKeepOnScreenCondition { !isSplashReady }

        enableEdgeToEdge()
        setContent {
            GithubSearchTheme {
                GithubSearchScreen()
            }
        }
    }
}
