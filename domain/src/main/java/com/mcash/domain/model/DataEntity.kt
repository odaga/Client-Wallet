package com.mcash.domain.model

import androidx.annotation.NonNull
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "data")
data class Data(
    @PrimaryKey
    var networkType:String,
    var data:List<DataEntity>
)


@Entity
data class DataEntity(
    @PrimaryKey
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
