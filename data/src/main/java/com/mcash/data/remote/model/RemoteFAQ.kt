package com.mcash.data.remote.model

import androidx.annotation.Keep
import androidx.room.Entity
import androidx.room.PrimaryKey

@Keep
data class RemoteFAQ(
    var message:String?,
    var status_Code:Int?,
    var app_status: Boolean?,
    var data:ArrayList<FAQ>
)

@Entity(tableName = "faqs")
data class FAQ(
    @PrimaryKey var id:Int,
    var title:String?,
    var content:String?
)

