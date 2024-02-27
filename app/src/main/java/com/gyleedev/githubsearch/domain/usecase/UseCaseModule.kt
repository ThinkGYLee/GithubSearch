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
    fun provideGetUserUseCase(repository: GitHubRepository) = GetUserUseCase(repository)

    @Provides
    @Singleton
    fun provideGetRepoUseCase(repository: GitHubRepository) = GetRepositoryUseCase(repository)

    @Provides
    @Singleton
    fun provideGetBothUseCase(
        repository: GitHubRepository
    ) = DetailFeedUseCase(repository)
}