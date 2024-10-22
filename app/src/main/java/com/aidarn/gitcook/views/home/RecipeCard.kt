package com.aidarn.gitcook.views.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.aidarn.gitcook.R
import com.aidarn.gitcook.data.Recipe
import com.aidarn.gitcook.views.RecipePhoto

@Composable
internal fun RecipeCard(
    name: String,
    imageModel: Any,
    isSelected: Boolean,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier, contentAlignment = Alignment.BottomEnd) {
        Card {
            Text(
                text = name,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(
                    vertical = dimensionResource(R.dimen.small_padding),
                    horizontal = dimensionResource(id = R.dimen.card_padding)
                )
            )
            RecipePhoto(imageModel, name)
        }
        if (isSelected) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = stringResource(id = R.string.checkmark),
                tint = MaterialTheme.colorScheme.surfaceTint
            )
        }
    }
}

@Preview
@Composable
internal fun RecipeCardPreview() {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier.height(150.dp)
    ) {
        item {
            RecipeCard("Cake", Recipe.Default.imageModel, false, Modifier.aspectRatio(1f))
        }
        item {
            RecipeCard(
                "Very Very Long Cake",
                Recipe.Default.imageModel,
                true,
                Modifier.aspectRatio(1f)
            )
        }
    }
}