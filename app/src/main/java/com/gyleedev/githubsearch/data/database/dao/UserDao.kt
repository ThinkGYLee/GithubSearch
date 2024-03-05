package com.gyleedev.githubsearch.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.gyleedev.githubsearch.data.database.entity.UserEntity

@Dao
interface UserDao {

    @Query("SELECT * FROM user LIMIT 10 OFFSET (:page-1)*10")
    fun getUsers(page: Int): List<UserEntity>

    @Query("SELECT * FROM user WHERE favorite = :favorite LIMIT 10 OFFSET (:page-1)*10")
    fun getFavorite(page: Int, favorite: Boolean = true): List<UserEntity>

    @Query("SELECT * FROM user WHERE user_id = :id  COLLATE NOCASE")
    fun getUser(id: String): UserEntity

    @Query("SELECT * FROM user WHERE user_id = :userId COLLATE NOCASE")
    fun getUserByGithubId(userId: String): UserEntity

    @Insert
    fun insertUser(user: UserEntity): Long

    @Update
    fun updateUser(user: UserEntity)
}