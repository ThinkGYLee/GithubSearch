package com.gyleedev.data.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.Instant

@Entity(
    tableName = "access_time",
    indices = [Index(value = ["github_id"], unique = true)],
)
data class AccessTimeEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long,
    @ColumnInfo(name = "github_id")
    val githubId: String,
    @ColumnInfo(name = "access_time")
    val accessTime: Instant,
    @ColumnInfo(name = "is_repo_fetched")
    val isRepoFetched: Boolean,
)
