package com.mcash.domain.model

import androidx.annotation.Keep
import java.util.*

@Keep
data class UserEntity(
    val id: String,
    val username: String,
    val phone: String,
    val name: String,
    val pin: String,
    val email: String,
    val token:String,
    val ip:String,
    val sip:String
) {

    val fullName = name
        .lowercase()
        .replaceFirstChar {
            if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
        }

    val isEmpty = phone.isEmpty() && name.isEmpty() && pin.isEmpty()
}