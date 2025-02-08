package com.mcash.client.core.models

data class IdCardInfo(
    var lastName: String = "",
    var firstName: String = "",
    var middleName: String = "",
    var expiryDate: String = "",
    var issueDate: String = "",
    var dateOfBirth: String = "",
    var gender: String = "",
    var nationality: String = "",
    var nin: String = "",
    var cardNumber: String = ""
)
