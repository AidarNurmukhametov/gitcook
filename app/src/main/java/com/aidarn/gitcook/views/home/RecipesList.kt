package com.aidarn.gitcook.views.home

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import com.aidarn.gitcook.R
import com.aidarn.gitcook.data.Recipe


@Composable
fun RecipesList(
    recipes: List<Recipe>,
    navigateToDetails: (Int) -> Unit,
    selectedRecipes: List<Recipe>,
    onSelectionChanged: (List<Recipe>) -> Unit,
    onAllSelectedPressed: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    queryResult: List<Recipe> = emptyList(),
    onQueryChange: (String) -> Unit,
    tagList: List<String>,
    onFilterTagChange: (List<String>) -> Unit
) {
    if (recipes.isEmpty()) {
        NoRecipes(modifier = Modifier.fillMaxSize())
        return
    }

    val selectionState = selectedRecipes.isNotEmpty()
    val showSelectAll = selectionState && recipes.size > 1
    AnimatedContent(targetState = showSelectAll, label = "") {
        if (it) {
            val isAllSelected = recipes.count() == selectedRecipes.count()
            Row(
                horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.row_spacing)),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(checked = isAllSelected, onCheckedChange = onAllSelectedPressed)
                Text(text = stringResource(id = R.string.select_all))
            }
        } else {
            Column {
                RecipeSearchBar(
                    queryResult = queryResult,
                    onQueryChange = onQueryChange,
                    navigateTo = navigateToDetails
                )
                TagList(tagList, onFilterTagChange)
            }

        }
    }
    RecipesGrid(
        recipes,
        selectedRecipes,
        selectionState,
        onSelectionChanged,
        navigateToDetails,
        modifier
    )
}

@Composable
@OptIn(ExperimentalFoundationApi::class)
private fun RecipesGrid(
    recipes: List<Recipe>,
    selectedRecipes: List<Recipe>,
    selectionState: Boolean,
    onSelectionChanged: (List<Recipe>) -> Unit,
    navigateToDetails: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = dimensionResource(R.dimen.grid_cell_size)),
        modifier = modifier,
        contentPadding = PaddingValues(vertical = dimensionResource(R.dimen.medium_spacing)),
        verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.medium_spacing)),
        horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.medium_spacing))
    ) {
        items(recipes, key = { it.id }) { recipe ->
            RecipeCard(
                name = recipe.name,
                imageModel = recipe.imageModel,
                isSelected = selectedRecipes.contains(recipe),
                modifier = Modifier
                    .combinedClickable(onLongClick = {
                        if (!selectionState) {
                            onSelectionChanged(selectedRecipes + recipe)
                        }
                    }, onClick = {
                        if (!selectionState) {
                            navigateToDetails(recipe.branchId)
                        } else {
                            if (selectedRecipes.contains(recipe)) {
                                onSelectionChanged(selectedRecipes - recipe)
                            } else {
                                onSelectionChanged(selectedRecipes + recipe)
                            }
                        }
                    })
                    .aspectRatio(1f)
            )
        }
    }
}