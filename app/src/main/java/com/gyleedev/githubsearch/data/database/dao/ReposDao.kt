package com.gyleedev.githubsearch.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.gyleedev.githubsearch.data.database.entity.ReposEntity
import com.gyleedev.githubsearch.data.database.entity.UserEntity

@Dao
interface ReposDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRepos(repos: List<ReposEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRepo(repo:ReposEntity)

    @Query("SELECT * FROM repos WHERE user_github_id = :githubId")
    suspend fun getReposByGithubId(githubId: String): List<ReposEntity>

    @Query("SELECT * FROM repos WHERE favorite = :favorite")
    fun getFavoriteRepos(favorite: Boolean): List<ReposEntity>

}