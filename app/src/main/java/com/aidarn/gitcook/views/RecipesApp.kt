package com.aidarn.gitcook.views

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.aidarn.gitcook.GitCookAppState
import com.aidarn.gitcook.rememberGitCookAppState
import com.aidarn.gitcook.views.detailed.DetailedScreen
import com.aidarn.gitcook.views.edit.EditScreen
import com.aidarn.gitcook.views.home.HomeScreen


@Composable
fun RecipesApp(
    appState: GitCookAppState = rememberGitCookAppState()
) {
    NavHost(
        navController = appState.navController,
        startDestination = Screen.Home.route,
    ) {
        val onBackPress = { appState.navigateBack() }

        composable(Screen.Home.route) { backStackEntry ->
            HomeScreen(appState, backStackEntry)
        }
        composable(Screen.Edit.route) {
            EditScreen(onBackPress = onBackPress)
        }
        composable(Screen.Detailed.route) { backStackEntry ->
            DetailedScreen(onBackPress = onBackPress, navigateToEdit = { id ->
                appState.navigateTo(Screen.Edit, branchId = id, backStackEntry)
            })
        }
    }
}

sealed class Screen(val route: String) {
    open fun createRoute(branchUri: String): String {
        throw NotImplementedError()
    }

    data object Home : Screen("home")
    data object Edit : Screen("edit/{$ARG_BRANCH_URI}") {
        override fun createRoute(branchUri: String) = "edit/$branchUri"
    }

    data object Detailed : Screen("detailed/{$ARG_BRANCH_URI}") {
        override fun createRoute(branchUri: String) = "detailed/$branchUri"
    }

    companion object {
        const val ARG_BRANCH_URI = "branchUri"
    }
}
