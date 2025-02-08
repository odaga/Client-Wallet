package com.mcash.data.remote.model

import androidx.annotation.Keep

@Keep
data class LoginResponse(
    val message: String?,
    val data: LoginData,
    val status_code: Int,
    val app_status: Boolean
)

data class VerifyOtpResponse(
    val message: String?,
    val status_code: Int,
    val app_status: Boolean,
    val data: RemoteUser
)

data class LoginData(
    val phone: String?,
    val code: String?
)

data class RemoteUser(
   val id: String,
   val username: String?,
   val phone: String?,
   val name: String?,
   val email: String?,
   val token:String,
   val ip:String,
   val sip:String
)

data class ResetPinResponse(
    val message: String?,
    val status_code: Int,
    val app_status: Boolean,
)
