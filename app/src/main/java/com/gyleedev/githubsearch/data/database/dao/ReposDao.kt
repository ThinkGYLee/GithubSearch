package com.gyleedev.githubsearch.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.gyleedev.githubsearch.data.database.entity.ReposEntity

@Dao
interface ReposDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRepos(repos: List<ReposEntity>)

    @Query("SELECT * FROM repos WHERE user_github_id = :githubId  COLLATE NOCASE")
    suspend fun getReposByGithubId(githubId: String): List<ReposEntity>

    @Query("DELETE FROM repos WHERE user_github_id = :githubId  COLLATE NOCASE")
    suspend fun deleteRepos(githubId: String)

    @Query("DELETE FROM repos")
    fun resetRepos()
}
