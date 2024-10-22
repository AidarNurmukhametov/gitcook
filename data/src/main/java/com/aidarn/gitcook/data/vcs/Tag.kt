package com.aidarn.gitcook.data.vcs

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import javax.annotation.concurrent.Immutable


@Immutable
@Entity(
    tableName = "tags",
    foreignKeys = [
        ForeignKey(
            entity = Commit::class,
            parentColumns = ["commitId"],
            childColumns = ["commitId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Tag(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    @ColumnInfo(index = true) val commitId: Int
)
