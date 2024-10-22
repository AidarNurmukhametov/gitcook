package com.aidarn.gitcook.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.aidarn.gitcook.data.dao.BranchDao
import com.aidarn.gitcook.data.dao.CommitDao
import com.aidarn.gitcook.data.dao.RecipeDao
import com.aidarn.gitcook.data.vcs.Branch
import com.aidarn.gitcook.data.vcs.Commit
import com.aidarn.gitcook.data.vcs.Tag

@Database(
    entities = [RecipeCore::class, Commit::class, Tag::class, Branch::class],
    views = [Recipe::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(ComplexTypeConverters::class)
abstract class RecipeDatabase : RoomDatabase() {
    abstract fun recipeDao(): RecipeDao
    abstract fun commitDao(): CommitDao
    abstract fun branchDao(): BranchDao
}
