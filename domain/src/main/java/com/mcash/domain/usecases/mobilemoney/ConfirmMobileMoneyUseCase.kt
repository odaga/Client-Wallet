package com.mcash.domain.usecases.mobilemoney

import com.mcash.domain.base.BaseFlowUseCase
import com.mcash.domain.model.sealed.Resource
import com.mcash.domain.repository.ClientRepository
import com.mcash.domain.repository.PreferenceRepository
import com.mcash.domain.repository.UtilRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import java.security.InvalidParameterException
import javax.inject.Inject

class ConfirmMobileMoneyUseCase @Inject constructor(
    private val clientRepository: ClientRepository,
    val utilRepository: UtilRepository,
    val preferenceRepository: PreferenceRepository

) : BaseFlowUseCase<ConfirmMobileMoneyUseCase.Param, Resource<String>>() {

    data class Param(
        val device_id:String,
        val model:String,
        val amount:String,
        val transaction_ref:String,
        val transaction_token:String,
    )

    override fun run(param: Param?): Flow<Resource<String>> = flow {
        emit(Resource.Loading)
        try {
            val user = preferenceRepository.getDataStoreUser().first()

            param?.let {
                val hashMap = HashMap<String, Any>().apply {
                    this["device_id"] = it.device_id
                    this["model"] = user.pin
                    this["client_account"] = user.username
                    this["pin"] = user.pin
                    this["amount"] = it.amount
                    this["transaction_ref"] = it.transaction_ref
                    this["transaction_token"] = it.transaction_token
                }

                val response = clientRepository.confirmTransferToMobileMoney(hashMap, "Bearer " + user.token)
                emit(Resource.Success(response))
            } ?: throw InvalidParameterException()
        }
        catch (throwable: Throwable) {
            emit(Resource.Error(utilRepository.getNetworkError(throwable)))
        }
    }
}