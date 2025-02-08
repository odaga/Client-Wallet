package com.mcash.domain.usecases.kyc

import com.mcash.domain.base.BaseFlowUseCase
import com.mcash.domain.model.CardInfoEntity
import com.mcash.domain.model.sealed.Resource
import com.mcash.domain.repository.PreferenceRepository
import com.mcash.domain.repository.UtilRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class SaveIdCardInfoUseCase @Inject constructor(
    private val preferenceRepository: PreferenceRepository,
) : BaseFlowUseCase<CardInfoEntity, Resource<CardInfoEntity>>() {
    override fun run(param: CardInfoEntity?): Flow<Resource<CardInfoEntity>> = flow {
        emit(Resource.Loading)
        try {
            param?.let {
                preferenceRepository.saveIdCard(it)
                emit(Resource.Success(preferenceRepository.getIdCard(it.nin)))
            }

        } catch (t: Throwable) {
            emit(Resource.Error(t.message ?: ""))
        }
    }
}