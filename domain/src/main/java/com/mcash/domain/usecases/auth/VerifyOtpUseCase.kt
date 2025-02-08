package com.mcash.domain.usecases.auth

import android.util.Log
import com.mcash.domain.base.BaseFlowUseCase
import com.mcash.domain.model.UserEntity
import com.mcash.domain.model.sealed.Resource
import com.mcash.domain.repository.ClientRepository
import com.mcash.domain.repository.PreferenceRepository
import com.mcash.domain.repository.UtilRepository
import kotlinx.coroutines.flow.flow
import java.security.InvalidParameterException
import javax.inject.Inject

class VerifyOtpUseCase @Inject constructor(
    private val utilRepository: UtilRepository,
    private val client: ClientRepository,
    private val preferences: PreferenceRepository,
): BaseFlowUseCase<VerifyOtpUseCase.Param, Resource<UserEntity>>() {
    override fun run(param: Param?)= flow {
        emit(Resource.Loading)
        try {
            param?.let {
                val hashMap = HashMap<String, Any>().apply {
                    this["mobile"] = it.phone
                    this["pinCode"] = it.pin
                    this["otp"] = it.otp
                    this["device_id"] = it.device_id
                    this["model"] = it.model
                    this["device_token"] = it.device_token
                }
                val response = client.verifyUserOTP(hashMap)
                val user = UserEntity(id = response.id, username = response.username, phone = response.phone, name = response.fullName, pin = it.pin, email = response.email, token = response.token, ip = response.ip, sip = response.sip )
                preferences.saveUserToDatastore(user)

                emit(Resource.Success(user))
            } ?: throw InvalidParameterException()

        } catch (throwable: Throwable) {
            Log.d("Error", "${throwable.message}")
            emit(Resource.Error(utilRepository.getNetworkError(throwable)))
        }
    }

    data class Param(
        val phone: String,
        val pin: String,
        val otp: String,
        var device_id:String,
        var model:String,
        var device_token:String
    )

}