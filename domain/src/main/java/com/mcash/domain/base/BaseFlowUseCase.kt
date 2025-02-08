package com.mcash.domain.base

import com.mcash.domain.dispacher.AppDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn

abstract class BaseFlowUseCase<in Param, Result> where Param : Any {

    abstract fun run(param: Param?): Flow<Result>

    operator fun invoke(param: Param? = null) = run(param)
}