package com.mcash.domain.usecases.auth

import com.mcash.domain.base.BaseFlowUseCase
import com.mcash.domain.model.UserEntity
import com.mcash.domain.model.sealed.Resource
import com.mcash.domain.repository.ClientRepository
import com.mcash.domain.repository.PreferenceRepository
import com.mcash.domain.repository.UtilRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import java.security.InvalidParameterException
import javax.inject.Inject

class ChangePinUseCase @Inject constructor(
    private val utilRepository: UtilRepository,
    private val clientRepository: ClientRepository,
    val preferenceRepository: PreferenceRepository
) : BaseFlowUseCase<ChangePinUseCase.Param, Resource<String>>() {

    data class Param(
        var old_pin:String,
        var new_pin:String,
        var device_id:String,
        var model:String
    )

    override fun run(param: Param?): Flow<Resource<String>>  = flow {
        emit(Resource.Loading)
        try {
            param?.let {
                val user = preferenceRepository.getDataStoreUser().first()

                val hashMap = HashMap<String, Any>().apply {
                    this["account"] = user.username
                    this["old_pin"] = it.old_pin
                    this["new_pin"] = it.new_pin
                    this["confirm_pin"] = it.new_pin
                    this["device_id"] = it.device_id
                    this["model"] = it.model
                }
                val response = clientRepository.changeClientPin(hashMap, "Bearer " + user.token)
                val entity = UserEntity(
                    id = user.id,
                    username= user.username,
                    phone= user.phone,
                    name = user.name,
                    pin = it.new_pin,
                    email = user.email,
                    token = user.token,
                    ip = user.ip,
                    sip = user.sip
                )
                preferenceRepository.saveUserToDatastore(entity)

                emit(Resource.Success(response))
            } ?: throw InvalidParameterException()

        } catch (throwable: Throwable) {
            emit(Resource.Error(utilRepository.getNetworkError(throwable)))
        }
    }
}