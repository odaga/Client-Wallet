package com.mcash.domain.usecases.version

import com.mcash.domain.base.BaseFlowUseCase
import com.mcash.domain.model.sealed.Resource
import com.mcash.domain.repository.ClientRepository
import com.mcash.domain.repository.UtilRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.security.InvalidParameterException
import javax.inject.Inject

class GetVersionUseCase @Inject constructor(
    private val utilRepository: UtilRepository,
    private val agent: ClientRepository,
): BaseFlowUseCase<GetVersionUseCase.Param, Resource<String>>() {

    override fun run(param: Param?): Flow<Resource<String>> = flow {
        emit(Resource.Loading)
        try {
            val response = agent.getAppVersion()

            emit(Resource.Success(response))

        } catch (throwable: Throwable) {
            emit(Resource.Error(utilRepository.getNetworkError(throwable)))
        }
    }
    class Param(

    )

}