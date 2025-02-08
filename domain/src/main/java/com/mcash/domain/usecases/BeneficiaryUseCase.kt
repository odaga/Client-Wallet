package com.mcash.domain.usecases

import com.mcash.domain.model.Beneficiary
import com.mcash.domain.repository.LocalRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class BeneficiaryUseCase @Inject constructor(
    private val local: LocalRepository
) {

    fun getBeneficiaries(): Flow<List<Beneficiary>> = local.getBeneficiaries()

    fun searchBeneficiary(search: String): Flow<List<Beneficiary>> = local.searchBeneficiary(search)

    suspend fun addBeneficiary(beneficiary: Beneficiary) = local.insertBeneficiary(beneficiary)

    suspend fun deleteBeneficiary(beneficiary: Beneficiary) = local.deleteBeneficiary(beneficiary)
}