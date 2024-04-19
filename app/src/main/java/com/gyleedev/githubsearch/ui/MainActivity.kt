package com.gyleedev.githubsearch.ui

import android.content.Context
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.content.SharedPreferences
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.annotation.RequiresExtension
import androidx.browser.customtabs.CustomTabsIntent
import androidx.lifecycle.lifecycleScope
import com.gyleedev.githubsearch.BuildConfig
import com.gyleedev.githubsearch.R
import com.gyleedev.githubsearch.ui.home.HomeViewModel
import com.gyleedev.githubsearch.ui.theme.GithubSearchTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel by viewModels<HomeViewModel>()

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
                        try {
                            login(this)
                        } catch (e: Error) {
                            println("e")
                        }
                    }
                )
            }
        }

        lifecycleScope.launch {
            viewModel.saveToken.collect {
                updateToken(it)
                showLoginMessage()
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
        println("onResume")
        // ViewModel에서 로그인한거 받아서 처리
        intent?.data?.getQueryParameter("code")?.let {
            println("onResume code: $it")

            // 엑세스 토큰 받아와야함
            lifecycleScope.launch {
                try {
                    viewModel.getAccessToken(it)
                } catch (e: Error) {
                    println(e)
                }
            }
        }
    }

    private fun showLoginMessage() {
        Toast.makeText(
            this@MainActivity,
            getString(R.string.log_in_toast_message),
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun updateToken(token: String) {
        val preference = getSharedPreferences("AccessToken", MODE_PRIVATE)
        val editor: SharedPreferences.Editor = preference.edit()
        editor.putString("Token", token)
        editor.apply()
    }

    private fun loadToken(): String {
        val preference = getSharedPreferences("AccessToken", MODE_PRIVATE)
        val tokenData = preference.getString("Token", "no Token")
        return preference.getString("Token", "no Token") ?: "no Token"
    }
}
