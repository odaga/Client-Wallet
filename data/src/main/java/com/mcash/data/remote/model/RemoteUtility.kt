package com.mcash.data.remote.model

data class NwscAreasResponse(
    val message: String?,
    val data: List<String>,
    val status_code: Int,
    val app_status: Boolean
)

//data class AreasData(
//    val areas: List<String>
//)