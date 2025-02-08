package com.mcash.data.remote.model

import com.google.gson.annotations.SerializedName


data class ProjectListResponse(
    val status: Int,
    val data: List<RemoteProject>
)

data class RemoteProject(
    val id: String,
    val name: String,
    @SerializedName("organisation_id")
    val organisationId: String,
    @SerializedName("creator_id")
    val creatorId: String,
    val enabled: String,
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("updated_at")
    val updatedAt: String,
    @SerializedName("deleted_at")
    val deletedAt: String
)

data class SingleProjectDetailsResponse(
    val status: Int,
    val data: SingleProjectDetails
)

data class SingleProjectDetails(
    val id: String,
    val name: String,
    @SerializedName("organisation_id")
    val organisationId: String,
    val description: String,
    @SerializedName("creator_id")
    val creatorId: String,
    val enabled: String,
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("updated_at")
    val updatedAt: String,
    @SerializedName("deleted_at")
    val deletedAt: String,
    val organisation: String,
    val forms: List<ProjectForm>
)

data class ProjectForm(
    val id: String,
    val code: String,
    @SerializedName("project_id")
    val projectId: String,
    val name: String,
    @SerializedName("callback_url")
    val callBackUrl: String,
    @SerializedName("forward_attachments")
    val forwardAttachments: String,
    @SerializedName("purge_forwarded_attachments")
    val purgeForwardedAttachments: String,
    @SerializedName("auto_approve")
    val autoApprove: String,
    @SerializedName("ref_field")
    val refField: String,
    @SerializedName("creator_id")
    val creatorId: String,
    val enabled: String,
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("updated_at")
    val updatedAt: String,
    @SerializedName("deleted_at")
    val deletedAt: String
)

data class RecordUploadResponse(
    val status: Int
//    val data: UploadResponseData?,
)

data class UploadResponseData(
    val data: String?,
    val id: String?,
    @SerializedName("project_form_id")
    val projectFormId: String?,
    val ref: String?
)

data class FormDetailsResponse(
    val status: Int,
    val data: FormDetailsModel
)

data class FormDetailsModel(
    val id: String,
    val code: String?,
    @SerializedName("project_id")
    val projectId: String?,
    val name: String?,
    @SerializedName("callback_url")
    val callbackUrl: String?,
    @SerializedName("forward_attachments")
    val forwardAttachments: String?,
    @SerializedName("purge_forwarded_attachments")
    val purgeForwardedAttachments: String?,
    @SerializedName("auto_approve")
    val autoApprove: String?,
    @SerializedName("update_resubmits")
    val updateResubmits: String?,
    @SerializedName("ref_field")
    val refField: String?,
    @SerializedName("creator_id")
    val creatorId: String?,
    val enabled: String?,
    @SerializedName("created_at")
    val createdAt: String?,
    @SerializedName("updated_at")
    val updatedAt: String?,
    @SerializedName("deleted_at")
    val deletedAt: String?,
    @SerializedName("form_groups")
    val formGroups: HashMap<String, FormGroupModel>
)

data class FormGroupModel(
    @SerializedName("group_label")
    val groupLabel: String?,
    @SerializedName("group_id")
    val groupId: String?,
    val fields: List<FormFieldModel>
)

data class FormFieldModel(
    @SerializedName("field_type_machine_name")
    val fieldTypeMachineName: String?,
    @SerializedName("field_type")
    val fieldType: String?,
    @SerializedName("field_type_id")
    val fieldTypeId: String?,
    @SerializedName("field_name")
    val fieldName: String?,
    val label: String?,
    @SerializedName("is_required")
    val isRequired: String?,
    val dataset: Any?,
    @SerializedName("data_source")
    val dataSource: String?,
    val weight: String?
)


