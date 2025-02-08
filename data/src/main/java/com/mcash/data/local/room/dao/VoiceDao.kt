package com.mcash.data.local.room.dao

import androidx.room.*
import com.mcash.domain.model.VoiceEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface VoiceDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVoiceBundle(tv: VoiceEntity)

    @Query("SELECT * FROM voice")
    fun getVoiceBundle(): List<VoiceEntity>

    @Delete
    suspend fun deleteVoiceBundle(tv: VoiceEntity)

    @Query("DELETE FROM voice")
    suspend fun clearVoiceBundle()

    @Query("SELECT * FROM voice WHERE code LIKE :search")
    fun searchVoiceBundle(search: String): Flow<List<VoiceEntity>>
}