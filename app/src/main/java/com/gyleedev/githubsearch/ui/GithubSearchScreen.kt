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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.gyleedev.githubsearch.BuildConfig
import com.gyleedev.githubsearch.R
import com.gyleedev.githubsearch.feature.detail.DetailScreen
import com.gyleedev.githubsearch.feature.favorite.FavoriteScreen
import com.gyleedev.githubsearch.feature.home.HomeScreen
import com.gyleedev.githubsearch.feature.setting.SettingScreen

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

@RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
@Composable
fun GithubSearchScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    onAuthenticationRequest: () -> Unit,
) {
    var bottomBarStatus by rememberSaveable {
        mutableStateOf(true)
    }

    Scaffold(
        bottomBar = {
            if (bottomBarStatus) {
                BottomNavigation(navController = navController, modifier = Modifier)
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
                    requestAuthentication = { onAuthenticationRequest() },
                    requestBottomBarStatus = { bottomBarStatus = !it },
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
                    onClick = { navController.navigateUp() },
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
                    requestAuthentication = { onAuthenticationRequest() },
                    versionName = BuildConfig.VERSION_NAME,
                    modifier = Modifier.fillMaxSize(),
                )
            }
        }
    }
}

@Composable
fun BottomNavigation(
    navController: NavHostController,
    modifier: Modifier,
) {
    val items =
        listOf(
            BottomNavItem.Home,
            BottomNavItem.Favorite,
            BottomNavItem.Setting,
        )
    NavigationBar(
        modifier = modifier.fillMaxWidth(),
    ) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        items.forEach { item ->
            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector = item.icons,
                        contentDescription = stringResource(id = item.title),
                    )
                },
                selected = currentRoute == item.screenRoute,
                onClick = {
                    navController.navigate(item.screenRoute) {
                        navController.graph.startDestinationRoute?.let {
                            popUpTo(it) { saveState = true }
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
            )
        }
    }
}
