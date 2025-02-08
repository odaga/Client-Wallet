package com.mcash.data.local.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.mcash.domain.model.CardInfoEntity

@Dao
interface IdCardDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertIdCard(cardInfoEntity: CardInfoEntity)

    @Query("SELECT * FROM CardInfoEntity where nin = :nin")
    suspend fun getCardInfo(nin: String): CardInfoEntity

    @Query("DELETE FROM CardInfoEntity")
    suspend fun clearCardInfo()
}