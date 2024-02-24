package com.gyleedev.githubsearch.data.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.gyleedev.githubsearch.data.database.entity.AccessTime


@Dao
interface AccessTimeDao {
    @Query("SELECT * FROM access_time WHERE github_id = :id")
    fun getTimeByGithubId(id: String): AccessTime

    @Insert
    fun insertTime(accessTime: AccessTime)

    @Delete
    fun deleteTime(accessTime: AccessTime)
}