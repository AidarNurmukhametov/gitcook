package com.aidarn.gitcook.data.di

import android.content.Context
import androidx.room.Room
import com.aidarn.gitcook.data.RecipeDatabase
import com.aidarn.gitcook.data.RecipesRepository
import com.aidarn.gitcook.data.dao.BranchDao
import com.aidarn.gitcook.data.dao.CommitDao
import com.aidarn.gitcook.data.dao.RecipeDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataDiModule {
    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context
    ): RecipeDatabase =
        Room.databaseBuilder(context, RecipeDatabase::class.java, "recipes.db")
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    @Singleton
    fun provideRecipeDao(database: RecipeDatabase): RecipeDao = database.recipeDao()

    @Provides
    @Singleton
    fun provideCommitDao(database: RecipeDatabase): CommitDao = database.commitDao()

    @Provides
    @Singleton
    fun provideBranchDao(database: RecipeDatabase): BranchDao = database.branchDao()

    @Provides
    @Singleton
    fun provideRecipesRepository(
        recipeDao: RecipeDao,
        commitDao: CommitDao,
        branchDao: BranchDao
    ): RecipesRepository =
        RecipesRepository(recipeDao = recipeDao, commitDao = commitDao, branchDao = branchDao)
}
