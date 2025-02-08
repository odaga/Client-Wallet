package com.mcash.client.presentation.ui.utilities.nwsc

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.mcash.domain.dispacher.AppDispatcher
import com.mcash.domain.model.*
import com.mcash.domain.model.sealed.Resource
import com.mcash.domain.usecases.account.GetSavedBalanceUseCase
import com.mcash.domain.usecases.nwsc.ConfirmNwscUseCase
import com.mcash.domain.usecases.nwsc.GetSavedNwscAreasUseCase
import com.mcash.domain.usecases.nwsc.ValidateNwscCustomerUseCase
import com.mcash.domain.usecases.nwsc.ValidateNwscUseCase
import com.mcash.domain.usecases.user.GetSavedUserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class NwscViewModel @Inject constructor(
    private val dispatcher: AppDispatcher,
    private val getSavedUserUseCase: GetSavedUserUseCase,
    private val getSavedNwscAreasUseCase: GetSavedNwscAreasUseCase,
    private val getSavedBalanceUseCase: GetSavedBalanceUseCase,
    private val validateNwscCustomerUseCase: ValidateNwscCustomerUseCase,
    private val validateNwscUseCase: ValidateNwscUseCase,
    private val confirmNwscUseCase: ConfirmNwscUseCase
) : ViewModel() {

    var selectedArea: String? = null
    var selectedMeterNumber: String? = null
    var selectedAmount: Double = 0.0

    val savedBalance = runBlocking(dispatcher.io) { getSavedBalanceUseCase().first() }

    val savedUser = runBlocking(dispatcher.io) { getSavedUserUseCase().first() }

    val savedAreas = runBlocking(dispatcher.io) { getSavedNwscAreasUseCase().first() }


    private val _validateNwscCustomerState = MutableStateFlow<ValidateNwscCustomerState>(
        ValidateNwscCustomerState.Initial
    )
    val validateNwscCustomerState get() = _validateNwscCustomerState.asLiveData()

    private val _validateNwscState = MutableStateFlow<ValidateNwscState>(ValidateNwscState.Initial)
    val validateNwscState get() = _validateNwscState.asLiveData()

    private val _confirmNwscState = MutableStateFlow<ConfirmNwscState>(ConfirmNwscState.Initial)
    val confirmNwscState get() = _confirmNwscState.asLiveData()


    fun validateNwscCustomer(device_id:String, model:String, customer_account: String, customer_phone:String, area:String){
        viewModelScope.launch {
            withContext(dispatcher.io){
                validateNwscCustomerUseCase.invoke(ValidateNwscCustomerUseCase.Param(device_id, model,customer_account, customer_phone, area)).collect {
                    when(it){
                        is Resource.Loading -> _validateNwscCustomerState.value =
                            ValidateNwscCustomerState.Loading
                        is Resource.Error -> _validateNwscCustomerState.value =
                            ValidateNwscCustomerState.Error(it.exception)
                        is Resource.Success -> _validateNwscCustomerState.value =
                            ValidateNwscCustomerState.Success(it.data)
                    }
                }

            }
        }

    }

    fun validateNwscPayment(device_id:String, model:String, customer_account: String, customer_phone:String, area:String, customer_name:String, outstanding_balance:String, amount:String){
        viewModelScope.launch {
            withContext(dispatcher.io){
                validateNwscUseCase.invoke(ValidateNwscUseCase.Param(device_id, model, customer_account, customer_phone, area, customer_name, outstanding_balance, amount)).collect {
                    when(it){
                        is Resource.Loading -> _validateNwscState.value = ValidateNwscState.Loading
                        is Resource.Error -> _validateNwscState.value =
                            ValidateNwscState.Error(it.exception)
                        is Resource.Success -> _validateNwscState.value =
                            ValidateNwscState.Success(it.data)
                    }
                }

            }
        }

    }

    fun confirmNwscPayment(device_id:String, model:String, customer_account: String, customer_phone:String, area:String, customer_name:String, outstanding_balance:String, amount:String, transaction_token:String, transaction_ref:String){
        viewModelScope.launch {
            withContext(dispatcher.io){
                confirmNwscUseCase.invoke(ConfirmNwscUseCase.Param(device_id, model, customer_account,customer_phone, area, customer_name, outstanding_balance, amount, transaction_token, transaction_ref)).collect {
                    when(it){
                        is Resource.Loading -> _confirmNwscState.value = ConfirmNwscState.Loading
                        is Resource.Error -> _confirmNwscState.value =
                            ConfirmNwscState.Error(it.exception)
                        is Resource.Success -> _confirmNwscState.value =
                            ConfirmNwscState.Success(it.data)
                    }
                }
            }
        }
    }


}

sealed class ValidateNwscCustomerState {
    object Initial : ValidateNwscCustomerState()
    object Loading : ValidateNwscCustomerState()
    data class Success(val data: NwscCustomerEntity) : ValidateNwscCustomerState()
    data class Error(val message: String) : ValidateNwscCustomerState()
}

sealed class ValidateNwscState {
    object Initial : ValidateNwscState()
    object Loading : ValidateNwscState()
    data class Success(val data: NwscValidateEntity) : ValidateNwscState()
    data class Error(val message: String) : ValidateNwscState()
}

sealed class ConfirmNwscState {
    object Initial : ConfirmNwscState()
    object Loading : ConfirmNwscState()
    data class Success(val data: NwscConfirmEntity) : ConfirmNwscState()
    data class Error(val message: String) : ConfirmNwscState()
}