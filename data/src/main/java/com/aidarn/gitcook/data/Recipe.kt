package com.aidarn.gitcook.data

import androidx.room.DatabaseView
import androidx.room.Entity
import androidx.room.PrimaryKey
import javax.annotation.concurrent.Immutable
import kotlin.time.Duration


@Entity(
    tableName = "recipes"
)
@Immutable
data class RecipeCore(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val name: String,
    val source: String? = null,
    val prepTime: Duration? = null,
    val totalTime: Duration? = null,
    val servings: Int? = null,
    val calories: Int? = null,
    val fat: Int? = null,
    val carbs: Int? = null,
    val protein: Int? = null,
    val instructions: String,
    val ingredients: String,
    val tags: String? = null,
    val isFavorite: Boolean = false,
    val imageModel: Any = ""
)

@Immutable
@DatabaseView(
    "SELECT recipes.id AS id, " +
            "recipes.name AS name, " +
            "recipes.source AS source, " +
            "recipes.prepTime AS prepTime, " +
            "recipes.totalTime AS totalTime, " +
            "recipes.servings AS servings, " +
            "recipes.calories AS calories, " +
            "recipes.fat AS fat, " +
            "recipes.carbs AS carbs, " +
            "recipes.protein AS protein, " +
            "recipes.instructions AS instructions, " +
            "recipes.ingredients AS ingredients, " +
            "recipes.tags AS tags, " +
            "recipes.isFavorite AS isFavorite, " +
            "recipes.imageModel AS imageModel, " +
            "branches.id AS branchId " +
            "FROM branches " +
            "JOIN commits ON branches.commitId = commits.commitId " +
            "JOIN recipes ON commits.recipeId = recipes.id"
)
data class Recipe(
    val id: Long = 0,
    val name: String,
    val source: String?,
    val prepTime: Duration?,
    val totalTime: Duration?,
    val servings: Int?,
    val calories: Int?,
    val fat: Int?,
    val carbs: Int?,
    val protein: Int?,
    val instructions: String,
    val ingredients: String,
    val tags: String?,
    val isFavorite: Boolean,
    val imageModel: Any,
    val branchId: Int
) {
    companion object {
        val Default = Recipe(
            id = 0,
            name = "",
            source = null,
            prepTime = null,
            totalTime = null,
            servings = null,
            calories = null,
            fat = null,
            carbs = null,
            protein = null,
            instructions = "",
            ingredients = "",
            tags = null,
            isFavorite = false,
            imageModel = "",
            branchId = 0
        )
    }

    fun asCore() = RecipeCore(
        id,
        name,
        source,
        prepTime,
        totalTime,
        servings,
        calories,
        fat,
        carbs,
        protein,
        instructions,
        ingredients,
        tags,
        isFavorite,
        imageModel
    )
}