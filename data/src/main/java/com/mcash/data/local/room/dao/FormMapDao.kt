package com.mcash.data.local.room.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.mcash.domain.model.FormMapEntity

@Dao
interface FormMapDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(hashMaps: FormMapEntity)

    @Query("SELECT * FROM FormMapEntity")
    suspend fun getAllFormMap(): List<FormMapEntity>

    @Query("SELECT * FROM FormMapEntity WHERE formGroupLabel = :formGroupLabel ")
    suspend fun getFormMapByFormLabel(formGroupLabel: String): List<FormMapEntity>

    @Delete
    suspend fun deleteFormMap(formMapEntity: FormMapEntity)
}