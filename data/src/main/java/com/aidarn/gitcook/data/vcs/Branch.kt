package com.aidarn.gitcook.data.vcs

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import javax.annotation.concurrent.Immutable


@Immutable
@Entity(
    tableName = "branches",
    foreignKeys = [
        ForeignKey(
            entity = Commit::class,
            parentColumns = ["commitId"],
            childColumns = ["commitId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Branch(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    @ColumnInfo(index = true) val commitId: Long
)
