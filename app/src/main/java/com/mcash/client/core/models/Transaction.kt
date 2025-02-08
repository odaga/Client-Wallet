package com.mcash.client.core.models

data class Transaction(
    var id: Int,
    var status: String,
    var name: String,
    var createdAt: String,
    var amount: String
)
