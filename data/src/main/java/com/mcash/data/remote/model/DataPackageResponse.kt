package com.mcash.data.remote.model


data class DataPackageResponse(
    var status_code:Int?,
    var app_status:Boolean?,
    var packages:List<DataPackage>
)

data class DataPackage(
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

