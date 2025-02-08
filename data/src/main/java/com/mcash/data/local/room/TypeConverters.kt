package com.mcash.data.local.room

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.mcash.domain.model.DataEntity

class DataTypeConverter {
    @TypeConverter
    fun toList(json: String?): List<DataEntity?> {
        val gson = Gson()
        val type = object : TypeToken<List<DataEntity?>>() {}.type
        return gson.fromJson(json, type)
    }

    @TypeConverter
    fun toString(crops: List<DataEntity>): String? {
        val gson = Gson()
        val type = object : TypeToken<List<DataEntity?>>() {}.type
        return gson.toJson(crops, type)
    }
}