package com.gyleedev.githubsearch.ui

import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP
import android.os.Build
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.annotation.RequiresExtension
import androidx.appcompat.app.AppCompatActivity
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.net.toUri
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.gyleedev.githubsearch.core.designsystem.theme.GithubSearchTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val viewModel by viewModels<MainViewModel>()

    @RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GithubSearchTheme {
                GithubSearchScreen()
            }
        }

        handleIntent(intent)

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                launch {
                    viewModel.launchOAuthEvent.collect { url ->
                        launchCustomTabs(url)
                    }
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent?) {
        intent?.data?.getQueryParameter("code")?.let { code ->
            if (code.isNotEmpty()) {
                viewModel.getAccessToken(code)
            } else {
                println("Error exists check your network status")
            }
            // 처리 완료 후 Intent Data를 비워 onResume 등에서 중복 실행되는 것을 방지
            intent.data = null
        }
    }

    private fun launchCustomTabs(url: String) {
        val customTabsIntent = CustomTabsIntent.Builder().build()
        customTabsIntent.intent.flags = FLAG_ACTIVITY_CLEAR_TOP
        customTabsIntent.launchUrl(this, url.toUri())
    }
}
