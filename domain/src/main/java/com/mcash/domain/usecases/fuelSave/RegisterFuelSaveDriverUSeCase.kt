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

class RegisterFuelSaveDriverUSeCase @Inject constructor(
    private val fuelSaveRepository: FuelSaveRepository,
    private val preferenceRepository: PreferenceRepository,
    private val utilRepository: UtilRepository
) : BaseFlowUseCase<RegisterFuelSaveDriverUSeCase.Param, Resource<Boolean>>() {

    data class Param(
        val nin: String
    )

    override fun run(param: Param?): Flow<Resource<Boolean>> = flow {
        try {
            emit(Resource.Loading)
            val user = preferenceRepository.getDataStoreUser().first()
            param?.let {
                val data = HashMap<String, Any>().apply {
                    this["name"] = user.fullName
                    this["phone"] = user.phone
                    this["nin"] = it.nin
                }
                fuelSaveRepository.registerFuelSaveDriver(
                    data,
                    "Bearer ${user.token}"
                )
                emit(Resource.Success(true))
            }

        } catch (t: Throwable) {
            emit(Resource.Error(utilRepository.getNetworkError(t)))
        }
    }
}