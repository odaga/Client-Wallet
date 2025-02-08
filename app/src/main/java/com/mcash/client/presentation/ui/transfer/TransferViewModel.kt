package com.mcash.client.presentation.ui.transfer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.mcash.domain.dispacher.AppDispatcher
import com.mcash.domain.model.Beneficiary
import com.mcash.domain.model.DepositEntity
import com.mcash.domain.model.sealed.Resource
import com.mcash.domain.usecases.BeneficiaryUseCase
import com.mcash.domain.usecases.account.GetSavedBalanceUseCase
import com.mcash.domain.usecases.mobilemoney.ConfirmMobileMoneyUseCase
import com.mcash.domain.usecases.mobilemoney.ValidateMobileMoneyUseCase
import com.mcash.domain.usecases.transfer.TransferToWalletUseCase
import com.mcash.domain.usecases.transfer.ValidateTransferToWalletUseCase
import com.mcash.domain.usecases.user.GetSavedUserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class TransferViewModel @Inject constructor(
    private val beneficiaryUseCase: BeneficiaryUseCase,
    private val validateTransferToWalletUseCase: ValidateTransferToWalletUseCase,
    private val transferToWalletUseCase: TransferToWalletUseCase,
    private val validateMobileMoneyUseCase: ValidateMobileMoneyUseCase,
    private val confirmMobileMoneyUseCase: ConfirmMobileMoneyUseCase,
    private val getSavedBalanceUseCase: GetSavedBalanceUseCase,
    private val getSavedUserUseCase: GetSavedUserUseCase,
    private val dispatcher: AppDispatcher
) : ViewModel() {

    private val _validateState = MutableStateFlow<ValidateState>(ValidateState.Initial)
    val validateState get() = _validateState.asLiveData()

    private val _transferState = MutableStateFlow<TransferState>(TransferState.Initial)
    val transferState get() = _transferState.asLiveData()

    private val _validateMMState = MutableStateFlow<ValidateMMState>(ValidateMMState.Initial)
    val validateMMState get() = _validateMMState.asLiveData()

    private val _confirmMMState = MutableStateFlow<ConfirmMMState>(ConfirmMMState.Initial)
    val confirmMMState get() = _confirmMMState.asLiveData()

    val savedBalance = runBlocking(dispatcher.io) { getSavedBalanceUseCase().first() }

    val savedUser = runBlocking(dispatcher.io) { getSavedUserUseCase().first() }

    fun saveBeneficiary(beneficiary: Beneficiary) {
        viewModelScope.launch {
            withContext(dispatcher.io) {
                beneficiaryUseCase.addBeneficiary(beneficiary)
            }
        }
    }

    fun validateTransferToWallet(device_id:String, model:String, customer_account: String, amount:String){
        viewModelScope.launch {
            withContext(dispatcher.io){
                validateTransferToWalletUseCase(
                    ValidateTransferToWalletUseCase.Param(
                        device_id, model, customer_account, amount
                    )).collect{
                    when(it){
                        is Resource.Loading -> _validateState.value = ValidateState.Loading
                        is Resource.Error -> _validateState.value =
                            ValidateState.Error(it.exception)
                        is Resource.Success -> _validateState.value = ValidateState.Success(it.data)
                    }
                }
            }
        }
    }

    fun confirmTransferToWallet(device_id:String, model:String, customer_account: String, amount:String, transaction_ref:String, transaction_token:String) {
        viewModelScope.launch {
            withContext(dispatcher.io) {
                transferToWalletUseCase(TransferToWalletUseCase.Param(device_id, model, customer_account, amount, transaction_ref, transaction_token)).collect {
                    when(it){
                        is Resource.Loading -> _transferState.value = TransferState.Loading
                        is Resource.Error -> _transferState.value =
                            TransferState.Error(it.exception)
                        is Resource.Success -> _transferState.value = TransferState.Success(it.data)
                    }
                }
            }
        }
    }


    fun validateTransferToMM(device_id:String, model:String, amount:String){
        viewModelScope.launch {
            withContext(dispatcher.io){
                validateMobileMoneyUseCase(
                    ValidateMobileMoneyUseCase.Param(
                        device_id, model, amount
                    )).collect{
                    when(it){
                        is Resource.Loading -> _validateMMState.value = ValidateMMState.Loading
                        is Resource.Error -> _validateMMState.value =
                            ValidateMMState.Error(it.exception)
                        is Resource.Success -> _validateMMState.value =
                            ValidateMMState.Success(it.data)
                    }
                }
            }
        }
    }

    fun confirmTransferToMM(device_id:String, model:String, amount:String, transaction_ref:String, transaction_token:String) {
        viewModelScope.launch {
            withContext(dispatcher.io) {
                confirmMobileMoneyUseCase(ConfirmMobileMoneyUseCase.Param(device_id, model, amount, transaction_ref, transaction_token)).collect {
                    when(it){
                        is Resource.Loading -> _confirmMMState.value = ConfirmMMState.Loading
                        is Resource.Error -> _confirmMMState.value =
                            ConfirmMMState.Error(it.exception)
                        is Resource.Success -> _confirmMMState.value =
                            ConfirmMMState.Success(it.data)
                    }
                }
            }
        }
    }



    override fun onCleared() {
        super.onCleared()
        _transferState.value = TransferState.Initial
    }
}

sealed class TransferState {
    object Initial: TransferState()
    object Loading: TransferState()
    data class Success(val data: String): TransferState()
    data class Error(val message: String): TransferState()
}

sealed class ValidateState {
    object Initial: ValidateState()
    object Loading: ValidateState()
    data class Success(val data: DepositEntity): ValidateState()
    data class Error(val message: String): ValidateState()
}

sealed class ConfirmMMState {
    object Initial: ConfirmMMState()
    object Loading: ConfirmMMState()
    data class Success(val data: String): ConfirmMMState()
    data class Error(val message: String): ConfirmMMState()
}

sealed class ValidateMMState {
    object Initial: ValidateMMState()
    object Loading: ValidateMMState()
    data class Success(val data: DepositEntity): ValidateMMState()
    data class Error(val message: String): ValidateMMState()
}