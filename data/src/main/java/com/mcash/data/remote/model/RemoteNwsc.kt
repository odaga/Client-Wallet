package com.mcash.data.remote.model

import androidx.annotation.Keep

@Keep
data class RemoteValidateNwscCustomer(
    var status_code:Int,
    var app_status:Boolean,
    var data: ValidateNwscCustomer
)

data class ValidateNwscCustomer(
    var customer_account:String?,
    var property_ref:String?,
    var customer_name:String?,
    var customer_area:String?,
    var outstanding_balance:Int?
)

data class RemoteValidateNwscTransaction(
    var status_code:Int,
    var app_status:Boolean,
    var transaction: ValidateNwscTransaction
)

data class ValidateNwscTransaction(
    var customer_account:String?,
    var customer_name:String?,
    var outstanding_balance:Int?,
    var amount:Int?,
    var amount_payable:Int?,
    var charge:Int?,
    var transactionToken:String?,
    var transaction_ref:String?
)


data class RemoteConfirmNwscTransaction(
    var status_code:Int,
    var app_status:Boolean,
    var transaction: ConfirmNwscTransaction
)

data class ConfirmNwscTransaction(
    var customer_account:String?,
    var customer_name:String?,
    var outstanding_balance:Int?,
    var amount:Int?,
    var amount_payable:String?,
    var charge:Int?,
    var transactionToken:String?,
    var transaction_ref:String?
)

