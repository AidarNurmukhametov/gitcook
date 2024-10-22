package com.aidarn.gitcook.views.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import com.aidarn.gitcook.R
import com.aidarn.gitcook.data.Recipe
import com.aidarn.gitcook.views.RecipePhoto


@Composable
internal fun RowRecipeItem(it: Recipe, navigateTo: (Int) -> Unit) {
    ListItem(leadingContent = {
        RecipePhoto(
            it.imageModel,
            it.name,
            modifier = Modifier.size(dimensionResource(R.dimen.search_result_icon_size))
        )
    }, headlineContent = {
        Text(it.name)
    }, modifier = Modifier.clickable {
        navigateTo(it.branchId)
    })
}
