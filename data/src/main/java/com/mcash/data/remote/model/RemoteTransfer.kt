package com.mcash.data.remote.model


data class RemoteTransferResponse(
    val message: String?,
    val data: RemoteTransferData,
    val status_code: Int,
    val app_status: Boolean
)

data class RemoteTransfer(
    val amount: String?,
    val date: String?,
    val description: String?,
    val formattedAmount: String?,
    val formattedDate: String?,
    val formattedProcessDate: String?,
    val id: Long,
    val processDate: String?,
    val status: String?,
    val systemAccountName: String?,
    val transferType: RemoteTransferType?,
    val member: RemoteUser?
)

data class RemoteTransferData(
    val transfer: RemoteTransfer
)

data class RemoteTransferType(
    val id: Int,
    val name: String?
)