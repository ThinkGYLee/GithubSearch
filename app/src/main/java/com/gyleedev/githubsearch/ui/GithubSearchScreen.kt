package com.gyleedev.githubsearch.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Details
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.Gray
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.gyleedev.githubsearch.R
import com.gyleedev.githubsearch.ui.detail.DetailScreen
import com.gyleedev.githubsearch.ui.favorite.FavoriteScreen
import com.gyleedev.githubsearch.ui.home.HomeScreen


sealed class BottomNavItem(
    val title: Int, val icons: ImageVector, val screenRoute: String
) {
    data object Home : BottomNavItem(R.string.app_name, Icons.Filled.Home, HOME)
    data object Detail : BottomNavItem(R.string.title_detail, Icons.Filled.Details, DETAIL)
    data object Setting : BottomNavItem(R.string.title_setting, Icons.Filled.Settings, SETTING)
    data object Favorite : BottomNavItem(R.string.title_favorite, Icons.Filled.StarBorder, FAVORITE)
}


@Composable
fun GithubSearchApp(
    navController: NavHostController = rememberNavController()
) {

    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    Scaffold(
        bottomBar = {
            BottomNavigation(navController = navController)
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = BottomNavItem.Home.screenRoute,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(route = BottomNavItem.Home.screenRoute) {
                HomeScreen(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(),
                    moveToDetail = { navController.navigate("${BottomNavItem.Detail.screenRoute}/$it") },
                )
            }


            composable(
                route = "${BottomNavItem.Detail.screenRoute}/{id}",
                arguments = listOf(
                    navArgument("id") {
                        type = NavType.StringType
                        nullable = false
                    }
                )
            ) {
                DetailScreen(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(),
                    onClick = {navController.navigateUp()}
                )
            }
            composable(route = BottomNavItem.Favorite.screenRoute) {
                FavoriteScreen(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(),
                    moveToDetail = { navController.navigate("${BottomNavItem.Detail.screenRoute}/$it") },
                )
            }

        }
    }
}

@Composable
fun BottomNavigation(navController: NavHostController) {
    val items = listOf(
        BottomNavItem.Home,
        BottomNavItem.Favorite,
        BottomNavItem.Setting,
    )

    androidx.compose.material.BottomNavigation(
        backgroundColor = MaterialTheme.colorScheme.background,
        contentColor = MaterialTheme.colorScheme.onBackground
    ) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        items.forEach { item ->
            BottomNavigationItem(
                icon = {
                    Icon(
                        imageVector = item.icons,
                        contentDescription = stringResource(id = item.title),
                        modifier = Modifier
                            .width(26.dp)
                            .height(26.dp)
                    )
                },
                label = { Text(stringResource(id = item.title), fontSize = 9.sp) },
                selectedContentColor = MaterialTheme.colorScheme.primary,
                unselectedContentColor = Gray,
                selected = currentRoute == item.screenRoute,
                alwaysShowLabel = false,
                onClick = {
                    navController.navigate(item.screenRoute) {
                        navController.graph.startDestinationRoute?.let {
                            popUpTo(it) { saveState = true }
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}