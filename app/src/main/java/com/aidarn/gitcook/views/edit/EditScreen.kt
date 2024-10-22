package com.aidarn.gitcook.views.edit

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.aidarn.gitcook.R
import com.aidarn.gitcook.viewmodels.EditViewModel
import com.aidarn.gitcook.viewmodels.EditViewModel.UpdateState
import com.aidarn.gitcook.views.CenterAlignedTopAppBar
import kotlinx.coroutines.launch

@Composable
fun EditScreen(
    onBackPress: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: EditViewModel = hiltViewModel()
) {
    val recipe = viewModel.recipe
    val updateState = viewModel.updateState
    val isRecipeLoaded = viewModel.isRecipeLoaded
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()
    val successMsg = stringResource(id = R.string.saved)
    val failureMsg = stringResource(id = R.string.recipe_not_saved_msg)
    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .imePadding(),
        topBar = {
            CenterAlignedTopAppBar(title = recipe.name,
                actions = {
                    IconButton(
                        onClick = {
                            scope.launch {
                                val state = viewModel.onUpdated()
                                val deferredRes = when (state) {
                                    UpdateState.Success -> true
                                    UpdateState.Fail -> false
                                    else -> null
                                }
                                deferredRes?.let {
                                    val msg = if (it) successMsg else failureMsg
                                    snackbarHostState.showSnackbar(msg)
                                }
                            }
                        },
                        enabled = updateState.canStartUpdate()
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check, contentDescription = stringResource(
                                id = R.string.save_recipe
                            )
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackPress) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(
                                id = R.string.back
                            )
                        )
                    }
                })
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        }) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            Column(
                modifier = Modifier
                    .verticalScroll(scrollState)
                    .padding(horizontal = dimensionResource(R.dimen.page_padding))
            ) {
                if (isRecipeLoaded) {
                    RecipeEditor(
                        recipe = recipe,
                        onNameChanged = viewModel::onNameChanged,
                        onSourceChanged = viewModel::onSourceChanged,
                        onPrepTimeChanged = viewModel::onPrepTimeChanged,
                        onTotalTimeChanged = viewModel::onTotalTimeChanged,
                        onServingsChanged = viewModel::onServingsChanged,
                        onCaloriesChanged = viewModel::onCaloriesChanged,
                        onFatChanged = viewModel::onFatChanged,
                        onCarbsChanged = viewModel::onCarbsChanged,
                        onProteinChanged = viewModel::onProteinChanged,
                        onIngredientsChanged = viewModel::onIngredientsChanged,
                        onTagsChanged = viewModel::onTagsChanged,
                        onImageUriChanged = viewModel::onImageUriChanged,
                        onInstructionsChanged = viewModel::onInstructionsChanged,
                    )
                } else {
                    LoadingMessage()
                }
            }
        }
    }
}

private fun UpdateState.canStartUpdate(): Boolean {
    return this == UpdateState.NotStarted || this == UpdateState.Fail
}

@Composable
private fun LoadingMessage() {
    Text(text = stringResource(id = R.string.wait_for_recipe))
}

