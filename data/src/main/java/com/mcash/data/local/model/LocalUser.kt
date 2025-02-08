package com.mcash.data.local.model

import androidx.annotation.Keep
import androidx.room.Entity
import androidx.room.PrimaryKey

@Keep
@Entity(tableName = "users")
data class LocalUser(
    @PrimaryKey(autoGenerate = false)
    val accountBalance: String,
    val accountId: String,
    val currency: String,
    val firstName: String,
    val lastName: String,
    val otp: Long,
    val phone: String
)