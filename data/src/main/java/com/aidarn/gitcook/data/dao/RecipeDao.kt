package com.aidarn.gitcook.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.aidarn.gitcook.data.Recipe
import com.aidarn.gitcook.data.RecipeCore
import kotlinx.coroutines.flow.Flow


@Dao
interface RecipeDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE, entity = RecipeCore::class)
    suspend fun insertRecipeCore(recipeCore: RecipeCore): Long

    @Query("DELETE FROM recipes WHERE id IN (:ids)")
    suspend fun deleteRecipeCoresByIds(ids: List<Long>)

    @Query("SELECT * FROM recipes")
    fun getAllRecipeCore(): Flow<List<RecipeCore>>

    @Query("SELECT * FROM Recipe")
    fun getAllRecipes(): Flow<List<Recipe>>

    @Query(
        "SELECT * FROM Recipe " +
                "WHERE name LIKE '%' || :keyword || '%' " +
                "   OR (source IS NOT NULL AND source LIKE '%' || :keyword || '%') " +
                "   OR instructions LIKE '%' || :keyword || '%' " +
                "   OR ingredients LIKE '%' || :keyword || '%' " +
                "   OR (tags IS NOT NULL AND tags LIKE '%' || :keyword || '%')"
    )
    fun findBy(keyword: String): Flow<List<Recipe>>

    @Query("SELECT * FROM Recipe WHERE branchId = :branchId")
    fun getRecipeByBranchId(branchId: Int): Flow<Recipe>
}