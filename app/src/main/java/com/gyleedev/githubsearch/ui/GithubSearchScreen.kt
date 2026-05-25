package com.gyleedev.githubsearch.ui

import android.app.Activity
import android.content.Intent
import android.os.Build
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresExtension
import androidx.browser.auth.AuthTabIntent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Details
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.core.net.toUri
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.gyleedev.githubsearch.R
import com.gyleedev.githubsearch.domain.model.GetAccessTokenUseCaseResult
import com.gyleedev.githubsearch.feature.detail.DetailScreen
import com.gyleedev.githubsearch.feature.favorite.FavoriteScreen
import com.gyleedev.githubsearch.feature.home.HomeScreen
import com.gyleedev.githubsearch.feature.setting.SettingScreen
import kotlinx.coroutines.flow.collectLatest
import com.gyleedev.githubsearch.BuildConfig as AppBuildConfig

@RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
@Composable
fun GithubSearchScreen(
    modifier: Modifier = Modifier,
    viewModel: MainViewModel = hiltViewModel(),
    navController: NavHostController = rememberNavController(),
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val lifecycleOwner = LocalLifecycleOwner.current
    val snackBarHostState = remember { SnackbarHostState() }
    val loginSuccessMessage = stringResource(id = R.string.log_in_success_message)
    val loginFailMessage = stringResource(id = R.string.log_in_fail_message)
    val redirectUri = remember { AppBuildConfig.REDIRECT_URI }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val intent = result.data
            if (intent != null) {
                val resultUri = intent.data
                if (resultUri != null) {
                    val code = resultUri.getQueryParameter("code")
                    code?.let { code ->
                        if (code.isNotBlank()) {
                            viewModel.getAccessToken(code)
                        }
                    }
                }
            }
        }
    }

    LaunchedEffect(viewModel.alertLoginSuccess, lifecycleOwner) {
        lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.alertLoginSuccess.collectLatest { result ->
                val message = when (result) {
                    GetAccessTokenUseCaseResult.Fail -> loginFailMessage
                    GetAccessTokenUseCaseResult.Success -> loginSuccessMessage
                }
                snackBarHostState.showSnackbar(
                    message = message,
                    duration = SnackbarDuration.Short,
                )
            }
        }
    }

    LaunchedEffect(viewModel.launchOAuthEvent, lifecycleOwner) {
        lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.launchOAuthEvent.collectLatest { loginUri ->
                launchAuthTab(
                    launcher = launcher,
                    loginUri = loginUri,
                    redirectUri = redirectUri,
                )
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackBarHostState) },
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize()) {
            NavHost(
                navController = navController,
                startDestination = BottomNavItem.Home.screenRoute,
                modifier = modifier
                    .fillMaxSize()
                    .padding(top = innerPadding.calculateTopPadding()),
            ) {
                composable(route = BottomNavItem.Home.screenRoute) {
                    HomeScreen(
                        modifier = Modifier.fillMaxSize(),
                        moveToDetail = { navController.navigate("${BottomNavItem.Detail.screenRoute}/$it") },
                        requestAuthentication = viewModel::requestGithubLogin,
                    )
                }

                composable(
                    route = "${BottomNavItem.Detail.screenRoute}/{id}",
                    arguments =
                    listOf(
                        navArgument("id") {
                            type = NavType.StringType
                            nullable = false
                        },
                    ),
                ) {
                    DetailScreen(
                        modifier = Modifier.fillMaxSize(),
                        onBackClick = { navController.navigateUp() },
                    )
                }

                composable(route = BottomNavItem.Favorite.screenRoute) {
                    FavoriteScreen(
                        modifier = Modifier.fillMaxSize(),
                        moveToDetail = { navController.navigate("${BottomNavItem.Detail.screenRoute}/$it") },
                    )
                }

                composable(BottomNavItem.Setting.screenRoute) {
                    SettingScreen(
                        requestAuthentication = viewModel::requestGithubLogin,
                        modifier = Modifier.fillMaxSize(),
                    )
                }
            }

            if (currentRoute != "DETAIL/{id}") {
                BottomNavigation(
                    currentRoute = currentRoute,
                    onClick = { route ->
                        navController.navigate(route) {
                            navController.graph.startDestinationRoute?.let {
                                popUpTo(it) { saveState = true }
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    modifier = Modifier.align(Alignment.BottomCenter),
                )
            }
        }
    }
}

@Composable
fun BottomNavigation(
    currentRoute: String?,
    onClick: (String) -> Unit,
    modifier: Modifier,
) {
    LiquidNavigationBar(
        currentRoute = currentRoute,
        onClick = onClick,
        modifier = modifier,
    )
}

private fun launchAuthTab(
    launcher: ManagedActivityResultLauncher<Intent, ActivityResult>,
    loginUri: String,
    redirectUri: String,
) {
    val authTabIntent = AuthTabIntent.Builder().build()
    val scheme = redirectUri.toUri().scheme ?: ""
    authTabIntent.launch(
        launcher,
        loginUri.toUri(),
        scheme,
    )
}

sealed class BottomNavItem(
    val title: Int,
    val icons: ImageVector,
    val screenRoute: String,
) {
    data object Home : BottomNavItem(R.string.title_home, Icons.Filled.Home, HOME)

    data object Detail : BottomNavItem(R.string.title_detail, Icons.Filled.Details, DETAIL)

    data object Setting : BottomNavItem(R.string.title_setting, Icons.Filled.Settings, SETTING)

    data object Favorite : BottomNavItem(R.string.title_favorite, Icons.Filled.FavoriteBorder, FAVORITE)
}
