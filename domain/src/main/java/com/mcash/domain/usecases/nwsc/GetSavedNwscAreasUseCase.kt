package com.mcash.domain.usecases.nwsc

import com.mcash.domain.repository.PreferenceRepository
import javax.inject.Inject

class GetSavedNwscAreasUseCase  @Inject constructor(
    private val preferences: PreferenceRepository
) {
    operator fun invoke() = preferences.getNwscAreas()
}