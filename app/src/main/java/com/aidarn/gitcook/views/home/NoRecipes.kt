package com.aidarn.gitcook.views.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import com.aidarn.gitcook.R


@Composable
internal fun NoRecipes(modifier: Modifier = Modifier) {
    val painter = rememberVectorPainter(ImageVector.vectorResource(R.drawable.no_meals))
    val tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.66f)
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            painter = painter,
            contentDescription = null,
            modifier = Modifier.size(dimensionResource(R.dimen.no_recipes_icon_size)),
            tint = tint
        )
        Text(
            text = stringResource(R.string.no_recipes),
            style = MaterialTheme.typography.headlineLarge,
            color = tint
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun NoRecipesPreview(modifier: Modifier = Modifier) {
    NoRecipes()
}