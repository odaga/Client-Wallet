package com.mcash.data.remote.model


data class RemoteTransactionResponse(
    val message: String?,
    val data: RemoteTransactionData,
    val status_code: Int,
    val app_status: Boolean
)

data class RemoteTransactionData(
    val currentPage: Long,
    val pageSize: Long,
    val totalCount: Long,
    val transfers: List<RemoteTransfer>
)