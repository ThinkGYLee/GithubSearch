package com.gyleedev.githubsearch.data.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.Instant

@Entity(tableName = "access_time")
data class AccessTime(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long,
    @ColumnInfo(name = "github_id")
    val githubId: String,
    @ColumnInfo(name = "access_time")
    val accessTime: Instant
)
