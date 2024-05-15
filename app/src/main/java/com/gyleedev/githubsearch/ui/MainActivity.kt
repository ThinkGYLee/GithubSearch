package com.gyleedev.githubsearch.ui

import android.content.Context
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.annotation.RequiresExtension
import androidx.appcompat.app.AppCompatActivity
import androidx.browser.customtabs.CustomTabsIntent
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.gyleedev.githubsearch.BuildConfig
import com.gyleedev.githubsearch.R
import com.gyleedev.githubsearch.ui.theme.GithubSearchTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val viewModel by viewModels<MainViewModel>()

    @RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.auto(
                darkScrim = android.graphics.Color.TRANSPARENT,
                lightScrim = android.graphics.Color.TRANSPARENT
            )
        )
        setContent {
            GithubSearchTheme {
                GithubSearchApp(
                    onLoginClicked = {
                        login(this)
                    }
                )
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.alertLoginSuccess.collect {
                    showLoginResult(it)
                }
            }
        }
    }

    fun login(context: Context) {
        val clientId = BuildConfig.GIT_ID
        val loginUrl = Uri.Builder().scheme("https").authority("github.com")
            .appendPath("login")
            .appendPath("oauth")
            .appendPath("authorize")
            .appendQueryParameter("client_id", clientId)
            .build()

        val customTabsIntent = CustomTabsIntent.Builder().build()

        //아래 플래그를 적용하지 않으면 로그인이 이미 된 상태에서 열 때 앱이 죽음
        customTabsIntent.intent.setFlags(FLAG_ACTIVITY_NEW_TASK)
        customTabsIntent.launchUrl(context, loginUrl)
    }


    override fun onResume() {
        super.onResume()
        // ViewModel에서 로그인한거 받아서 처리
        intent?.data?.getQueryParameter("code")?.let {
            if (it.isNotEmpty()) {
                viewModel.getAccessToken(it)
            } else {
                println("Error exists check your network status")
            }
        }
    }

    private fun showLoginResult(result: Boolean) {
        val resultMessage = if (result) {
            getString(R.string.log_in_success_message)
        } else {
            getString(R.string.log_in_fail_message)
        }

        Toast.makeText(
            this@MainActivity,
            resultMessage,
            Toast.LENGTH_SHORT
        ).show()
    }
}
