package com.mcash.domain.usecases.utility.tv

import androidx.room.Dao
import com.mcash.domain.base.BaseFlowUseCase
import com.mcash.domain.model.sealed.Resource
import com.mcash.domain.repository.ClientRepository
import com.mcash.domain.repository.PreferenceRepository
import com.mcash.domain.repository.UtilRepository
import com.mcash.domain.usecases.utility.GetPackagesUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import java.security.InvalidParameterException
import javax.inject.Inject

class GetTvPackagesUseCase @Inject constructor(
    val clientRepository: ClientRepository,
    val preferenceRepository: PreferenceRepository,
    val utilRepository: UtilRepository,
) : BaseFlowUseCase<GetTvPackagesUseCase.Param, Resource<Boolean>>() {

    data class Param(
        val providers: List<String>
    )

    override fun run(param: Param?): Flow<Resource<Boolean>> = flow {
        emit(Resource.Loading)
        try {
            param?.let {
                val user = preferenceRepository.getDataStoreUser().first()
                it.providers.forEach { provider ->
                    clientRepository.getTVPackages(provider, "Bearer " + user.token)
                }
                emit(Resource.Success(true))
            } ?: throw InvalidParameterException()

        } catch (t: Throwable) {
            emit(Resource.Error(utilRepository.getNetworkError(t)))
        }
    }
}