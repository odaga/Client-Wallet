package com.mcash.domain.usecases.transfer

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

class TransferToWalletUseCase @Inject constructor(
    val client: ClientRepository,
    val preferences: PreferenceRepository,
    val utilRepository: UtilRepository
): BaseFlowUseCase<TransferToWalletUseCase.Param, Resource<String>>() {

    data class Param(
        val device_id:String,
        val model:String,
        val customer_account:String,
        val amount:String,
        val transaction_ref:String,
        val transaction_token:String,
    )

    override fun run(param: Param?): Flow<Resource<String>> = flow {
        emit(Resource.Loading)
        try {
            val user = preferences.getDataStoreUser().first()

            param?.let {
                val hashMap = HashMap<String, Any>().apply {
                    this["device_id"] = user.username
                    this["model"] = user.pin
                    this["client_account"] = user.username
                    this["pin"] = user.pin
                    this["customer_account"] = it.customer_account
                    this["amount"] = it.amount
                    this["transaction_ref"] = it.transaction_ref
                    this["transaction_token"] = it.transaction_token
                }

                val response = client.confirmTransferToWallet(hashMap, "Bearer " + user.token)
                emit(Resource.Success(response))
            } ?: throw InvalidParameterException()
        }
        catch (throwable: Throwable) {
            emit(Resource.Error(utilRepository.getNetworkError(throwable)))
        }
    }
}