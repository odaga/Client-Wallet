package com.mcash.client.presentation.ui.beneficiaries

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mcash.client.core.sealed.BeneficiaryState
import com.mcash.domain.model.Beneficiary
import com.mcash.domain.usecases.BeneficiaryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class BeneficiaryViewModel @Inject constructor(
    private val beneficiaryUseCase: BeneficiaryUseCase
) :ViewModel() {

    init {
        getSavedBeneficiaries()
    }

    private val _beneficiaryState = MutableStateFlow<BeneficiaryState>(BeneficiaryState.Loading)
    val beneficiaryState get() = _beneficiaryState.asStateFlow()

    fun getSavedBeneficiaries() {
        viewModelScope.launch(Dispatchers.IO) {
            beneficiaryUseCase.getBeneficiaries()
                .catch { e ->
                    _beneficiaryState.value = BeneficiaryState.Error(e.localizedMessage)
                }
                .collect {
                    _beneficiaryState.value = BeneficiaryState.Success(it)
                }
        }
    }

    fun deleteBeneficiary(beneficiary: Beneficiary) {
        viewModelScope.launch(Dispatchers.IO) {
            beneficiaryUseCase.deleteBeneficiary(beneficiary)
        }
    }

    fun searchBeneficiary(beneficiary: String) {
        viewModelScope.launch(Dispatchers.IO) {
            beneficiaryUseCase.searchBeneficiary(beneficiary)
                .catch { e ->
                    _beneficiaryState.value = BeneficiaryState.Error(e.localizedMessage)
                }
                .collect {
                    Timber.d("Search results: $it")
                    _beneficiaryState.value = BeneficiaryState.Success(it)
                }
        }
    }

    fun saveBeneficiary(beneficiary: Beneficiary) {
        viewModelScope.launch(Dispatchers.IO) {
            beneficiaryUseCase.addBeneficiary(beneficiary)
        }
    }
}