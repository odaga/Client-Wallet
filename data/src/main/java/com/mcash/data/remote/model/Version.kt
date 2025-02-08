package com.mcash.data.remote.model

import androidx.annotation.Keep

@Keep
data class Version(
    var message:String?,
    var status_code: Int?,
    var app_status:Boolean?,
    var data: VersionData
)

data class VersionData(
    var project: String?,
    var version:String?
)
