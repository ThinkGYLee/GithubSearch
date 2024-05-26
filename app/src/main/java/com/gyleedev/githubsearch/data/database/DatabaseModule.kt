package com.gyleedev.githubsearch.data.database

import android.content.Context
import androidx.room.Room
import com.gyleedev.githubsearch.data.database.dao.AccessTimeDao
import com.gyleedev.githubsearch.data.database.dao.ReposDao
import com.gyleedev.githubsearch.data.database.dao.UserDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Singleton
    @Provides
    fun providesUserDatabase(@ApplicationContext context: Context): UserDatabase {
        return Room.databaseBuilder(
            context,
            UserDatabase::class.java,
            "database",
        ).build()
    }

    @Singleton
    @Provides
    fun providesUserDao(userDatabase: UserDatabase): UserDao = userDatabase.userDao()

    @Singleton
    @Provides
    fun providesReposDao(userDatabase: UserDatabase): ReposDao = userDatabase.reposDao()

    @Singleton
    @Provides
    fun providesAccessTimeDao(userDatabase: UserDatabase): AccessTimeDao = userDatabase.accessTimeDao()
}
