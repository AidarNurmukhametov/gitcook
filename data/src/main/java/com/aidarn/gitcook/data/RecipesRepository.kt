package com.aidarn.gitcook.data

import com.aidarn.gitcook.data.dao.BranchDao
import com.aidarn.gitcook.data.dao.CommitDao
import com.aidarn.gitcook.data.dao.RecipeDao
import com.aidarn.gitcook.data.vcs.Branch
import com.aidarn.gitcook.data.vcs.Commit
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import java.time.Instant


class RecipesRepository(
    private val commitDao: CommitDao,
    private val branchDao: BranchDao,
    private val recipeDao: RecipeDao
) {
    private var isPendingRecipeVersion: Boolean = false

    fun getAllRecipes(): Flow<List<Recipe>> {
        return recipeDao.getAllRecipes()
    }

    suspend fun updateRecipe(recipe: Recipe, commitMessage: String): Long {
        if (isPendingRecipeVersion) {
            val newRecipeCoreId = insertRecipe(recipe.asCore())
            isPendingRecipeVersion = false
            return newRecipeCoreId
        }
        val newRecipeCoreId = recipeDao.insertRecipeCore(recipe.copy(id = 0).asCore())
        val branch = branchDao.getBranchById(recipe.branchId)
            ?: throw IllegalArgumentException("Branch not found for id: ${recipe.branchId}")
        val currentCommitId = branch.commitId

        val newCommit = Commit(
            message = commitMessage,
            timestamp = Instant.now(),
            parentCommitId = currentCommitId,
            recipeId = newRecipeCoreId
        )
        val newCommitId = commitDao.insertCommit(newCommit)


        val updatedBranch = branch.copy(commitId = newCommitId)
        val res = branchDao.updateBranch(updatedBranch)
        check(res != 0)

        return newRecipeCoreId
    }

    suspend fun deleteRecipe(recipe: Recipe) {
        withContext(Dispatchers.IO) {
            val branch = branchDao.getBranchById(recipe.branchId)
                ?: throw IllegalStateException("Branch not found for recipe")

            branchDao.deleteBranch(branch)

            val commits = commitDao.getCommitsForRecipe(recipe.id)
            if (commits.isNotEmpty()) {
                commitDao.deleteCommitsByIds(commits.map { it.commitId })
                recipeDao.deleteRecipeCoresByIds(listOf(recipe.id))
            }
        }
    }

    suspend fun insertRecipe(recipe: RecipeCore): Long {
        val newRecipeCoreId = recipeDao.insertRecipeCore(recipe)

        val newCommit = Commit(
            message = "Create new recipe",
            timestamp = Instant.now(),
            parentCommitId = null,
            recipeId = newRecipeCoreId
        )
        val newCommitId = commitDao.insertCommit(newCommit)

        val newBranch = Branch(
            name = "main",
            commitId = newCommitId
        )
        branchDao.insertBranch(newBranch)
        return newRecipeCoreId
    }

    fun findByKeyword(keyword: String): Flow<List<Recipe>> = recipeDao.findBy(keyword)

    suspend fun insertAll(pendingRecipes: List<Recipe>) {
        pendingRecipes.forEach {
            insertRecipe(it.asCore())
        }
    }

    fun getOrCreateRecipeByBranchId(branchId: Int): Flow<Recipe> {
        if (branchId != Recipe.Default.branchId) {
            return recipeDao.getRecipeByBranchId(branchId)
        }
        isPendingRecipeVersion = true
        return flow {
            emit(Recipe.Default)
        }
    }
}