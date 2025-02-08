package com.mcash.domain.usecases.user

import com.mcash.domain.model.UserEntity
import com.mcash.domain.repository.PreferenceRepository
import javax.inject.Inject

class SaveLocalUserUseCase @Inject constructor(
    private val preferences: PreferenceRepository
) {
    suspend operator fun invoke(user: UserEntity) = preferences.saveUserToDatastore(user)
}