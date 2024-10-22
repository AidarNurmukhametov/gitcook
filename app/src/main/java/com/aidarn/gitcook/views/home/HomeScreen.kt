package com.aidarn.gitcook.views.home

import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavBackStackEntry
import com.aidarn.gitcook.GitCookAppState
import com.aidarn.gitcook.R
import com.aidarn.gitcook.data.Recipe
import com.aidarn.gitcook.viewmodels.HomeScreenUiState
import com.aidarn.gitcook.viewmodels.HomeViewModel
import com.aidarn.gitcook.views.CenterAlignedTopAppBar
import com.aidarn.gitcook.views.Screen
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    appState: GitCookAppState,
    backStackEntry: NavBackStackEntry,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val mainScreenUiState by viewModel.state.collectAsState()
    val recipes by viewModel.filteredRecipes.collectAsState()
    val favoriteRecipes by viewModel.favoriteRecipes.collectAsState()
    val selectedRecipes by viewModel.selectedRecipes.collectAsState()
    val isDeleteInProgress by viewModel.isDeleteInProgress.collectAsState()
    val queryResult by viewModel.queryResult.collectAsState()
    val tagList by viewModel.tagList.collectAsState()
    val selectionState = mainScreenUiState is HomeScreenUiState.Selecting
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val deletedMsg = stringResource(id = R.string.successfully_deleted)
    val undoMsg = stringResource(id = R.string.undo)
    val mainActions: @Composable RowScope.() -> Unit = {
        if (recipes.count() > 1) {
            IconButton(onClick = {
                val randomRecipeId = viewModel.getRandomBranch()
                appState.navigateTo(Screen.Detailed, randomRecipeId, backStackEntry)
            }) {
                Icon(
                    imageVector = ImageVector.vectorResource(id = R.drawable.dice),
                    contentDescription = stringResource(id = R.string.get_random_recipe)
                )
            }
        }
        IconButton(onClick = {
            appState.navigateTo(Screen.Edit, Recipe.Default.branchId, backStackEntry)
        }) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = stringResource(id = R.string.add)
            )
        }
    }
    val selectionActions: @Composable RowScope.() -> Unit = {
        IconButton(onClick = {
            scope.launch {
                viewModel.onDeletePressed()
                val result = snackbarHostState.showSnackbar(deletedMsg, undoMsg)
                when (result) {
                    SnackbarResult.ActionPerformed -> viewModel.undoDelete()
                    SnackbarResult.Dismissed -> viewModel.deleteConfirmed()
                }
            }
        }, enabled = !isDeleteInProgress) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = stringResource(id = R.string.delete)
            )
        }
    }
    val actions = if (selectionState) selectionActions else mainActions
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val navigateToDetails = { id: Int ->
        appState.navigateTo(
            Screen.Detailed, id, backStackEntry
        )
    }
    Scaffold(modifier = Modifier.fillMaxSize(), topBar = {
        CenterAlignedTopAppBar(
            title = stringResource(id = R.string.app_name),
            actions = actions,
            navigationIcon = {
                IconButton({
                    scope.launch {
                        drawerState.apply {
                            if (isClosed) open() else close()
                        }
                    }
                }) {
                    Icon(Icons.Default.Menu, contentDescription = stringResource(R.string.menu))
                }
            }
        )
    }, snackbarHost = { SnackbarHost(hostState = snackbarHostState) }) { innerPadding ->
        AppDrawer(
            drawerState = drawerState,
            recipes = recipes,
            selectedRecipes = selectedRecipes,
            favoriteRecipes = favoriteRecipes,
            queryResult = queryResult,
            tagList = tagList,
            navigateToDetails = navigateToDetails,
            mainScreenUiState = mainScreenUiState,
            onSelectionChanged = viewModel::onSelectionChanged,
            onAllSelectedPressed = viewModel::onAllSelectedPressed,
            onQueryChange = viewModel::onQueryChange,
            onFilterTagChange = viewModel::onFilterTagChange,
            modifier = Modifier.padding(innerPadding),
        )
    }
}
