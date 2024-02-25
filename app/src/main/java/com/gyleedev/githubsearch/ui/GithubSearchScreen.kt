package com.gyleedev.githubsearch.ui

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.gyleedev.githubsearch.R
import com.gyleedev.githubsearch.ui.detail.DetailScreen
import com.gyleedev.githubsearch.ui.home.HomeScreen

enum class GithubSearchScreen(@StringRes val title: Int) {
    Home(title = R.string.app_name),
    Detail(title = R.string.title_detail),
    Favorite(title = R.string.title_favorite),
}


@Composable
fun GithubSearchApp(
    navController: NavHostController = rememberNavController()
) {

    val backStackEntry by navController.currentBackStackEntryAsState()

    Scaffold(
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = GithubSearchScreen.Home.name,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(route = GithubSearchScreen.Home.name) {
                HomeScreen(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(),
                    moveToDetail = { navController.navigate("${GithubSearchScreen.Detail.name}/$it") },
                )
            }


            composable(
                route = "${GithubSearchScreen.Detail.name}/{id}",
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
                        .padding()
                )
            }



        }

    }
}