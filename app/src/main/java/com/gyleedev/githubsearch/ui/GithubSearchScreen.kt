package com.gyleedev.githubsearch.ui

import android.os.Build
import androidx.annotation.RequiresExtension
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Details
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
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
import com.gyleedev.githubsearch.feature.detail.DetailScreen
import com.gyleedev.githubsearch.feature.favorite.FavoriteScreen
import com.gyleedev.githubsearch.feature.home.HomeScreen
import com.gyleedev.githubsearch.feature.setting.SettingScreen
import kotlinx.coroutines.flow.collectLatest

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

    LaunchedEffect(viewModel.alertLoginSuccess, lifecycleOwner) {
        lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.alertLoginSuccess.collectLatest { result ->
                val message = if (result) loginSuccessMessage else loginFailMessage
                snackBarHostState.showSnackbar(
                    message = message,
                    duration = SnackbarDuration.Short,
                )
            }
        }
    }

    Scaffold(
        bottomBar = {
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
                    modifier = Modifier,
                )
            }
        },
        // Scaffold가 자동으로 주입하는 inset 무시
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = BottomNavItem.Home.screenRoute,
            modifier = modifier
                .padding(innerPadding)
                .consumeWindowInsets(innerPadding),
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
    }
}

@Composable
fun BottomNavigation(
    currentRoute: String?,
    onClick: (String) -> Unit,
    modifier: Modifier,
) {
    val items = remember {
        listOf(
            BottomNavItem.Home,
            BottomNavItem.Favorite,
            BottomNavItem.Setting,
        )
    }

    NavigationBar(
        modifier = modifier.fillMaxWidth(),
    ) {
        items.forEach { item ->
            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector = item.icons,
                        contentDescription = stringResource(id = item.title),
                    )
                },
                selected = currentRoute == item.screenRoute,
                onClick = { onClick(item.screenRoute) },
            )
        }
    }
}

sealed class BottomNavItem(
    val title: Int,
    val icons: ImageVector,
    val screenRoute: String,
) {
    data object Home : BottomNavItem(R.string.app_name, Icons.Filled.Home, HOME)

    data object Detail : BottomNavItem(R.string.title_detail, Icons.Filled.Details, DETAIL)

    data object Setting : BottomNavItem(R.string.title_setting, Icons.Filled.Settings, SETTING)

    data object Favorite : BottomNavItem(R.string.title_favorite, Icons.Filled.StarBorder, FAVORITE)
}
