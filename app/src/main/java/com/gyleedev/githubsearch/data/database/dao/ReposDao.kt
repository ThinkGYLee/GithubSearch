package com.gyleedev.githubsearch.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.gyleedev.githubsearch.data.database.entity.ReposEntity

@Dao
interface ReposDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRepos(repos: List<ReposEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRepo(repo:ReposEntity)

}