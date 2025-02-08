package com.mcash.domain.usecases.fuelSave

import com.mcash.domain.base.BaseFlowUseCase
import com.mcash.domain.model.FuelSaveTransaction
import com.mcash.domain.model.ValidateFuelPurchaseEntity
import com.mcash.domain.model.sealed.Resource
import com.mcash.domain.repository.FuelSaveRepository
import com.mcash.domain.repository.PreferenceRepository
import com.mcash.domain.repository.UtilRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import javax.inject.Inject


class ConfirmFuelSavePurchaseUseCase @Inject constructor(
    private val fuelSaveRepository: FuelSaveRepository,
    private val preferenceRepository: PreferenceRepository,
    private val utilRepository: UtilRepository
) : BaseFlowUseCase<ConfirmFuelSavePurchaseUseCase.Param, Resource<FuelSaveTransaction>>() {

    data class Param(
        val deviceId: String,
        val model: String,
        val imei: String,
        val longitude: String,
        val latitude: String,
        val pin: String,
        val branchCode: String,
        val amount: String,
        val fuelTypeId: String,
        val indicator: String
    )

    override fun run(param: Param?): Flow<Resource<FuelSaveTransaction>> = flow {
        try {
            emit(Resource.Loading)
            val authToken = preferenceRepository.getDataStoreUser().first().token
            val user = preferenceRepository.getDataStoreUser().first()
            param?.let {
                val data = HashMap<String, Any>().apply {
                    this["device_id"] = it.deviceId
                    this["model"] = it.model
                    this["imei"] = it.imei
                    this["longitude"] = it.longitude
                    this["latitude"] = it.latitude
                    this["pin"] = it.pin
                    this["driver_code"] = user.phone
                    this["branch_code"] = it.branchCode
                    this["customer_phone"] = user.phone
                    this["amount"] = it.amount
                    this["client_account"] = user.username
                    this["fuel_type_id"] = it.fuelTypeId
                    this["indicator"] = it.indicator
                }
                val confirmationData =
                    fuelSaveRepository.confirmKaftaPurchase(data, "Bearer $authToken")
                emit(Resource.Success(confirmationData))
            }
        } catch (t: Throwable) {
            emit(Resource.Error(utilRepository.getNetworkError(t)))
        }
    }


}