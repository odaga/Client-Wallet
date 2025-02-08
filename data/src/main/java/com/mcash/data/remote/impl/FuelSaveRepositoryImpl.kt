package com.mcash.data.remote.impl

import com.mcash.data.remote.services.ClientService
import com.mcash.domain.model.Branch
import com.mcash.domain.model.FuelSaveDriverEntity
import com.mcash.domain.model.FuelSaveTransaction
import com.mcash.domain.model.FuelType
import com.mcash.domain.model.ValidateFuelPurchaseEntity
import com.mcash.domain.repository.FuelSaveRepository
import javax.inject.Inject

class FuelSaveRepositoryImpl @Inject constructor(
    private val service: ClientService
) : FuelSaveRepository {

    override suspend fun getBranch(branchId: String, authHeader: String): Branch {
        return try {
            val fuelTypes = ArrayList<FuelType>()
            val response = service.getBranchDetails(branchId, authHeader)
            println(response.data)
            val data = response.data

            println("=== branch details before: $data")
            response.data.paymentItems.forEach {
                val fuelType = FuelType(
                    id = it.id,
                    fuelTypeId = it.fuelTypeId,
                    fuelTypeName = it.fuelTypeName,
                    stationId = it.stationId,
                    description = it.description,
                    branchId = it.branchId,
                    pricePerLiter = it.pricePerLiter,
                    currency = it.currency,
                    priceDate = it.priceDate,
                    updatedAt = it.updatedAt
                )
                fuelTypes.add(fuelType)

                println("=== fuel type: $fuelTypes")
            }
            val branch = Branch(
                id = data.id,
                branchName = data.branchName,
                stationId = data.stationId.toString(),
                code = data.code.toString(),
                stationName = data.station.stationName,
                addressInfo = data.addressInfo ?: "",
                pricing = fuelTypes
            )
            println("=== branch Details: $branch")
            branch
        } catch (t: Throwable) {
            throw t
        }
    }

    override suspend fun validateKaftaPurchase(
        data: HashMap<String, Any>,
        authHeader: String
    ): ValidateFuelPurchaseEntity {
        return try {
            val validationData = service.validateKaftaPurchase(data, authHeader).data

            val transaction = ValidateFuelPurchaseEntity(
                id = validationData.id,
                driverCode = validationData.driverCode,
                branchCode = validationData.branchCode,
                fuelTypeId = validationData.fuelTypeId,
                amount = validationData.amount,
                priceDate = validationData.priceDate,
                pricePerLiter = validationData.pricePerLiter,
                status = validationData.status,
                stationId = validationData.stationId,
                associationId = validationData.associationId,
                indicator = validationData.indicator,
                literEquivalent = validationData.literEquivalent
            )
            transaction

        } catch (t: Throwable) {
            throw t
        }
    }

    override suspend fun confirmKaftaPurchase(
        data: HashMap<String, Any>,
        authHeader: String
    ): FuelSaveTransaction {
        return try {
            val result = service.confirmKaftaPurchase(data, authHeader).data
            val transaction = FuelSaveTransaction(
                id = result.id ?: "",
                transactionRef = result.transactionRef ?: "",
                driverCode = result.driverCode ?: "",
                branchCode = result.branchCode ?: "",
                fuelTypeId = result.fuelTypeId ?: "",
                amount = result.amount ?: "",
                priceDate = result.priceDate ?: "",
                pricePerLiter = result.pricePerLiter ?: "",
                status = result.status ?: "",
                stationId = result.stationId ?: "",
                associationId = result.associationId ?: "",
                literEquivalent = result.literEquivalent ?: "",
                validatedTransactionId = result.validatedTransactionId ?: "",
                commissionStatus = result.commissionStatus ?: ""
            )
            transaction
        } catch (t: Throwable) {
            throw t
        }
    }

    override suspend fun getFuelSaveDriver(
        driverCode: String,
        authHeader: String
    ): FuelSaveDriverEntity {
        return try {
            val response = service.getDriver(driverCode, authHeader).data
            val fuelSaveDriver = FuelSaveDriverEntity(
                id = response.id ?: "",
                associationId = response.associationId ?: "",
                stageId = response.stageId ?: "",
                fullName = response.fullName ?: "",
                userId = response.userId ?: "",
                slug = response.slug ?: "",
                phoneNumber = response.phoneNumber ?: "",
                dob = response.dob ?: "",
                gender = response.gender ?: "",
                licenseNumber = response.licenseNumber ?: "",
                licenseExpiryDate = response.licenseExpiryDate ?: "",
                createdById = response.createdById ?: "",
                email = response.email ?: "",
                associationName = response.associationName ?: ""
            )
            fuelSaveDriver
        } catch (t: Throwable) {
            throw t
        }
    }

    override suspend fun registerFuelSaveDriver(data: HashMap<String, Any>, authHeader: String) {
        return try {
            val response = service.registerDriver(data, authHeader).data

        } catch (t: Throwable) {
            throw t
        }
    }

    override suspend fun getLoyaltyPoints(
        data: HashMap<String, Any>,
        authHeader: String
    ): Float {
        return try {
            val response = service.getLoyaltyPoints(data, authHeader).data
            response.points!!.toFloat()
        } catch (t: Throwable) {
            throw t
        }
    }
}