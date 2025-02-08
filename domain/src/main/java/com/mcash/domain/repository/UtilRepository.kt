package com.mcash.domain.repository

interface UtilRepository {

    fun getNetworkError(throwable: Throwable): String
}