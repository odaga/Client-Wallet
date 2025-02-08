package com.mcash.data.remote.impl

import com.mcash.data.util.resolveError
import com.mcash.domain.repository.UtilRepository
import javax.inject.Inject

class UtilRepositoryImpl @Inject constructor(): UtilRepository {

    override fun getNetworkError(throwable: Throwable): String = throwable.resolveError()
}