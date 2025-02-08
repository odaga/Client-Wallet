package com.mcash.domain.usecases.fuelSave

import com.mcash.domain.base.BaseFlowUseCase
import com.mcash.domain.model.sealed.Resource
import com.mcash.domain.repository.FuelSaveRepository
import com.mcash.domain.repository.PreferenceRepository
import com.mcash.domain.repository.UtilRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetDriverLoyaltyPointsUseCase @Inject constructor(
    private val fuelSaveRepository: FuelSaveRepository,
    private val preferenceRepository: PreferenceRepository,
    private val utilRepository: UtilRepository
) : BaseFlowUseCase<GetDriverLoyaltyPointsUseCase.Param, Resource<Float>>() {

    data class Param(
        val deviceId: String,
        val model: String,
        val imei: String,
        val longitude: String,
        val latitude: String,
    )

    override fun run(param: Param?): Flow<Resource<Float>> = flow {
        try {
            emit(Resource.Loading)
            val user = preferenceRepository.getDataStoreUser().first()
            param?.let {
                val data = HashMap<String, Any>().apply {
                    this["device_id"] = param.deviceId
                    this["model"] = it.model
                    this["imei"] = it.imei
                    this["longitude"] = it.longitude
                    this["latitude"] = it.latitude
                    this["pin"] = user.pin
                    this["account_number"] = user.phone
                }
                val loyaltyPoints =
                    fuelSaveRepository.getLoyaltyPoints(data, "Bearer ${user.token}")
                emit(Resource.Success(loyaltyPoints))
            }
        } catch (t: Throwable) {
            emit(Resource.Error(utilRepository.getNetworkError(t)))
        }
    }
}