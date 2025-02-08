package com.mcash.client.presentation.ui.topup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.mcash.domain.dispacher.AppDispatcher
import com.mcash.domain.model.DepositEntity
import com.mcash.domain.model.sealed.Resource
import com.mcash.domain.usecases.account.GetSavedBalanceUseCase
import com.mcash.domain.usecases.deposit.DepositToWalletUseCase
import com.mcash.domain.usecases.deposit.ValidateMMNumberUseCase
import com.mcash.domain.usecases.user.GetSavedUserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class TopupViewModel @Inject constructor(
    private val dispatcher: AppDispatcher,
    private val validateMMNumberUseCase: ValidateMMNumberUseCase,
    private val depositToWalletUseCase: DepositToWalletUseCase,
    private val getSavedBalanceUseCase: GetSavedBalanceUseCase,
    private val getSavedUserUseCase: GetSavedUserUseCase,
) : ViewModel() {

    private val _depositState = MutableStateFlow<DepositState>(DepositState.Initial)
    val depositState get() = _depositState.asLiveData()

    private val _validateState = MutableStateFlow<ValidateState>(ValidateState.Initial)
    val validateState get() = _validateState.asLiveData()

    val savedBalance = runBlocking(dispatcher.io) { getSavedBalanceUseCase().first() }

    val savedUser = runBlocking(dispatcher.io) { getSavedUserUseCase().first() }

    fun validateNumber(device_id:String, model:String, mobile_account: String, amount:String){
        viewModelScope.launch {
            withContext(dispatcher.io){
                validateMMNumberUseCase(
                    ValidateMMNumberUseCase.Param(
                    device_id, model, mobile_account, amount
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

    fun depositToWallet(device_id:String, model:String, mobile_account: String, amount:String, transaction_ref:String, transaction_token:String) {
        viewModelScope.launch {
            withContext(dispatcher.io) {

                depositToWalletUseCase(DepositToWalletUseCase.Param(device_id, model, mobile_account, amount, transaction_ref, transaction_token)).collect {
                    when(it){
                        is Resource.Loading -> _depositState.value = DepositState.Loading
                        is Resource.Error -> _depositState.value =
                            DepositState.Error(it.exception)
                        is Resource.Success -> _depositState.value = DepositState.Success(it.data)
                    }
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        _depositState.value = DepositState.Initial
    }
}

sealed class DepositState {
    object Initial: DepositState()
    object Loading: DepositState()
    data class Success(val data: String): DepositState()
    data class Error(val message: String): DepositState()
}

sealed class ValidateState {
    object Initial: ValidateState()
    object Loading: ValidateState()
    data class Success(val data: DepositEntity): ValidateState()
    data class Error(val message: String): ValidateState()
}