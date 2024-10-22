package com.aidarn.gitcook.data.vcs

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.aidarn.gitcook.data.RecipeCore
import java.time.Instant
import javax.annotation.concurrent.Immutable


@Immutable
@Entity(
    tableName = "commits",
    foreignKeys = [
        ForeignKey(
            entity = RecipeCore::class,
            parentColumns = ["id"],
            childColumns = ["recipeId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Commit::class,
            parentColumns = ["commitId"],
            childColumns = ["parentCommitId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
)
data class Commit(
    @PrimaryKey(autoGenerate = true) val commitId: Long = 0,
    val message: String,
    val timestamp: Instant,
    @ColumnInfo(index = true) val parentCommitId: Long? = null,
    @ColumnInfo(index = true) val recipeId: Long
)