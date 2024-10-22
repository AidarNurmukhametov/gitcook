package com.aidarn.gitcook.data

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.aidarn.gitcook.data.dao.BranchDao
import com.aidarn.gitcook.data.dao.CommitDao
import com.aidarn.gitcook.data.dao.RecipeDao
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertNotEquals
import org.junit.Before
import org.junit.Test
import kotlin.time.DurationUnit
import kotlin.time.toDuration


class RecipesRepositoryTest {
    private lateinit var repository: RecipesRepository
    private lateinit var db: RecipeDatabase
    private lateinit var branchDao: BranchDao
    private lateinit var commitDao: CommitDao
    private lateinit var recipeDao: RecipeDao

    @Before
    fun setUp() {
        val context: Context = ApplicationProvider.getApplicationContext()
        db = Room.inMemoryDatabaseBuilder(context, RecipeDatabase::class.java).build()
        branchDao = db.branchDao()
        commitDao = db.commitDao()
        recipeDao = db.recipeDao()
        repository = RecipesRepository(commitDao, branchDao, recipeDao)
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun addTest(): Unit = runBlocking {
        val recipeCore =
            RecipeCore(name = "Name", instructions = "Instructions", ingredients = "Ingredients")
        val id = repository.insertRecipe(recipeCore)
        assertNotEquals(0, id)

        val recipes = repository.getAllRecipes().first()
        assertEquals(1, recipes.size)
        val recipe = recipes[0]
        assertEquals("Name", recipe.name)
        assertEquals("Instructions", recipe.instructions)
        assertEquals("Ingredients", recipe.ingredients)

        val allRecipesCore = recipeDao.getAllRecipeCore().first()
        assertEquals(1, allRecipesCore.size)
        val branch = branchDao.getBranchById(recipe.branchId)
        assertNotNull(branch)
        val allBranches = branchDao.getAllBranches().first()
        assertEquals(1, allBranches.size)
        val commit = commitDao.getCommitById(branch!!.commitId)
        assertNotNull(commit)
        val allCommits = commitDao.getAllCommits().first()
        assertEquals(1, allCommits.size)
    }

    @Test
    fun updateTest(): Unit = runBlocking {
        val recipeCore =
            RecipeCore(name = "Name", instructions = "Instructions", ingredients = "Ingredients")
        repository.insertRecipe(recipeCore)
        val recipes = repository.getAllRecipes().first()
        val recipe = recipes[0].copy(name = "Name1")

        val updated = repository.updateRecipe(recipe, "Change name")
        assertNotEquals(0L, updated)

        val updatedRecipes = repository.getAllRecipes().first()
        assertEquals(1, updatedRecipes.size)
        val updatedRecipe = updatedRecipes[0]
        assertEquals("Name1", updatedRecipe.name)
        assertEquals("Instructions", updatedRecipe.instructions)
        assertEquals("Ingredients", updatedRecipe.ingredients)

        val allRecipesCore = recipeDao.getAllRecipeCore().first()
        assertEquals(2, allRecipesCore.size)
        val branch = branchDao.getBranchById(updatedRecipe.branchId)
        assertNotNull(branch)
        val allBranches = branchDao.getAllBranches().first()
        assertEquals(1, allBranches.size)
        val commit = commitDao.getCommitById(branch!!.commitId)
        assertNotNull(commit)
        val allCommits = commitDao.getAllCommits().first()
        assertEquals(2, allCommits.size)
    }

    @Test
    fun secondUpdateTest(): Unit = runBlocking {
        val recipeCore =
            RecipeCore(name = "Name", instructions = "Instructions", ingredients = "Ingredients")
        repository.insertRecipe(recipeCore)
        val recipes = repository.getAllRecipes().first()
        val recipe = recipes[0].copy(name = "Name1")

        repository.updateRecipe(recipe, "Change name")
        val updated =
            repository.updateRecipe(recipe.copy(source = "org.example.com"), "Change source")
        assertNotEquals(0L, updated)

        val updatedRecipes = repository.getAllRecipes().first()
        assertEquals(1, updatedRecipes.size)
        val updatedRecipe = updatedRecipes[0]
        assertEquals("Name1", updatedRecipe.name)
        assertEquals("org.example.com", updatedRecipe.source)
        assertEquals("Instructions", updatedRecipe.instructions)
        assertEquals("Ingredients", updatedRecipe.ingredients)

        val allRecipesCore = recipeDao.getAllRecipeCore().first()
        assertEquals(3, allRecipesCore.size)
        val branch = branchDao.getBranchById(updatedRecipe.branchId)
        assertNotNull(branch)
        val allBranches = branchDao.getAllBranches().first()
        assertEquals(1, allBranches.size)
        val commit = commitDao.getCommitById(branch!!.commitId)
        assertNotNull(commit)
        val allCommits = commitDao.getAllCommits().first()
        assertEquals(3, allCommits.size)
    }

    @Test
    fun getOrCreate(): Unit = runBlocking {
        val recipe = repository.getOrCreateRecipeByBranchId(Recipe.Default.branchId)
        val prepTime = 10.0.toDuration(DurationUnit.MINUTES)
        val newRecipe = recipe.first().copy(name = "Name", prepTime = prepTime)
        repository.updateRecipe(newRecipe, "Updated")

        val updatedRecipes = repository.getAllRecipes().first()
        assertEquals(1, updatedRecipes.size)
        val updatedRecipe = updatedRecipes[0]
        assertEquals("Name", updatedRecipe.name)
        assertEquals(prepTime, updatedRecipe.prepTime)

        val allRecipesCore = recipeDao.getAllRecipeCore().first()
        assertEquals(1, allRecipesCore.size)
        val branch = branchDao.getBranchById(updatedRecipe.branchId)
        assertNotNull(branch)
        val allBranches = branchDao.getAllBranches().first()
        assertEquals(1, allBranches.size)
        val commit = commitDao.getCommitById(branch!!.commitId)
        assertNotNull(commit)
        val allCommits = commitDao.getAllCommits().first()
        assertEquals(1, allCommits.size)
    }
}