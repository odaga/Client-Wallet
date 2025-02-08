package com.mcash.data.remote.di

import com.mcash.data.qualifiers.ClientApiQualifier
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class OkhttpModule {

    @Provides
    fun getHttpLogging(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            this.level  = HttpLoggingInterceptor.Level.BODY
        }
    }

    @Provides
    @Singleton
    fun getOkHttpClient(
        interceptor: HttpLoggingInterceptor
    ): OkHttpClient {
        return OkHttpClient.Builder().apply {
            this.addInterceptor(interceptor)
                .connectTimeout(1000, TimeUnit.SECONDS)
                .readTimeout(1000,TimeUnit.SECONDS)
        }.build()
    }

}