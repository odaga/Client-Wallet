package com.mcash.data.remote.model

data class VoicePackageResponse(
    var status_code:Int?,
    var app_status:Boolean?,
    var packages:List<VoicePackage>
)

data class VoicePackage(
    var packageId:String,
    var isAmountFixed:Boolean?,
    var paymentitemname:String?,
    var amount:String?,
    var code:String?,
    var currencyCode:String?,
    var currencySymbol:String?,
    var itemCurrencySymbol:String?,
    var sortOrder:String?,
    var pictureId:String?
)
