package com.mcash.domain.usecases.fuelSave

import com.mcash.domain.base.BaseFlowUseCase
import com.mcash.domain.model.ValidateFuelPurchaseEntity
import com.mcash.domain.model.sealed.Resource
import com.mcash.domain.repository.FuelSaveRepository
import com.mcash.domain.repository.PreferenceRepository
import com.mcash.domain.repository.UtilRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.fold
import javax.inject.Inject


class ValidateFuelSavePurchaseUseCase @Inject constructor(
    private val kaftaRepository: FuelSaveRepository,
    val preferenceRepository: PreferenceRepository,
    private val utilRepository: UtilRepository
) : BaseFlowUseCase<ValidateFuelSavePurchaseUseCase.Param, Resource<ValidateFuelPurchaseEntity>>() {

    data class Param(
        val deviceId: String,
        val model: String,
        val imei: String,
        val longitude: String,
        val latitude: String,
        val branchCode: String,
        val amount: String,
        val fuelTypeId: String
    )

    override fun run(param: Param?): Flow<Resource<ValidateFuelPurchaseEntity>> = flow {
        emit(Resource.Loading)
        try {
            val authToken = preferenceRepository.getDataStoreUser().first().token
            val user = preferenceRepository.getDataStoreUser().first()
            param?.let {
                val data = HashMap<String, Any>().apply {
                    this["device_id"] = it.deviceId
                    this["model"] = it.model
                    this["imei"] = it.imei
                    this["longitude"] = it.longitude
                    this["latitude"] = it.latitude
                    this["pin"] = user.pin
                    this["driver_code"] = user.phone
                    this["branch_code"] = it.branchCode
                    this["customer_phone"] = user.phone
                    this["amount"] = it.amount
                    this["client_account"] = user.username
                    this["fuel_type_id"] = it.fuelTypeId
                }
                val validationData =
                    kaftaRepository.validateKaftaPurchase(data, "Bearer $authToken")
                emit(Resource.Success(validationData))
            }

        } catch (t: Throwable) {
            emit(Resource.Error(utilRepository.getNetworkError(t)))
        }
    }


}