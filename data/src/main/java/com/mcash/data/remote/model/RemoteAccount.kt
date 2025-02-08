package com.mcash.data.remote.model

import androidx.annotation.Keep

@Keep
data class AccountBalanceResponse(
    val message: String?,
    val data: RemoteAccountBalance,
    val status_code: Int,
    val app_status: Boolean
)

data class RemoteAccountBalance(
    val amount: Long
)