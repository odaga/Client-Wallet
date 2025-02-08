package com.mcash.domain.usecases.utility.voice

import com.mcash.domain.base.BaseFlowUseCase
import com.mcash.domain.model.ValidateUtilityEntity
import com.mcash.domain.model.sealed.Resource
import com.mcash.domain.repository.ClientRepository
import com.mcash.domain.repository.PreferenceRepository
import com.mcash.domain.repository.UtilRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import java.security.InvalidParameterException
import javax.inject.Inject

class ValidateVoiceUseCase @Inject constructor(
    val clientRepository: ClientRepository,
    val preferenceRepository: PreferenceRepository,
    val utilRepository: UtilRepository
) : BaseFlowUseCase<ValidateVoiceUseCase.Param, Resource<ValidateUtilityEntity>>(){

    data class Param(
        var type:String,
        var device_id:String,
        var model:String,
        var customer_account:String,
        var package_id:String,
        var amount:String
    )

    override fun run(param: Param?): Flow<Resource<ValidateUtilityEntity>>  = flow {
        emit(Resource.Loading)
        try {
            param?.let {
                val user = preferenceRepository.getDataStoreUser().first()
                val map = hashMapOf<String, Any>().apply {
                    this["device_id"] = it.device_id
                    this["model"] = it.model
                    this["customer_account"] = it.customer_account
                    this["package_id"] = it.package_id
                    this["amount"] = it.amount
                    this["client_account"] = user.username
                    this["pin"] = user.pin
                }

                val response  = clientRepository.validateVoiceCustomer(it.type, map, "Bearer " + user.token)
                emit(Resource.Success(response))

            } ?: throw InvalidParameterException()

        } catch (t:Throwable) {
            emit(Resource.Error(utilRepository.getNetworkError(t)))
        }
    }
}