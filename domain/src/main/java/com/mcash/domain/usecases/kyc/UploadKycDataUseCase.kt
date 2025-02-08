package com.mcash.domain.usecases.kyc

import com.mcash.domain.base.BaseFlowUseCase
import com.mcash.domain.model.DeviceInfo
import com.mcash.domain.model.sealed.Resource
import com.mcash.domain.repository.KycRepository
import com.mcash.domain.repository.PreferenceRepository
import com.mcash.domain.repository.UtilRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class UploadKycDataUseCase @Inject constructor(
    private val preferenceRepository: PreferenceRepository,
    private val utilRepository: UtilRepository,
    private val kycRepository: KycRepository
) : BaseFlowUseCase<UploadKycDataUseCase.Param, Resource<Boolean>>() {

    data class Param(
        val deviceId: String,
        val model: String,
        val androidVersion: String,
        val longitude: String,
        val latitude: String
    )

    override fun run(param: Param?): Flow<Resource<Boolean>> = flow {
        emit(Resource.Loading)
        try {
            param?.let {
                val kycData = preferenceRepository.getAllFormGroupData()
                val data = HashMap<String, Any>().apply {
                    this["device_id"] = it.deviceId
                    this["model"] = it.model
                    this["longitude"] = it.longitude
                    this["latitude"] = it.latitude
                }

                val projectDetails = kycRepository.fetchSingleFormDetails("11")

                println(projectDetails)

                println("==== projectId: ${projectDetails.projectId}")
                println("==== FormCode: ${projectDetails.code}")
                println("==== FormCode: ${projectDetails.id}")
                println("==== formMap: $kycData")


                val deviceInfo = DeviceInfo(
                    it.deviceId,
                    it.model,
                    it.androidVersion,
                    "${it.latitude}, ${it.longitude}"
                )



                val response = kycRepository.uploadDynamicForm(
                    projectDetails.projectId.toString(),
                    projectDetails.code.toString(),    //"MCF0010"
                    "ref",
                    projectDetails.id,
                    deviceInfo,
                    kycData
                )

                println("===== upload response: $response")
            }

            emit(Resource.Success(true))
        } catch (t: Throwable) {
            emit(Resource.Error(utilRepository.getNetworkError(t)))
        }
    }
}