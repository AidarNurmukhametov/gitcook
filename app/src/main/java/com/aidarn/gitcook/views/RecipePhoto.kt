package com.aidarn.gitcook.views

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import coil.compose.AsyncImage
import com.aidarn.gitcook.R
import com.aidarn.gitcook.data.Recipe

@Composable
internal fun RecipePhoto(
    imageModel: Any,
    name: String,
    modifier: Modifier = Modifier
) {
    val placeholder =
        rememberVectorPainter(image = ImageVector.vectorResource(id = R.drawable.ic_recipe_placeholder))
    val fallback =
        rememberVectorPainter(image = ImageVector.vectorResource(id = com.aidarn.richedit.R.drawable.image))
    val (model, colorFilter) = if (imageModel == Recipe.Default.imageModel) {
        R.drawable.ic_recipe_placeholder to ColorFilter.tint(MaterialTheme.colorScheme.surfaceTint)
    } else {
        imageModel to null
    }
    AsyncImage(
        model = model,
        contentDescription = stringResource(id = R.string.recipe_of, name),
        colorFilter = colorFilter,
        placeholder = placeholder,
        fallback = fallback,
        alignment = Alignment.Center,
        modifier = modifier
            .fillMaxSize()
            .padding(dimensionResource(id = R.dimen.small_padding))
    )
}