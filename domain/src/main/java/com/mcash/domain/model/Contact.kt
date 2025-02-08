package com.mcash.domain.model

import androidx.annotation.NonNull
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "contact")
data class Contact(
    @PrimaryKey
    @NonNull
    var id: String = "",
    var name:String = "",
    var numbers: List<String> = ArrayList()
) {

    override fun toString(): String {
        return "Contact(id='$id', name='$name', numbers=$numbers)"
    }
}

@Entity(tableName = "beneficiary")
data class Beneficiary(
    @PrimaryKey
    @NonNull
    var id: String = "",
    var name:String = "",
    var numbers: List<String> = ArrayList()
) {

    override fun toString(): String {
        return "Beneficiary(id='$id', name='$name', numbers=$numbers)"
    }
}