package com.mcash.domain.usecases.nwsc

import com.mcash.domain.base.BaseFlowUseCase
import com.mcash.domain.model.NwscCustomerEntity
import com.mcash.domain.model.sealed.Resource
import com.mcash.domain.repository.ClientRepository
import com.mcash.domain.repository.PreferenceRepository
import com.mcash.domain.repository.UtilRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import java.security.InvalidParameterException
import javax.inject.Inject

class ValidateNwscCustomerUseCase @Inject constructor(
    private val utilRepository: UtilRepository,
    private val preferenceRepository: PreferenceRepository,
    private val clientRepository: ClientRepository
): BaseFlowUseCase<ValidateNwscCustomerUseCase.Param, Resource<NwscCustomerEntity>>() {

    data class Param(
        val device_id:String,
        val model:String,
        val customer_account:String,
        val customer_phone:String,
        val area:String
    )

    override fun run(param: Param?): Flow<Resource<NwscCustomerEntity>>  = flow {
        emit(Resource.Loading)
        try {
            param?.let {
                val user = preferenceRepository.getDataStoreUser().first()
                val map = HashMap<String, Any>().apply {
                    this["device_id"] = it.device_id
                    this["model"] = it.model
                    this["customer_phone"] = it.customer_phone
                    this["customer_account"] = it.customer_account
                    this["area"] = it.area
                    this["client_account"] = user.username
                    this["pin"] = user.pin
                }

                val response = clientRepository.validateNwscCustomer(map, "Bearer " + user.token)
                emit(Resource.Success(response))

            } ?: throw InvalidParameterException()
        } catch (e: Throwable) {
            emit(Resource.Error(utilRepository.getNetworkError(e)))
        }
    }
}