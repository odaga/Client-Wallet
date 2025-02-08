package com.mcash.data.local.di

import android.content.Context
import androidx.room.Room
import com.mcash.data.local.room.AppDatabase
import com.mcash.data.local.utils.LocalConstants.APP_DATABASE_DB
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideMcashDatabase(
        @ApplicationContext app: Context
    ) = Room.databaseBuilder(
        app,
        AppDatabase::class.java,
        APP_DATABASE_DB
    ).fallbackToDestructiveMigration()
        .allowMainThreadQueries()
        .build()

    @Provides
    fun provideBeneficiaryDao(database: AppDatabase) = database.beneficiaryDao()


    @Provides
    fun provideContactDao(database: AppDatabase) = database.contactDao()

    @Provides
    fun provideUserDao(database: AppDatabase) = database.userDao()

    @Provides
    fun provideTVDao(database: AppDatabase) = database.tvDao()

    @Provides
    fun provideVoiceDao(database: AppDatabase) = database.voiceDao()

    @Provides
    fun provideDataDao(database: AppDatabase) = database.dataDao()

    @Provides
    fun provideFAQDao(database: AppDatabase) = database.faqDao()

    @Provides
    fun provideNotificationDao(database: AppDatabase) = database.notificationDao()

    @Provides
    fun providesFormMapDao(database: AppDatabase) = database.formMapDao()

    @Provides
    fun providesIdCardDao(database: AppDatabase) = database.idCardDao()
}