package com.mcash.data.remote.services

import com.mcash.data.remote.model.FormDetailsResponse
import com.mcash.data.remote.model.RecordUploadResponse
import okhttp3.MultipartBody
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

interface KycService {

    @GET("project-form")
    suspend fun getProjectFormDetails(
        @Query("id") projectFormId: String,
        @Header("Authorization") authHeader: String,
    ): FormDetailsResponse

    @Multipart
    @POST("record/add")
    suspend fun uploadFormRecord(
        @Part kycData: List<MultipartBody.Part>,
        @Header("Authorization") authHeader: String,
    ): RecordUploadResponse
}