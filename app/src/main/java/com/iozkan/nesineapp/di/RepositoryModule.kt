package com.iozkan.nesineapp.di

import com.iozkan.nesineapp.data.repository.PostRepositoryImpl
import com.iozkan.nesineapp.domain.repository.PostRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Binds the repository implementation to its domain interface so consumers can
 * depend on the abstraction (dependency inversion).
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindPostRepository(impl: PostRepositoryImpl): PostRepository
}
