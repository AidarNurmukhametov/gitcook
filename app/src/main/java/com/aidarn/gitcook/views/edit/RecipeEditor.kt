package com.aidarn.gitcook.views.edit

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.aidarn.gitcook.Constants
import com.aidarn.gitcook.R
import com.aidarn.gitcook.data.Recipe
import com.aidarn.gitcook.ui.theme.GitCookTheme
import com.aidarn.richedit.RichEdit
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.toDuration

@Composable
fun RecipeEditor(
    recipe: Recipe,
    onNameChanged: (String) -> Unit,
    onSourceChanged: (String) -> Unit,
    onPrepTimeChanged: (Duration?) -> Unit,
    onTotalTimeChanged: (Duration?) -> Unit,
    onServingsChanged: (Int?) -> Unit,
    onCaloriesChanged: (Int?) -> Unit,
    onFatChanged: (Int?) -> Unit,
    onCarbsChanged: (Int?) -> Unit,
    onProteinChanged: (Int?) -> Unit,
    onIngredientsChanged: (String) -> Unit,
    onTagsChanged: (String) -> Unit,
    onImageUriChanged: (Any) -> Unit,
    onInstructionsChanged: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var useRichEdit by remember { mutableStateOf(false) }
    Column(
        verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.column_spacing)),
        modifier = modifier.padding(dimensionResource(id = R.dimen.small_padding))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(Constants.IMAGE_RATIO), horizontalArrangement = Arrangement.Center
        ) {
            ImageBox(imageModel = recipe.imageModel, onImageUriChanged = onImageUriChanged)
        }
        OutlinedTextField(
            value = recipe.name,
            label = { Text(text = stringResource(id = R.string.recipe_name)) },
            onValueChange = onNameChanged,
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
        )
        OutlinedTextField(
            value = recipe.source ?: "",
            label = { Text(text = stringResource(id = R.string.recipe_source)) },
            onValueChange = onSourceChanged,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
        )
        Text(text = stringResource(id = R.string.prep_time))
        DurationEditor(recipe.prepTime, onPrepTimeChanged)
        Text(text = stringResource(id = R.string.total_time))
        DurationEditor(recipe.totalTime, onTotalTimeChanged)
        Row(horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.row_spacing))) {
            val textFieldModifier = Modifier
                .weight(1f)
            IntPropertyEditor(
                value = recipe.servings,
                valueDesc = stringResource(id = R.string.servings),
                onValueChanged = onServingsChanged,
                modifier = textFieldModifier
            )
            IntPropertyEditor(
                value = recipe.calories,
                valueDesc = stringResource(id = R.string.calories),
                onValueChanged = onCaloriesChanged,
                modifier = textFieldModifier,
                suffix = { Text(text = stringResource(id = R.string.kcal)) }
            )
        }
        Text(
            text = stringResource(R.string.nutrition)
        )
        Row(horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.row_spacing))) {
            val textFieldModifier = Modifier
                .weight(1f)

            IntPropertyEditor(
                value = recipe.fat,
                valueDesc = stringResource(id = R.string.fat),
                onValueChanged = onFatChanged,
                modifier = textFieldModifier,
                suffix = { Text(text = stringResource(id = R.string.gramm)) }
            )
            IntPropertyEditor(
                value = recipe.carbs,
                valueDesc = stringResource(id = R.string.carbs),
                onValueChanged = onCarbsChanged,
                modifier = textFieldModifier,
                suffix = { Text(text = stringResource(id = R.string.gramm)) }
            )
            IntPropertyEditor(
                value = recipe.protein,
                valueDesc = stringResource(id = R.string.protein),
                onValueChanged = onProteinChanged,
                modifier = textFieldModifier,
                suffix = { Text(text = stringResource(id = R.string.gramm)) }
            )
        }
        Text(text = stringResource(id = R.string.ingredients))
        IngredientEditor(recipe.ingredients, onIngredientsChanged)
        Text(text = stringResource(id = R.string.tags))
        TagEditor(recipe.tags, onTagsChanged)
        Text(text = stringResource(id = R.string.instructions))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(stringResource(R.string.use_rich_edit))
            Switch(useRichEdit, { useRichEdit = it })
        }
        if (useRichEdit) {
            RichEdit(
                recipe.instructions,
                onInstructionsChanged,
                modifier = Modifier.fillMaxWidth()
            )
        } else {
            SimpleEditor(recipe.instructions, onInstructionsChanged, Modifier.fillMaxWidth())
        }
    }
}

@Preview
@Composable
fun RecipeEditorPreview() {
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
        instructions = "",
        ingredients = "Money",
        tags = "Desert",
        isFavorite = false,
    )
    var recipeState by remember {
        mutableStateOf(recipe)
    }
    GitCookTheme {
        RecipeEditor(
            recipe = recipeState,
            onNameChanged = {
                recipeState = recipeState.copy(name = it)
            },
            onSourceChanged = {
                recipeState = recipeState.copy(source = it)
            },
            onPrepTimeChanged = {
                recipeState = recipeState.copy(prepTime = it)
            },
            onTotalTimeChanged = {
                recipeState = recipeState.copy(totalTime = it)
            },
            onServingsChanged = {
                recipeState = recipeState.copy(servings = it)
            },
            onCaloriesChanged = {
                recipeState = recipeState.copy(calories = it)
            },
            onFatChanged = {
                recipeState = recipeState.copy(fat = it)
            },
            onCarbsChanged = {
                recipeState = recipeState.copy(carbs = it)
            },
            onProteinChanged = {
                recipeState = recipeState.copy(protein = it)
            },
            onIngredientsChanged = {
                recipeState = recipeState.copy(ingredients = it)
            },
            onTagsChanged = {
                recipeState = recipeState.copy(tags = it)
            },
            onImageUriChanged = {
                recipeState = recipeState.copy(imageModel = it)
            },
            onInstructionsChanged = {
                recipeState = recipeState.copy(imageModel = it)
            }
        )
    }
}