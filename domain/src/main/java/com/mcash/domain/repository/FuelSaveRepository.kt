package com.mcash.domain.repository

import com.mcash.domain.model.Branch
import com.mcash.domain.model.FuelSaveDriverEntity
import com.mcash.domain.model.FuelSaveTransaction
import com.mcash.domain.model.ValidateFuelPurchaseEntity

interface FuelSaveRepository {

    suspend fun getBranch(branchId: String, authHeader: String): Branch

    suspend fun validateKaftaPurchase(
        data: HashMap<String, Any>,
        authHeader: String
    ): ValidateFuelPurchaseEntity

    suspend fun confirmKaftaPurchase(
        data: HashMap<String, Any>,
        authHeader: String
    ): FuelSaveTransaction

    suspend fun getFuelSaveDriver(driverCode: String, authHeader: String): FuelSaveDriverEntity

    suspend fun registerFuelSaveDriver(data: HashMap<String, Any>, authHeader: String)

    suspend fun getLoyaltyPoints(data: HashMap<String, Any>, authHeader: String): Float

}

