package com.mcash.data.remote.remoteMappers

import com.mcash.data.remote.model.FormDetailsModel
import com.mcash.domain.model.FormDetailEntity
import javax.inject.Inject

class RemoteFormDetailsMapper @Inject constructor() :
    BaseRemoteMapper<FormDetailsModel, FormDetailEntity> {
    override fun mapToDomain(entity: FormDetailsModel): FormDetailEntity {
        return FormDetailEntity(
            id = entity.id,
            code = entity.code ?: "",
            projectId = entity.projectId ?: "",
            name = entity.name ?: "",
            callbackUrl = entity.callbackUrl ?: "",
            forwardAttachments = entity.forwardAttachments ?: "",
            purgeForwardedAttachments = entity.purgeForwardedAttachments ?: "",
            autoApprove = entity.autoApprove ?: "",
            updateResubmits = entity.updateResubmits ?: "",
            refField = entity.refField ?: "",
            creatorId = entity.creatorId ?: "",
            enabled = entity.enabled ?: "",
            createdAt = entity.createdAt ?: "",
            updatedAt = entity.updatedAt ?: "",
            deletedAt = entity.deletedAt ?: ""
        )
    }
}