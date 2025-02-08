package com.mcash.data.remote.di


import com.mcash.data.qualifiers.ClientApiQualifier
import com.mcash.data.qualifiers.KycServiceQualifier
import com.mcash.data.qualifiers.VersionApiQualifier
import com.mcash.data.remote.services.ClientService
import com.mcash.data.remote.services.KycService
import com.mcash.data.remote.services.VersionService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class ServiceModule {

    @Provides
    @Singleton
    internal fun provideClientService(
        @ClientApiQualifier retrofit: Retrofit
    ): ClientService {
        return retrofit.create(ClientService::class.java)
    }

    @Provides
    @Singleton
    internal fun provideVersionService(
        @VersionApiQualifier retrofit: Retrofit
    ): VersionService {
        return retrofit.create(VersionService::class.java)
    }

    @Provides
    @Singleton
    internal fun providesKycService(
        @KycServiceQualifier retrofit: Retrofit
    ): KycService {
        return retrofit.create(KycService::class.java)
    }
}