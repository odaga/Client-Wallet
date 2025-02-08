package com.mcash.domain.usecases.settings

import android.util.Log
import com.mcash.domain.base.BaseFlowUseCase
import com.mcash.domain.model.sealed.Resource
import com.mcash.domain.repository.ClientRepository
import com.mcash.domain.repository.UtilRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class HelpUseCase @Inject constructor(
    val utilRepository: UtilRepository,
    val clientRepository: ClientRepository
) {

    suspend operator fun invoke(): String {
        return try {
           val x =  clientRepository.getHelp()
            Log.d("x response", x)
            x
        } catch (t: Throwable) {
            throw t
        }
    }
//    class Param()
//
//    override fun run(param: Param?): Flow<Resource<String>> = flow {
//        emit(Resource.Loading)
//
//        try {
//            val request = clientRepository.getHelp()
//            emit(Resource.Success(request))
//
//        } catch (t: Throwable) {
//            emit(Resource.Error(utilRepository.getNetworkError(t)))
//        }
//
//    }


}