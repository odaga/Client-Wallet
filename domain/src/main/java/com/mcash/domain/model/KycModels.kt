package com.mcash.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class CardInfoEntity(
    @PrimaryKey(autoGenerate = false)
    var nin: String,
    var lastName: String,
    var firstName: String,
    var middleName: String,
    var expiryDate: String,
    var issueDate: String,
    var dateOfBirth: String,
    var gender: String,
    var nationality: String,
    var cardNumber: String
)

@Entity
data class FormMapEntity(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    val type: String,
    var formGroupLabel: String,
    var data: String
)

@Entity
data class ProfileSnapshot(
    @PrimaryKey(autoGenerate = false)
    val id: String,
    val projectId: String,
    val projectCode: String,
    val formId: String,
    val status: String,
    val resourceNames: List<String>,
    val resourcePaths: List<String>,
    val data: String,
    val createdAt: String
)

data class DeviceInfo(
    val deviceId: String,
    val deviceModel: String,
    val androidVersion: String,
    val gpsCoordinates: String
)

data class FormAttachment(
    val name: String,
    val filePath: String
)

data class RecordMetaData(
    val formCode: String,
    val deviceInfo: DeviceInfo
//    val deviceUser: DeviceUser
)

data class KycProfile(
    val data: Map<String, String>,
    val attachments: List<String>
)


data class KycPayload(
    val data: KycProfile,
    val metaData: RecordMetaData
)