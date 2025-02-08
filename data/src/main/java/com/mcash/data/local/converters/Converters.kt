package com.mcash.data.local.converters

import androidx.room.TypeConverter
import com.google.gson.Gson

class Converters {
    @TypeConverter
    fun fromList(value: List<String>) = Gson().toJson(value)

    @TypeConverter
    fun toList(value: String) = Gson().fromJson(value, Array<String>::class.java).toList()

//    @TypeConverter
//    fun fromDeliveryList(value: List<LocalDeliveryAddress>): String = Gson().toJson(value)
//
//    @TypeConverter
//    fun toDeliveryList(value: String) = Gson().fromJson(value, Array<LocalDeliveryAddress>::class.java).toList()
}