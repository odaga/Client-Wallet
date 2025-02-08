package com.mcash.domain.model

data class ValidateUtilityEntity(
    val customerName: String?,
    val responseCode: String?,
    val biller: String?,
    val excise: Int?,
    val surchargeType: Boolean,
    val transactionRef: String?,
    val isAmountFixed: String?,
    val surcharge: Int?,
    val paymentItemId: Int?,
    val amount: Int?,
    val paymentItem: String?,
    val shortTransactionRef: String?,
    val balance: Int?,
    val customerId: String?,
    val balanceNarration: String?,
    val collectionsAccountNumber: String?,
    val narration: String?,
    val collectionsAccountType: String?,
    val displayBalance: Boolean?,
    val balanceType: String?,
    val transactionToken: String?,
)
data class ConfirmUtilityEntity(
    var customer_reference:String?,
    var customer_phone:String?,
    var amount:String?,
    var service_code:String?,
    var payment_ref:String?,
    var transaction_ref:String?,
//    var responseMessage:String?,
//    var responseCode:String?,
//    var rechargePIN:String?,
//    var requestReference:String?,
//    var transferCode:String?,
//    var transactionRef:String?
)
