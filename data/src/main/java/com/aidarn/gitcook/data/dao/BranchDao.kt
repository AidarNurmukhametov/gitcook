package com.aidarn.gitcook.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.aidarn.gitcook.data.vcs.Branch
import kotlinx.coroutines.flow.Flow


@Dao
interface BranchDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBranch(branch: Branch): Long

    @Update
    suspend fun updateBranch(branch: Branch): Int

    @Delete
    suspend fun deleteBranch(branch: Branch)

    @Query("SELECT * FROM branches")
    fun getAllBranches(): Flow<List<Branch>>

    @Query("SELECT * FROM branches WHERE id = :id")
    suspend fun getBranchById(id: Int): Branch?

    @Query("SELECT * FROM branches WHERE name = :name")
    suspend fun getBranchByName(name: String): Branch?

    @Query("DELETE FROM branches WHERE id IN (:ids)")
    suspend fun deleteBranchesByIds(ids: List<Int>)
}