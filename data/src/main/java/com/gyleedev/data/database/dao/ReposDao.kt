package com.gyleedev.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.gyleedev.data.database.entity.ReposEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ReposDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRepos(repos: List<ReposEntity>)

    @Query("SELECT * FROM repos WHERE user_github_id = :githubId  COLLATE NOCASE")
    suspend fun getReposByGithubId(githubId: String): List<ReposEntity>

    @Query("SELECT * FROM repos WHERE user_github_id = :githubId  COLLATE NOCASE")
    fun getReposByGithubIdWithFlow(githubId: String): Flow<List<ReposEntity>>

    @Query("DELETE FROM repos WHERE user_github_id = :githubId  COLLATE NOCASE")
    suspend fun deleteRepos(githubId: String)

    @Query("DELETE FROM repos")
    suspend fun resetRepos()
}
