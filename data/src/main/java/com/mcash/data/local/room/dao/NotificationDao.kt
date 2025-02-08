package com.mcash.data.local.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.mcash.domain.model.NotificationEntity

@Dao
interface NotificationDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(response: NotificationEntity)

    @Query("SELECT * FROM notificationentity WHERE read=0")
    suspend fun getNotifications(): List<NotificationEntity>

    @Update
    suspend fun updateNotification(notificationEntity: NotificationEntity)

    @Query("DELETE FROM notificationentity")
    suspend fun clearNotifications()
}