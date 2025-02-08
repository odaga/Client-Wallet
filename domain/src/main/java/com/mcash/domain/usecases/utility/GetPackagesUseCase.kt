package com.mcash.domain.usecases.utility

import com.mcash.domain.repository.ClientRepository
import com.mcash.domain.repository.PreferenceRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class GetPackagesUseCase @Inject constructor(
    var agentRepository: ClientRepository,
    var preferenceRepository: PreferenceRepository
) {

    var tVPackages = listOf<String>("dstv", "azamtv", "startimestv", "zukutv")
    var voicePackages = listOf("mtn", "airtel")
    var dataPackages = listOf("airtel", "mtn", "roke", "smile")

    suspend fun getTvPackages() {
        val user = preferenceRepository.getDataStoreUser().first()
        for (i in tVPackages){
            agentRepository.getTVPackages(i, "Bearer " + user.token)
        }
    }

    suspend fun getVoicePackages() {
        val user = preferenceRepository.getDataStoreUser().first()
        for (i in voicePackages){
            agentRepository.getVoicePackages(i,"Bearer " + user.token)
        }
    }

    suspend fun getDataPackages() {
        val user = preferenceRepository.getDataStoreUser().first()
        for (i in dataPackages){
            agentRepository.getDataPackages(i,"Bearer " + user.token)
        }
    }
}