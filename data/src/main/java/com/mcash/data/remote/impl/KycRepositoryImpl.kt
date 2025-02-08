package com.mcash.data.remote.impl

import com.google.gson.GsonBuilder
import com.mcash.data.remote.model.FormDetailsModel
import com.mcash.data.remote.remoteMappers.RemoteFormDetailsMapper
import com.mcash.data.remote.services.KycService
import com.mcash.domain.model.DeviceInfo
import com.mcash.domain.model.FormAttachment
import com.mcash.domain.model.FormDetailEntity
import com.mcash.domain.model.FormGroupEntity
import com.mcash.domain.model.FormMapEntity
import com.mcash.domain.model.KycPayload
import com.mcash.domain.model.KycProfile
import com.mcash.domain.model.RecordMetaData
import com.mcash.domain.repository.KycRepository
import com.mcash.domain.repository.PreferenceRepository
import kotlinx.coroutines.flow.first
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import org.json.JSONObject
import java.io.File
import javax.inject.Inject

class KycRepositoryImpl @Inject constructor(
    private val service: KycService,
    private val preferenceRepository: PreferenceRepository,
    private val formDetailsMapper: RemoteFormDetailsMapper
) : KycRepository {
    override suspend fun fetchSingleFormDetails(formId: String): FormDetailEntity {
        return try {
            val response = service.getProjectFormDetails(formId, "Bearer $AUTH_TOKEN")
            // Form Details
            val formDetails = formDetailsMapper.mapToDomain(response.data)
            processFormGroups(response.data)

            formDetails
        } catch (t: Throwable) {
            println(t.message)
            throw t
        }
    }

    override suspend fun uploadDynamicForm(
        projectId: String,
        formId: String,
        formCode: String,
        ref: String,
        deviceInfo: DeviceInfo,
        formDataMap: List<FormMapEntity>
    ): Boolean {
        return try {
            val user = preferenceRepository.getDataStoreUser().first()
            val gson = GsonBuilder().disableHtmlEscaping().create()
            val kycData = hashMapOf<String, String>()
            val attachments = ArrayList<FormAttachment>()
            val attachmentNames = ArrayList<String>()
            // Flatten the formDataMap to produce a single key-value hashmap for upload
            formDataMap.forEach {
                println("==== form Map ====")
                println(it.data)
                if (it.type == "File") {
                    attachments.add(
                        FormAttachment(
                            name = it.formGroupLabel,
                            filePath = it.data
                        )
                    )
                    attachmentNames.add(it.formGroupLabel)
                }
                if (it.type == "Text") {
                    val parsedFormValue = parseJsonToMap(it.data)
                    println("===== parsedFormValue: $parsedFormValue")
                    kycData.putAll(parsedFormValue)
                }
            }

            // Build the recordMetaData
            val metaData = RecordMetaData(
                formCode,
                deviceInfo
            )

            val kycProfile = KycProfile(
                data = kycData,
                attachmentNames
            )

            val uploadData = KycPayload(
                data = kycProfile,
                metaData = metaData

            )

            // final upload Data
            println("======= final Upload Data: \n $uploadData")

            println("======= final Upload Data processed: \n ${gson.toJson(uploadData)}")


            // Initialize the form Body Builder
            val formBodyBuilder = MultipartBody.Builder().setType(MultipartBody.FORM)

            // Adding the kyv data to form
            formBodyBuilder.addFormDataPart("ref", ref)
            formBodyBuilder.addFormDataPart("project_form_id", projectId)
            formBodyBuilder.addFormDataPart("creator_id", "creator_id")
            formBodyBuilder.addFormDataPart("project_form_id", formCode)
            formBodyBuilder.addFormDataPart("data", gson.toJson(uploadData))

            // Adding the form attachments
            attachments.forEach {
                val file = File(it.filePath)
                if(file.exists()) {
                    formBodyBuilder.addFormDataPart(
                        it.name, // Use index to get the corresponding resource name
                        file.name,
                        file.asRequestBody("application/octet-stream".toMediaType())
                    )
                }
            }

            val body = formBodyBuilder.build()
            println("===== request Body: ${body.parts}")
            val response = service.uploadFormRecord(body.parts, "Bearer $AUTH_TOKEN")


            true
        } catch (t: Throwable) {
            throw t
        }

    }


    private fun processFormGroups(formDetails: FormDetailsModel) {
        val formGroup = ArrayList<FormGroupEntity>()
        formDetails.formGroups.forEach {
            println(it)
            val group = FormGroupEntity(
                it.key,
                formDetails.id,
                it.value.groupLabel,
                formDetails.projectId
            )
            formGroup.add(group)
        }
    }

    private fun parseJsonToMap(jsonString: String): Map<String, String> {
        val jsonObject = JSONObject(jsonString)
        val map = mutableMapOf<String, String>()
        // Convert JSONObject to Map
        for (key in jsonObject.keys()) {
            map[key] = jsonObject.getString(key)
        }
        return map
    }

    companion object {
        private const val AUTH_TOKEN =
            "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJqb3NodWEiLCJpc3MiOiJtY2FzaCIsImlhdCI6MTczNzAxMTczNCwiZXhwIjoxNzM3NDQzNzM0fQ.i79t-7HVaqg9z72p4Jl2n2x0KZSDZrQbZjQ7E0Y8RvU"
    }
}