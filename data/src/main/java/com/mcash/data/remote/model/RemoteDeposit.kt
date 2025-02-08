package com.mcash.data.remote.model


data class RemoteValidateDepositResponse(
    val message: String?,
    val transaction: ValidateDepositResponse,
    val status_code: Int,
    val app_status: Boolean
)

data class ValidateDepositResponse(
    val name: String?,
    val amount: Int?,
    val charge: Int?,
    val transactionRef: String?,
    var transactionToken:String?
)

data class RemoteConfirmDepositResponse(
    var message:String?,
    var status_code:Int?,
    var app_status:Boolean,
    var data:ConfirmDepositResponse?
)

data class ConfirmDepositResponse(
    val message: String?,
)


data class RemoteConfirmTransferResponse(
    var message:String?,
    var status_code:Int?,
    var app_status:Boolean,
    var transaction:ConfirmTransferResponse
)

data class ConfirmTransferResponse(
    val description: String?,
)
