package com.mcash.domain.usecases.user

import com.mcash.domain.repository.PreferenceRepository
import javax.inject.Inject

class GetSavedUserUseCase @Inject constructor(
    private val preferences: PreferenceRepository
) {

    operator fun invoke() = preferences.getDataStoreUser()
}