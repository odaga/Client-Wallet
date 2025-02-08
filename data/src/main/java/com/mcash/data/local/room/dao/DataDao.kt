package com.mcash.data.local.room.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.mcash.domain.model.Data
import com.mcash.domain.model.DataEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DataDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDataBundle(tv: Data)

    @Delete
    suspend fun deleteDataBundle(tv: Data)

    @Query("DELETE FROM data")
    suspend fun clearDataBundle()

    @Query("SELECT * FROM data WHERE networkType LIKE :search")
    fun getDataBundleByType(search: String): Data
}