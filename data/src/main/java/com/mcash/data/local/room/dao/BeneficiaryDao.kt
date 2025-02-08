package com.mcash.data.local.room.dao

import androidx.room.*
import com.mcash.domain.model.Beneficiary
import kotlinx.coroutines.flow.Flow

@Dao
interface BeneficiaryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBeneficiary(beneficiary: Beneficiary)

    @Query("SELECT * FROM beneficiary")
    fun getBeneficiaries(): Flow<List<Beneficiary>>

    @Delete
    suspend fun deleteBeneficiary(contact: Beneficiary)

    @Query("DELETE FROM beneficiary")
    suspend fun clearBeneficiaries()

    @Query("SELECT * FROM beneficiary WHERE name LIKE :search")
    fun searchBeneficiary(search: String): Flow<List<Beneficiary>>
}