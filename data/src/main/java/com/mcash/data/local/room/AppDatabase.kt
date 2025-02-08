package com.mcash.data.local.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.mcash.data.local.converters.Converters
import com.mcash.data.local.model.LocalUser
import com.mcash.data.local.room.dao.*
import com.mcash.data.remote.model.FAQ
import com.mcash.domain.model.*

@TypeConverters(Converters::class, DataTypeConverter::class)
@Database(
    entities = [
        Contact::class,
        Beneficiary::class,
        LocalUser::class,
        TvEntity::class,
        VoiceEntity::class,
        DataEntity::class,
        Data::class,
        FAQ::class,
        NotificationEntity::class,
        FormMapEntity::class,
        CardInfoEntity::class
    ],
    version = 3,
    exportSchema = false
)

abstract class AppDatabase : RoomDatabase() {
    abstract fun beneficiaryDao(): BeneficiaryDao
    abstract fun contactDao(): ContactDao
    abstract fun userDao(): UserDao
    abstract fun tvDao(): TvDao
    abstract fun voiceDao(): VoiceDao
    abstract fun dataDao(): DataDao
    abstract fun faqDao(): FAQDao
    abstract fun notificationDao(): NotificationDao
    abstract fun formMapDao(): FormMapDao
    abstract fun idCardDao(): IdCardDao
}