package com.mcash.data.remote.services

import com.mcash.data.remote.model.Version
import retrofit2.http.GET

interface VersionService {

    @GET("version/get/client_wallet")
    suspend fun getVersionNumber(): Version
}