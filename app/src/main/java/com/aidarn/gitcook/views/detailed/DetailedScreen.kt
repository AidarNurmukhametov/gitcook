package com.aidarn.gitcook.views.detailed

import android.content.Intent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.aidarn.gitcook.R
import com.aidarn.gitcook.data.Recipe
import com.aidarn.gitcook.data.getIngredientInfos
import com.aidarn.gitcook.viewmodels.DetailedViewModel
import com.aidarn.gitcook.views.CenterAlignedTopAppBar

@Composable
fun DetailedScreen(
    onBackPress: () -> Unit,
    navigateToEdit: (Int) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: DetailedViewModel = hiltViewModel()
) {
    val recipe by viewModel.recipe.collectAsState()
    val scrollState = rememberScrollState()
    var isDropDownExpanded by remember { mutableStateOf(false) }
    val sendIntent: Intent = Intent().apply {
        action = Intent.ACTION_SEND
        type = "text/plain"
    }
    val context = LocalContext.current
    val title = stringResource(R.string.groceries_list_title, recipe.name)

    Scaffold(modifier = modifier.fillMaxSize(), topBar = {
        CenterAlignedTopAppBar(title = recipe.name, actions = {
            IconButton(
                onClick = {
                    viewModel.toggleFavorites(recipe)
                },
            ) {
                val icon: ImageVector =
                    if (recipe.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder
                Icon(
                    imageVector = icon, contentDescription = stringResource(
                        id = R.string.toggle_is_favorite
                    )
                )
            }
            Box {
                IconButton(
                    onClick = {
                        isDropDownExpanded = true
                    },
                ) {
                    Icon(
                        imageVector = Icons.Default.MoreVert, contentDescription = stringResource(
                            id = R.string.more
                        )
                    )
                }
                DropdownMenu(
                    expanded = isDropDownExpanded,
                    onDismissRequest = { isDropDownExpanded = false }
                ) {
                    DropdownMenuItem(
                        text = { Text(stringResource(R.string.edit)) },
                        onClick = {
                            navigateToEdit(recipe.branchId)
                            isDropDownExpanded = false
                        })
                    DropdownMenuItem(
                        text = { Text(stringResource(R.string.to_groceries_list)) },
                        onClick = {
                            val groceriesList: String = getGroceriesList(recipe, title)
                            sendIntent.putExtra(Intent.EXTRA_TEXT, groceriesList)
                            val shareIntent = Intent.createChooser(sendIntent, null)
                            context.startActivity(shareIntent)
                            isDropDownExpanded = false
                        })
                }
            }
        }, navigationIcon = {
            IconButton(onClick = onBackPress) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(
                        id = R.string.back
                    )
                )
            }
        })
    }) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            Column(
                modifier = Modifier
                    .padding(horizontal = dimensionResource(R.dimen.page_padding))
                    .verticalScroll(scrollState)
            ) {
                RecipeView(recipe = recipe)
            }
        }
    }
}

private fun getGroceriesList(recipe: Recipe, title: String) = buildString {
    appendLine(title)
    val ingredientList = getIngredientInfos(recipe.ingredients)
    append(ingredientList.toBulletList())
}
