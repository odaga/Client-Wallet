package com.mcash.client.core.sealed

import com.mcash.domain.model.Beneficiary
import com.mcash.domain.model.Contact

sealed class BeneficiaryState {
    object Loading : BeneficiaryState()
    data class Success(val data: List<Beneficiary>) : BeneficiaryState()
    data class Error(val message: String) : BeneficiaryState()
}


sealed class ContactState {
    object Loading : ContactState()
    data class Error(val message: String) : ContactState()
    data class Success(val data: List<Contact>) : ContactState()
}