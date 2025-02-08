package com.mcash.domain.model

import androidx.annotation.NonNull
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "voice")
class VoiceEntity(
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