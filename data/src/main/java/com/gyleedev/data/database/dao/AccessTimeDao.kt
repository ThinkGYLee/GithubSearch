package com.gyleedev.data.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.gyleedev.data.database.entity.AccessTimeEntity

@Dao
interface AccessTimeDao {
    @Query("SELECT * FROM access_time WHERE github_id = :id  COLLATE NOCASE")
    suspend fun getTimeByGithubId(id: String): AccessTimeEntity?

    @Upsert
    suspend fun upsertAccessTime(accessTimeEntity: AccessTimeEntity)

    @Query("DELETE FROM access_time")
    suspend fun resetAccessTime()
}
