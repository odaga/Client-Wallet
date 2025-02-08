package com.mcash.domain.model

data class DepositEntity (
    val name: String?,
    val amount: Int?,
    val charge: Int?,
    val transactionRef: String?,
    var transactionToken:String?
)