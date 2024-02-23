package com.gyleedev.githubsearch.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.gyleedev.githubsearch.data.database.dao.AccessTimeDao
import com.gyleedev.githubsearch.data.database.dao.ReposDao
import com.gyleedev.githubsearch.data.database.dao.UserDao
import com.gyleedev.githubsearch.data.database.entity.ReposEntity
import com.gyleedev.githubsearch.data.database.entity.UserEntity
import com.gyleedev.githubsearch.data.database.entity.AccessTime

@Database(
    entities = [
        UserEntity::class,
        ReposEntity::class,
        AccessTime::class
    ],
    version = 1,
    exportSchema = true
)

abstract class UserDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun reposDao(): ReposDao
    abstract fun accessTimeDao(): AccessTimeDao
}