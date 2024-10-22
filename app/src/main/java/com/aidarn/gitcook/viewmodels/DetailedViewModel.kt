package com.aidarn.gitcook.viewmodels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aidarn.gitcook.Constants
import com.aidarn.gitcook.data.Recipe
import com.aidarn.gitcook.data.RecipesRepository
import com.aidarn.gitcook.views.Screen
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailedViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle, private val recipesRepository: RecipesRepository
) : ViewModel() {
    private val branchId: Int = checkNotNull(
        savedStateHandle.get<String>(Screen.ARG_BRANCH_URI)?.toIntOrNull()
    )

    val recipe: StateFlow<Recipe> = recipesRepository.getOrCreateRecipeByBranchId(branchId)
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(Constants.SUBSCRIBE_TIMEOUT),
            Recipe.Default
        )

    fun toggleFavorites(recipe: Recipe) {
        viewModelScope.launch {
            val newRecipe = recipe.copy(isFavorite = !recipe.isFavorite)
            recipesRepository.updateRecipe(newRecipe, "Toggled isFavorite")
        }
    }
}
