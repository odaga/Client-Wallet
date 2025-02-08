package com.mcash.data.di

import com.mcash.data.impl.PagingRepositoryImpl
import com.mcash.data.local.impl.LocalRepositoryImpl
import com.mcash.data.local.impl.PreferenceRepositoryImpl
import com.mcash.data.remote.impl.ClientRepositoryImpl
import com.mcash.data.remote.impl.FuelSaveRepositoryImpl
import com.mcash.data.remote.impl.KycRepositoryImpl
import com.mcash.data.remote.impl.UtilRepositoryImpl
import com.mcash.domain.repository.*
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface RepositoryModule {

    @Singleton
    @Binds
    fun bindRepository(impl: ClientRepositoryImpl): ClientRepository

    @Singleton
    @Binds
    fun bindLocalRepository(impl: LocalRepositoryImpl): LocalRepository

    @Singleton
    @Binds
    fun bindUtilRepository(impl: UtilRepositoryImpl): UtilRepository

    @Singleton
    @Binds
    fun bindPagingRepository(impl: PagingRepositoryImpl): PagingRepository

    @Singleton
    @Binds
    fun bindPreferenceRepository(impl: PreferenceRepositoryImpl): PreferenceRepository

    @Singleton
    @Binds
    fun bindFuelSaveRepository(impl: FuelSaveRepositoryImpl): FuelSaveRepository

    @Singleton
    @Binds
    fun bindKycRepository(impl: KycRepositoryImpl): KycRepository
}