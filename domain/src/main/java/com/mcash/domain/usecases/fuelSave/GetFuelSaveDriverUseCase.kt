package com.mcash.domain.usecases.fuelSave

import com.mcash.domain.base.BaseFlowUseCase
import com.mcash.domain.model.FuelSaveDriverEntity
import com.mcash.domain.model.sealed.Resource
import com.mcash.domain.repository.FuelSaveRepository
import com.mcash.domain.repository.PreferenceRepository
import com.mcash.domain.repository.UtilRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetFuelSaveDriverUseCase @Inject constructor(
    private val fuelSaveRepository: FuelSaveRepository,
    private val preferenceRepository: PreferenceRepository,
    private val utilRepository: UtilRepository
) : BaseFlowUseCase<GetFuelSaveDriverUseCase.Param, Resource<FuelSaveDriverEntity>>() {

    data class Param(
        val driverCode: String
    )

    override fun run(param: Param?): Flow<Resource<FuelSaveDriverEntity>> = flow {
        try {
            emit(Resource.Loading)
            val user = preferenceRepository.getDataStoreUser().first()

            param?.let {
                val fuelSaveDriver =
                    fuelSaveRepository.getFuelSaveDriver(it.driverCode, "Bearer ${user.token}")
                emit(Resource.Success(fuelSaveDriver))
            }

        } catch (t: Throwable) {
            emit(Resource.Error(utilRepository.getNetworkError(t)))
        }
    }

}