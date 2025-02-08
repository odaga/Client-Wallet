package com.mcash.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class NotificationEntity(
    @PrimaryKey val id:String="",
    val title:String?,
    var message:String?,
    var date:String?,
    var read:Boolean = false
)
