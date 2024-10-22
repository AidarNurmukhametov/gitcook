package com.aidarn.gitcook.views.detailed

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import com.aidarn.gitcook.R
import com.aidarn.gitcook.data.getIngredientInfos


@Composable
fun IngredientsList(
    ingredientsString: String,
    modifier: Modifier = Modifier
) {
    if (ingredientsString.isBlank()) {
        return
    }

    val ingredients = getIngredientInfos(ingredientsString)
    val text = ingredients.toBulletList()

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.column_spacing))
    ) {
        Text(
            text = stringResource(id = R.string.ingredients),
            style = MaterialTheme.typography.headlineMedium
        )
        Text(text = text, style = MaterialTheme.typography.bodyLarge)
    }
}