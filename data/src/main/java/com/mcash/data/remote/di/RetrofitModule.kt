package com.mcash.data.remote.di

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.mcash.data.qualifiers.ClientApiQualifier
import com.mcash.data.qualifiers.KycServiceQualifier
import com.mcash.data.qualifiers.VersionApiQualifier

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import mcash.data.BuildConfig
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class RetrofitModule {

    @Provides
    @Singleton
    internal fun provideGson(): Gson {
        return GsonBuilder().setLenient().create()
    }

    @Provides
    @Singleton
    internal fun provideGsonConverterFactory(gson: Gson): GsonConverterFactory {
        return GsonConverterFactory.create(gson)
    }


    @Provides
    @Singleton
    @ClientApiQualifier
    internal fun provideClientRetrofit(
        client: OkHttpClient,
        converterFactory: GsonConverterFactory
    ): Retrofit {
        val builder = Retrofit.Builder()
        builder.apply {
            baseUrl(BuildConfig.CLIENT_BASE_URL)
            client(client)
            addConverterFactory(converterFactory)
        }
        return builder.build()
    }

    @Provides
    @Singleton
    @VersionApiQualifier
    internal fun provideClientVersionRetrofit(
        client: OkHttpClient,
        converterFactory: GsonConverterFactory
    ): Retrofit {
        val builder = Retrofit.Builder()
        builder.apply {
            baseUrl(BuildConfig.CLIENT_VERSION_URL)
            client(client)
            addConverterFactory(converterFactory)
        }
        return builder.build()
    }

    @Provides
    @Singleton
    @KycServiceQualifier
    internal fun providesKycServiceRetrofit(
        client: OkHttpClient,
        converterFactory: GsonConverterFactory
    ): Retrofit {
        val baseUrl = "https://newtest.mcash.ug/ekyc/core/public/api/"
        val builder = Retrofit.Builder()
        builder.apply {
            baseUrl(baseUrl)
            client(client)
            addConverterFactory(converterFactory)
        }
        return builder.build()
    }


}