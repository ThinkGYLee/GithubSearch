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
    fun getTimeByGithubId(id: String): AccessTimeEntity?

    @Insert
    fun insertTime(accessTimeEntity: AccessTimeEntity)

    @Delete
    fun deleteTime(accessTimeEntity: AccessTimeEntity)

    @Update
    fun updateTime(accessTimeEntity: AccessTimeEntity)

    @Query("DELETE FROM access_time")
    fun resetAccessTime()
}
