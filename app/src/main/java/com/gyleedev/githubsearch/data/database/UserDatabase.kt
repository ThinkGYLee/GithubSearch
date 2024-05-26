package com.gyleedev.githubsearch.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.gyleedev.githubsearch.data.database.dao.AccessTimeDao
import com.gyleedev.githubsearch.data.database.dao.ReposDao
import com.gyleedev.githubsearch.data.database.dao.UserDao
import com.gyleedev.githubsearch.data.database.entity.AccessTime
import com.gyleedev.githubsearch.data.database.entity.ReposEntity
import com.gyleedev.githubsearch.data.database.entity.UserEntity

@Database(
    entities = [
        UserEntity::class,
        ReposEntity::class,
        AccessTime::class,
    ],
    version = 1,
    exportSchema = true,
)
@TypeConverters(DbTypeConverters::class)
abstract class UserDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun reposDao(): ReposDao
    abstract fun accessTimeDao(): AccessTimeDao
}
