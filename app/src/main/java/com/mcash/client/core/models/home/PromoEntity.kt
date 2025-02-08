package com.mcash.client.core.models.home

import android.os.Parcelable
//import androidx.room.Entity
//import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
//import kotlinx.parcelize.Parcelize

//@Entity
//@Parcelize
data class PromoEntity(
    //@PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val body: String,
    val banner: String,
    @SerializedName("formatted_body")
    val formattedBody: String
)//: Parcelable
