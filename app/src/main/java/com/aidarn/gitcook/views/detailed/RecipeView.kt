package com.aidarn.gitcook.views.detailed

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.aidarn.gitcook.Constants
import com.aidarn.gitcook.R
import com.aidarn.gitcook.data.Recipe
import com.aidarn.gitcook.views.RecipePhoto
import com.aidarn.richedit.AsComposable
import kotlin.time.DurationUnit
import kotlin.time.toDuration

@Composable
fun RecipeView(
    recipe: Recipe,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(Constants.IMAGE_RATIO), horizontalArrangement = Arrangement.Center
        ) {
            RecipePhoto(recipe.imageModel, recipe.name)
        }
        Card(modifier = Modifier.fillMaxSize()) {
            Column(modifier = Modifier.padding(dimensionResource(R.dimen.page_padding))) {
                Text(text = recipe.name, style = MaterialTheme.typography.headlineMedium)
                PropertyView(propertyValue = recipe.source, description = R.string.recipe_source)
                PropertyView(propertyValue = recipe.prepTime, description = R.string.prep_time)
                PropertyView(propertyValue = recipe.totalTime, description = R.string.total_time)
                PropertyView(propertyValue = recipe.servings, description = R.string.servings)
                PropertyView(
                    propertyValue = recipe.calories,
                    description = R.string.calories,
                    suffix = stringResource(id = R.string.kcal)
                )
                Row {
                    PropertyView(
                        propertyValue = recipe.fat,
                        description = R.string.fat,
                        suffix = stringResource(
                            id = R.string.gramm
                        )
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    PropertyView(
                        propertyValue = recipe.carbs,
                        description = R.string.carbs,
                        suffix = stringResource(
                            id = R.string.gramm
                        )
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    PropertyView(
                        propertyValue = recipe.protein,
                        description = R.string.protein,
                        suffix = stringResource(
                            id = R.string.gramm
                        )
                    )
                }
                TagsView(rawTags = recipe.tags)
                IngredientsList(recipe.ingredients)
                Instructions(instructions = { recipe.instructions.AsComposable() })
            }
        }
    }
}

@Composable
inline fun <reified T> PropertyView(
    propertyValue: T?,
    @StringRes description: Int,
    modifier: Modifier = Modifier,
    suffix: String = ""
) {
    if (propertyValue != null) {
        Text(
            text = "${stringResource(id = description)}: $propertyValue$suffix",
            modifier = modifier,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Preview(showBackground = true)
@Composable
fun RecipeViewPreview() {
    val recipe = Recipe.Default.copy(
        id = 1,
        name = "Cake",
        source = "www.microsoft.com",
        prepTime = 2.5.toDuration(DurationUnit.HOURS),
        totalTime = 3.toDuration(DurationUnit.HOURS),
        servings = 8,
        calories = 2400,
        fat = 24,
        carbs = 52,
        protein = 3,
        instructions = """
            # Recipe
            
            Step by step guide 
            1. Turn on the heat. Let owen preheat 10 minutes
            >!TIMER PT10S
            
            ![desc](IMAGE_PLACEHOLDER)
            2. Cut a fish
            3. Add cut fish to the cake
            ![desc](IMAGE_PLACEHOLDER)
            4. Bake 40 minutes
            >!TIMER PT40M
        """.trimIndent().replace("IMAGE_PLACEHOLDER", R.drawable.ic_recipe_placeholder.toString()),
        ingredients = "Money_$1000;Lime",
        tags = "Desert;Fancy",
        isFavorite = false,
    )

    Column {
        RecipeView(recipe)
    }
}