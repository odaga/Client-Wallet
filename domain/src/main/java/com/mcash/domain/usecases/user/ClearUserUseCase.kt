package com.mcash.domain.usecases.user

import com.mcash.domain.repository.PreferenceRepository
import javax.inject.Inject

class ClearUserUseCase @Inject constructor(
    private val preferences: PreferenceRepository
) {
    suspend operator fun invoke() = preferences.clearUser()
}