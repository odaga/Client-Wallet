package com.mcash.domain.model


data class NwscCustomerEntity(
    var customer_account:String?,
    var property_ref:String?,
    var customer_name:String?,
    var customer_area:String?,
    var outstanding_balance:Int?
)

data class NwscValidateEntity(
    var customer_account:String?,
    var customer_name:String?,
    var outstanding_balance:Int?,
    var amount:Int?,
    var amount_payable:Int?,
    var charge:Int?,
    var transactionToken:String?,
    var transaction_ref:String?
)

data class NwscConfirmEntity(
    var customer_account:String?,
    var customer_name:String?,
    var outstanding_balance:Int?,
    var amount:Int?,
    var amount_payable:String?,
    var charge:Int?,
    var transactionToken:String?,
    var transaction_ref:String?
)