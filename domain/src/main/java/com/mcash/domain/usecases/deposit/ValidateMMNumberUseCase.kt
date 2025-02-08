package com.mcash.domain.usecases.deposit

import com.mcash.domain.base.BaseFlowUseCase
import com.mcash.domain.model.DepositEntity
import com.mcash.domain.model.sealed.Resource
import com.mcash.domain.repository.ClientRepository
import com.mcash.domain.repository.PreferenceRepository
import com.mcash.domain.repository.UtilRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import java.security.InvalidParameterException
import javax.inject.Inject

class ValidateMMNumberUseCase @Inject constructor(
    val client: ClientRepository,
    val preferences: PreferenceRepository,
    val utilRepository: UtilRepository
): BaseFlowUseCase<ValidateMMNumberUseCase.Param, Resource<DepositEntity>>() {

    data class Param(
        var device_id:String,
        var model:String,
        var mobile_account: String,
        var amount:String
    )

    override fun run(param: Param?): Flow<Resource<DepositEntity>> = flow {
        emit(Resource.Loading)

        try {
            val user = preferences.getDataStoreUser().first()

            param?.let {
                val hashMap = HashMap<String, Any>().apply {
                    this["device_id"] = it.device_id
                    this["model"] = it.model
                    this["client_account"] = user.username
                    this["mobile_account"] = it.mobile_account
                    this["amount"] = it.amount
                    this["pin"] = user.pin
                }

                val response = client.validateDepositToWallet(hashMap, "Bearer " + user.token)
                emit(Resource.Success(response))
            } ?: throw InvalidParameterException()
        }
        catch (throwable: Throwable) {
            emit(Resource.Error(utilRepository.getNetworkError(throwable)))
        }
    }
}