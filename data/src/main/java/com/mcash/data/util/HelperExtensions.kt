package com.mcash.data.util

import com.google.gson.Gson
import com.mcash.domain.model.GenericError
import retrofit2.HttpException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

/**
 * A method that extracts and formats the error returned from an endpoint
 */
fun Throwable.resolveError(): String {

    return when (this) {
        is SocketTimeoutException -> {
           "Connection failed"
        }
        is ConnectException -> {
            "No internet access"
        }
        is UnknownHostException -> {
            "No Internet Connection"
        }

        is HttpException -> {
            try {
                this.response()?.errorBody()?.charStream()?.let {
                    Gson().fromJson(it, GenericError::class.java).message
                }.toString()
            } catch (ex: Exception) {
                "Connection failed"
            }
        }

        else -> {
            "An error occurred. Please contact support if the issue persists. ${this}"
        }
    }
}