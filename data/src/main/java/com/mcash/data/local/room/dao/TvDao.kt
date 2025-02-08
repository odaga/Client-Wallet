package com.mcash.data.local.room.dao

import androidx.room.*
import com.mcash.domain.model.TvEntity

@Dao
interface TvDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTvPackage(tv: TvEntity)

    @Query("SELECT * FROM tv")
    fun getTvPackages(): List<TvEntity>

    @Delete
    suspend fun deleteTvPackage(tv: TvEntity)

    @Query("DELETE FROM tv")
    suspend fun clearTvPackage()

    @Query("SELECT * FROM tv WHERE code LIKE :search")
    fun searchTvPackage(search: String): List<TvEntity>

    @Query("SELECT * FROM tv WHERE  provider= :type")
    fun getTvPackageByProvider(type: String): List<TvEntity>
}