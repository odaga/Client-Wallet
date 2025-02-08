package com.mcash.domain.model

data class VerifyUmemeEntity(
    var CustomerRef:String?,
    var CustomerName:String?,
    var CustomerType:String?,
    var Balance: Double?,
    var Credit:Int?
)

data class PayUmemeEntity(
    var AccountPayAmount: String?,
    var AccountSaveAmount:String?,
    var RemainingCredit:String?,
    var LifeLine:String?,
    var ServiceFee:String?,
    var DebtRecovery:String?,
    var ReceiptNumber:String?,
    var StatusDescription:String?,
    var StatusCode:String?,
    var MeterNumber:String?,
    var Units:String?,
    var TokenValue:String?,
    var Inflation:String?,
    var Tax:String?,
    var Fx:String?,
    var Fuel:String?,
    var TotalAmount:String?,
    var PrepaidToken:String?
)