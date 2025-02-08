package com.mcash.domain.usecases.fuelSave

import com.mcash.domain.base.BaseFlowUseCase
import com.mcash.domain.model.Branch
import com.mcash.domain.model.sealed.Resource
import com.mcash.domain.repository.FuelSaveRepository
import com.mcash.domain.repository.PreferenceRepository
import com.mcash.domain.repository.UtilRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import java.security.InvalidParameterException
import javax.inject.Inject


class GetBranchDetailsUseCase @Inject constructor(
    private val fuelSaveRepository: FuelSaveRepository,
    private val preferenceRepository: PreferenceRepository,
    private val utilRepository: UtilRepository
) : BaseFlowUseCase<GetBranchDetailsUseCase.Param, Resource<Branch>>() {

    data class Param(
        var branchId: String
    )

    override fun run(param: Param?): Flow<Resource<Branch>> = flow {
        emit(Resource.Loading)
        try {
            val authToken = preferenceRepository.getDataStoreUser().first().token
            param?.let {
                val branch = fuelSaveRepository.getBranch(it.branchId, "Bearer $authToken")
                emit(Resource.Success(branch))
            } ?: throw InvalidParameterException()

        } catch (t: Throwable) {
            emit(Resource.Error(utilRepository.getNetworkError(t)))
        }
    }
}