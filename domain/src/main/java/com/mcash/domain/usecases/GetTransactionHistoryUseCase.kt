package com.mcash.domain.usecases

import com.mcash.domain.base.BaseFlowUseCase
import com.mcash.domain.model.HistoryEntity
import com.mcash.domain.model.sealed.Resource
import com.mcash.domain.repository.ClientRepository
import com.mcash.domain.repository.PreferenceRepository
import com.mcash.domain.repository.UtilRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import java.security.InvalidParameterException
import javax.inject.Inject

class GetTransactionHistoryUseCase @Inject constructor(
    private val utilRepository: UtilRepository,
    private val agent: ClientRepository,
    private val preferenceRepository: PreferenceRepository
): BaseFlowUseCase<GetTransactionHistoryUseCase.Param, Resource<List<HistoryEntity>>>() {

    class Param()

    override fun run(param: Param?): Flow<Resource<List<HistoryEntity>>> = flow {
        emit(Resource.Loading)
        try {
            val user = preferenceRepository.getDataStoreUser().first()
            param?.let {
                val hashMap = HashMap<String, Any>().apply {
                    this["account_number"] = user.username
                    this["pin"] = user.pin
                }

                val response = agent.getClientHistory(hashMap, "Bearer " + user.token)
                emit(Resource.Success(response))

            } ?: throw InvalidParameterException()

        } catch (throwable: Throwable) {
            emit(Resource.Error(utilRepository.getNetworkError(throwable)))
        }

    }
}