package com.mcash.domain.usecases.utility.voice

import com.mcash.domain.base.BaseFlowUseCase
import com.mcash.domain.model.ConfirmUtilityEntity
import com.mcash.domain.model.sealed.Resource
import com.mcash.domain.repository.ClientRepository
import com.mcash.domain.repository.PreferenceRepository
import com.mcash.domain.repository.UtilRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import java.security.InvalidParameterException
import javax.inject.Inject

class ConfirmVoiceUseCase @Inject constructor(
    private val clientRepository: ClientRepository,
    private val preferences: PreferenceRepository,
    private val utilRepository: UtilRepository
) : BaseFlowUseCase<ConfirmVoiceUseCase.Param, Resource<ConfirmUtilityEntity>>() {

    data class Param(
        var type:String,
        var device_id:String,
        var model:String,
        var customer_account: String,
        var package_id:String,
        var amount:String,
        var transaction_ref:String,
        var short_transaction_ref:String,
        var transaction_token:String,
    )


    override fun run(param: Param?): Flow<Resource<ConfirmUtilityEntity>> = flow {
        emit(Resource.Loading)
        try {
            val user = preferences.getDataStoreUser().first()
            param?.let {
                val type = it.type
                val map = HashMap<String, Any>().apply {
                    this["device_id"] = it.device_id
                    this["model"] = it.model
                    this["customer_account"] = it.customer_account
                    this["package_id"] = it.package_id
                    this["amount"] = it.amount
                    this["client_account"] = user.username
                    this["pin"] = user.pin
                    this["transaction_ref"] = it.transaction_ref
                    this["short_transaction_ref"] = it.short_transaction_ref
                    this["transaction_token"] = it.transaction_token
                }

                val response = clientRepository.confirmVoicePayment(type, map, "Bearer " + user.token)
                emit(Resource.Success(response))
            } ?: throw InvalidParameterException()

        } catch (t:Throwable){
            emit(Resource.Error(utilRepository.getNetworkError(t)))
        }
    }


}