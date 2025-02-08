package com.mcash.domain.model

import androidx.room.PrimaryKey
import java.io.Serializable


data class FuelSaveTransaction(
    @PrimaryKey(autoGenerate = false)
    val id: String,
    val transactionRef: String?,
    val driverCode: String?,
    val branchCode: String?,
    val fuelTypeId: String?,
    val amount: String?,
    val pricePerLiter: String?,
    val priceDate: String?,
    val status: String?,
    val commissionStatus: String?,
    val stationId: String?,
    val associationId: String?,
    val validatedTransactionId: String?,
    val literEquivalent: String?
)

data class ValidateFuelPurchaseEntity(
    @PrimaryKey(autoGenerate = false)
    val id: String,
    val driverCode: String?,
    val branchCode: String?,
    val fuelTypeId: String?,
    val amount: String?,
    val pricePerLiter: String?,
    val priceDate: String?,
    val status: String?,
    val stationId: String?,
    val associationId: String?,
    val indicator: String?,
    val literEquivalent: String?
)


data class Branch(
    val id: String,
    val branchName: String?,
    val code: String?,
    val stationId: String?,
    val stationName: String,
    val addressInfo: String?,
    val pricing: List<FuelType>
) : Serializable


data class Station(
    val id: String?,
    val stationName: String?,
    val slug: String?,
    val addressInfo: String?,
    val contactEmail: String?,
    val cyclosUsername: String?,
    val phoneNumber: String?,
    val isActive: String?,
    val franchizeId: String?,
    val createdById: String?,
    val activationDate: String?,
)

data class FuelType(
    val id: String,
    val fuelTypeId: String?,
    val fuelTypeName: String?,
    val stationId: String?,
    val description: String?,
    val branchId: String?,
    val pricePerLiter: String?,
    val currency: String?,
    val priceDate: String?,
    val updatedAt: String?
)


data class FuelSaveDriverEntity(
    val id: String,
    val associationId: String,
    val stageId: String,
    val fullName: String,
    val userId: String,
    val slug: String,
    val phoneNumber: String,
    val dob: String,
    val gender: String,
    val licenseNumber: String,
    val licenseExpiryDate: String,
    val createdById: String,
    val email: String,
    val associationName: String
)