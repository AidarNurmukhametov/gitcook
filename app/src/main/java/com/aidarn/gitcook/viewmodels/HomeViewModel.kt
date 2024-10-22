package com.aidarn.gitcook.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aidarn.gitcook.Constants
import com.aidarn.gitcook.data.Recipe
import com.aidarn.gitcook.data.RecipesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject
import kotlin.random.Random

@HiltViewModel
@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModel @Inject constructor(private val recipesRepository: RecipesRepository) :
    ViewModel() {
    private val _filterTagList = MutableStateFlow<List<String>>(emptyList())
    val tagList: StateFlow<List<String>>
        get() = _allTags
    private val queryFlow = MutableStateFlow("")
    val queryResult = queryFlow.flatMapLatest {
        recipesRepository.findByKeyword(it)
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(Constants.SUBSCRIBE_TIMEOUT),
        emptyList()
    )
    private val _state = MutableStateFlow<HomeScreenUiState>(HomeScreenUiState.Loading)
    private val allRecipesFlow = recipesRepository.getAllRecipes()
    private val _allTags = allRecipesFlow.mapLatest { recipes ->
        val res = mutableSetOf<String>()
        for (recipe in recipes) {
            recipe.tags?.split(Constants.SEPARATOR)?.forEach {
                res.add(it)
            }
        }
        res.toList()
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(Constants.SUBSCRIBE_TIMEOUT),
        emptyList()
    )
    val filteredRecipes: StateFlow<List<Recipe>> =
        combine(
            allRecipesFlow,
            _filterTagList
        ) { recipes: List<Recipe>, filterTags: List<String> ->
            _state.value = HomeScreenUiState.Ready
            if (filterTags.isEmpty()) {
                recipes
            } else {
                val res = recipes.filter { recipe ->
                    filterTags.any {
                        recipe.tags?.contains(it) ?: false
                    }
                }
                res
            }
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(Constants.SUBSCRIBE_TIMEOUT),
            emptyList()
        )
    val favoriteRecipes: StateFlow<List<Recipe>> = allRecipesFlow.mapLatest { recipes ->
        recipes.filter { it.isFavorite }
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(Constants.SUBSCRIBE_TIMEOUT),
        emptyList()
    )
    val state: StateFlow<HomeScreenUiState>
        get() = _state
    private val _selectedRecipes = MutableStateFlow<List<Recipe>>(emptyList())
    val selectedRecipes: StateFlow<List<Recipe>>
        get() = _selectedRecipes
    private val _isDeleteInProgress = MutableStateFlow(false)
    val isDeleteInProgress: StateFlow<Boolean>
        get() = _isDeleteInProgress
    private var pendingRecipes = emptyList<Recipe>()

    fun onSelectionChanged(recipes: List<Recipe>) {
        _selectedRecipes.value = recipes
        if (recipes.isNotEmpty()) {
            _state.value = HomeScreenUiState.Selecting
        } else {
            _state.value = HomeScreenUiState.Ready
        }
    }

    fun onAllSelectedPressed(isAllSelected: Boolean) {
        val selection = if (isAllSelected) filteredRecipes.value else emptyList()
        onSelectionChanged(selection)
    }

    suspend fun onDeletePressed() {
        _isDeleteInProgress.value = true
        selectedRecipes.value.forEach {
            recipesRepository.deleteRecipe(it)
        }
        pendingRecipes = selectedRecipes.value
        _selectedRecipes.value = emptyList()
        _isDeleteInProgress.value = false
    }

    suspend fun undoDelete() {
        recipesRepository.insertAll(pendingRecipes)
    }

    fun deleteConfirmed() {
        pendingRecipes = emptyList()
    }

    fun getRandomBranch(): Int {
        val randomItem = Random.nextInt(filteredRecipes.value.size)
        return checkNotNull(filteredRecipes.value[randomItem].branchId)
    }

    fun onQueryChange(query: String) {
        queryFlow.value = query
    }

    fun onFilterTagChange(filterTags: List<String>) {
        _filterTagList.value = filterTags
    }
}

sealed interface HomeScreenUiState {
    data object Loading : HomeScreenUiState
    data class Error(val errorMessage: String? = null) : HomeScreenUiState
    data object Ready : HomeScreenUiState
    data object Selecting : HomeScreenUiState
}