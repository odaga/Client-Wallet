package com.mcash.domain.usecases.nwsc

import com.mcash.domain.repository.ClientRepository
import com.mcash.domain.repository.PreferenceRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class FetchNwscAresUseCase @Inject constructor(
    private val client: ClientRepository,
    private val preferenceRepository: PreferenceRepository
) {

    suspend operator fun invoke() {
        val user = preferenceRepository.getDataStoreUser().first()
        client.fetchNwscAreas("Bearer " + user.token)
    }
}