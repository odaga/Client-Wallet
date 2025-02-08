package com.mcash.domain.usecases.utility.data

import com.mcash.domain.base.BaseFlowUseCase
import com.mcash.domain.model.DataEntity
import com.mcash.domain.model.sealed.Resource
import com.mcash.domain.repository.ClientRepository
import com.mcash.domain.repository.PreferenceRepository
import com.mcash.domain.repository.UtilRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetDataBundlesUsCase @Inject constructor(
    private val clientRepository: ClientRepository,
    private val preferences: PreferenceRepository,
    private val utilRepository: UtilRepository
) : BaseFlowUseCase<GetDataBundlesUsCase.Param, Resource<Boolean>>() {

    data class Param(
        var providers: List<String>
    )

    override fun run(param: Param?): Flow<Resource<Boolean>> = flow {
        emit(Resource.Loading)
        try {
            val user = preferences.getDataStoreUser().first()
            param?.let { it ->
                val dataPackages = ArrayList<DataEntity>()
                it.providers.forEach { type ->
                    val response = clientRepository.getDataPackages(type, "Bearer " + user.token)

                    println("======== data packages: $response")
                }
            }
            emit(Resource.Success(true))
        } catch (t: Throwable) {
            emit(Resource.Error(utilRepository.getNetworkError(t)))
        }
    }
}