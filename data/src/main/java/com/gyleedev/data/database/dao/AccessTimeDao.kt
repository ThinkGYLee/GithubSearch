package com.gyleedev.data.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.gyleedev.data.database.entity.AccessTimeEntity

@Dao
interface AccessTimeDao {
    @Query("SELECT * FROM access_time WHERE github_id = :id  COLLATE NOCASE")
    suspend fun getTimeByGithubId(id: String): AccessTimeEntity?

    @Insert
    suspend fun insertTime(accessTimeEntity: AccessTimeEntity)

    @Delete
    suspend fun deleteTime(accessTimeEntity: AccessTimeEntity)

    @Update
    suspend fun updateTime(accessTimeEntity: AccessTimeEntity)

    @Query("DELETE FROM access_time")
    suspend fun resetAccessTime()
}
