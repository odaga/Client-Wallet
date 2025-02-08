package com.mcash.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class FormDetailEntity(
    @PrimaryKey(autoGenerate = false)
    val id: String,
    val code: String?,
    val projectId: String?,
    val name: String?,
    val callbackUrl: String?,
    val forwardAttachments: String?,
    val purgeForwardedAttachments: String?,
    val autoApprove: String?,
    val updateResubmits: String?,
    val refField: String?,
    val creatorId: String?,
    val enabled: String?,
    val createdAt: String?,
    val updatedAt: String?,
    val deletedAt: String?
)

@Entity
data class FormGroupEntity(
    @PrimaryKey(autoGenerate = false)
    val id: String,
    val formId: String?,
    val groupLabel: String?,
    val projectId: String?
)