package com.aidarn.gitcook

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.aidarn.gitcook.views.Screen

@Composable
fun rememberGitCookAppState(
    navController: NavHostController = rememberNavController(),
    context: Context = LocalContext.current
) = remember(navController, context) {
    GitCookAppState(navController)
}

class GitCookAppState(
    val navController: NavHostController
) {
    fun navigateBack() {
        navController.popBackStack()
    }

    inline fun <reified T : Screen> navigateTo(dest: T, branchId: Int, from: NavBackStackEntry) {
        if (from.getLifecycle().currentState == Lifecycle.State.RESUMED) {
            val encodedUri = Uri.encode(branchId.toString())
            navController.navigate(dest.createRoute(encodedUri))
        }
    }
}
