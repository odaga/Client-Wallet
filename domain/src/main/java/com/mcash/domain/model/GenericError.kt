package com.mcash.domain.model

data class GenericError(
    var app_status: Boolean = false,
    var status_code: Int,
    var message: String = ""
)