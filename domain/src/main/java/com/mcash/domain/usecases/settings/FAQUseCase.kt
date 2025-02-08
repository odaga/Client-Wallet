package com.mcash.domain.usecases.settings

import com.mcash.domain.model.FAQEntity
import com.mcash.domain.model.sealed.Resource
import com.mcash.domain.repository.ClientRepository
import com.mcash.domain.repository.UtilRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class FAQUseCase @Inject constructor(
    val utilRepository: UtilRepository,
    val clientRepository: ClientRepository
) {

    suspend operator fun invoke(): List<FAQEntity> {
        return try {
            clientRepository.getFAQs()
        } catch (t: Throwable) {
            throw t
        }
    }

//    override fun run(param: Param?): Flow<Resource<List<FAQEntity>>> = flow {
//        emit(Resource.Loading)
//        try {
//            val request = clientRepository.getFAQs()
//            emit(Resource.Success(request))
//
//        } catch (t: Throwable) {
//            emit(Resource.Error(utilRepository.getNetworkError(t)))
//        }
//    }
}