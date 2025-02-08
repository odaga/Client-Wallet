package com.mcash.domain.repository

import com.mcash.domain.model.DeviceInfo
import com.mcash.domain.model.FormDetailEntity
import com.mcash.domain.model.FormMapEntity

interface KycRepository {

    // Remote Data Source
    suspend fun fetchSingleFormDetails(formId: String): FormDetailEntity

    suspend fun uploadDynamicForm(
        projectId: String,
        formId: String,
        formCode: String,
        ref: String,
        deviceInfo: DeviceInfo,
        formDataMap: List<FormMapEntity>
    ): Boolean

}