package com.mcash.domain.usecases.account

import com.mcash.domain.repository.PreferenceRepository
import javax.inject.Inject

class GetSavedBalanceUseCase @Inject constructor(
    private val preferences: PreferenceRepository
) {
    operator fun invoke() = preferences.getAccountBalance()
}