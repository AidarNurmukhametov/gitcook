package com.aidarn.gitcook.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.aidarn.gitcook.data.vcs.Commit
import kotlinx.coroutines.flow.Flow


@Dao
interface CommitDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCommit(commit: Commit): Long

    @Delete
    suspend fun deleteCommit(commit: Commit)

    @Query("SELECT * FROM commits")
    fun getAllCommits(): Flow<List<Commit>>

    @Query("SELECT * FROM commits WHERE commitId = :commitId")
    suspend fun getCommitById(commitId: Long): Commit?

    @Query("DELETE FROM commits WHERE commitId IN (:commitIds)")
    suspend fun deleteCommitsByIds(commitIds: List<Long>)

    @Query("SELECT * FROM commits WHERE recipeId = :recipeId")
    fun getCommitsForRecipe(recipeId: Long): List<Commit>
}