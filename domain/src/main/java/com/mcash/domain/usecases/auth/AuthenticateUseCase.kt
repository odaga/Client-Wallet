package com.mcash.domain.usecases.auth

import com.mcash.domain.base.BaseFlowUseCase
import com.mcash.domain.model.sealed.Resource
import com.mcash.domain.repository.ClientRepository
import com.mcash.domain.repository.UtilRepository
import kotlinx.coroutines.flow.flow
import java.security.InvalidParameterException
import javax.inject.Inject

class AuthenticateUseCase @Inject  constructor(
    private val utilRepository: UtilRepository,
    private val client: ClientRepository,
): BaseFlowUseCase<AuthenticateUseCase.Param, Resource<Int>>() {
    override fun run(param: Param?)= flow {
        emit(Resource.Loading)
        try {
            param?.let {
                val hashMap = HashMap<String, Any>().apply {
                    this["mobile"] = it.phone
                    this["pinCode"] = it.pin
                    this["device_id"] = it.device_id
                    this["model"] = it.model
                    this["device_token"] = it.device_token
                }
                val response = client.authenticateUser(hashMap)

                emit(Resource.Success(response))
            } ?: throw InvalidParameterException()

        } catch (throwable: Throwable) {
            emit(Resource.Error(utilRepository.getNetworkError(throwable)))
        }
    }

    data class Param(
        val phone: String,
        val pin: String,
        val device_id:String,
        val model:String,
        var device_token:String
    )

}