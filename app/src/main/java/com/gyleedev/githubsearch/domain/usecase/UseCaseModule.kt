package com.gyleedev.githubsearch.domain.usecase

import com.gyleedev.githubsearch.data.repository.GitHubRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class UseCaseModule {

    @Provides
    @Singleton
    fun provideHomeGetUsersUseCase(repository: GitHubRepository) = HomeGetUsersUseCase(repository)

    @Provides
    @Singleton
    fun providesHomeSearchUserUseCase(repository: GitHubRepository) =
        HomeSearchUserUseCase(repository)

    @Provides
    @Singleton
    fun providesDetailGetFeedUseCase(repository: GitHubRepository) =
        DetailGetFeedUseCase(repository)

    @Provides
    @Singleton
    fun providesDetailUpdateFavoriteStatusUseCase(repository: GitHubRepository) =
        DetailUpdateFavoriteStatusUseCase(repository)

    @Provides
    @Singleton
    fun providesFavoriteGetFavoritesUseCase(repository: GitHubRepository) =
        FavoriteGetFavoritesUseCase(repository)

    @Provides
    @Singleton
    fun providesFavoriteUpdateFavoriteStatusUseCase(repository: GitHubRepository) =
        FavoriteUpdateFavoriteStatusUseCase(repository)
}