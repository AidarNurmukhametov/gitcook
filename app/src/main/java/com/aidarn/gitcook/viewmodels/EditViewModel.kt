package com.aidarn.gitcook.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aidarn.gitcook.data.Recipe
import com.aidarn.gitcook.data.RecipesRepository
import com.aidarn.gitcook.views.Screen
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration

@HiltViewModel
class EditViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val recipesRepository: RecipesRepository
) : ViewModel() {

    enum class UpdateState {
        NotStarted, InProgress, Success, Fail
    }

    private var branchId: Int =
        checkNotNull(savedStateHandle.get<String>(Screen.ARG_BRANCH_URI)?.toIntOrNull())
    var recipe by mutableStateOf(Recipe.Default)
        private set
    var updateState by mutableStateOf(UpdateState.Success)
        private set
    var isRecipeLoaded by mutableStateOf(false)
        private set

    init {
        viewModelScope.launch {
            recipesRepository.getOrCreateRecipeByBranchId(branchId).collect {
                recipe = it
                isRecipeLoaded = true
            }
        }
    }

    suspend fun onUpdated(): UpdateState {
        updateState = UpdateState.InProgress
        val newRecipeId = recipesRepository.updateRecipe(recipe, "Recipe updated")
        val newState = if (newRecipeId != -1L)
            UpdateState.Success else UpdateState.Fail
        updateState = newState
        return newState
    }

    fun onNameChanged(name: String) {
        updateState = UpdateState.NotStarted
        recipe = recipe.copy(name = name)
    }

    fun onSourceChanged(source: String) {
        updateState = UpdateState.NotStarted
        recipe = recipe.copy(source = source)
    }

    fun onPrepTimeChanged(prepTime: Duration?) {
        updateState = UpdateState.NotStarted
        recipe = recipe.copy(prepTime = prepTime)
    }

    fun onTotalTimeChanged(totalTime: Duration?) {
        updateState = UpdateState.NotStarted
        recipe = recipe.copy(totalTime = totalTime)
    }

    fun onServingsChanged(servings: Int?) {
        updateState = UpdateState.NotStarted
        recipe = recipe.copy(servings = servings)
    }

    fun onCaloriesChanged(calories: Int?) {
        updateState = UpdateState.NotStarted
        recipe = recipe.copy(calories = calories)
    }

    fun onFatChanged(fat: Int?) {
        updateState = UpdateState.NotStarted
        recipe = recipe.copy(fat = fat)
    }

    fun onCarbsChanged(carbs: Int?) {
        updateState = UpdateState.NotStarted
        recipe = recipe.copy(carbs = carbs)
    }

    fun onProteinChanged(protein: Int?) {
        updateState = UpdateState.NotStarted
        recipe = recipe.copy(protein = protein)
    }

    fun onIngredientsChanged(ingredients: String) {
        updateState = UpdateState.NotStarted
        recipe = recipe.copy(ingredients = ingredients.trim())
    }

    fun onTagsChanged(tags: String) {
        updateState = UpdateState.NotStarted
        recipe = recipe.copy(tags = tags.trim())
    }

    fun onImageUriChanged(model: Any) {
        updateState = UpdateState.NotStarted
        recipe = recipe.copy(imageModel = model)
    }

    fun onInstructionsChanged(instructions: String) {
        updateState = UpdateState.NotStarted
        recipe = recipe.copy(instructions = instructions)
    }
}
