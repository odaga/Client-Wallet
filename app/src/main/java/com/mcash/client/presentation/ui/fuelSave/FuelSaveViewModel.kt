package com.mcash.client.presentation.ui.fuelSave

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.mcash.domain.dispacher.AppDispatcher
import com.mcash.domain.model.Branch
import com.mcash.domain.model.FuelSaveTransaction
import com.mcash.domain.model.ValidateFuelPurchaseEntity
import com.mcash.domain.model.sealed.Resource
import com.mcash.domain.usecases.fuelSave.ConfirmFuelSavePurchaseUseCase
import com.mcash.domain.usecases.fuelSave.GetBranchDetailsUseCase
import com.mcash.domain.usecases.fuelSave.GetDriverLoyaltyPointsUseCase
import com.mcash.domain.usecases.fuelSave.ValidateFuelSavePurchaseUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class FuelSaveViewModel @Inject constructor(
    private val getBranchDetailsUseCase: GetBranchDetailsUseCase,
    private val dispatcher: AppDispatcher,
    private val validateFuelSavePurchaseUseCase: ValidateFuelSavePurchaseUseCase,
    private val confirmFuelSavePurchaseUseCase: ConfirmFuelSavePurchaseUseCase,
    private val getDriverLoyaltyPointsUseCase: GetDriverLoyaltyPointsUseCase
) : ViewModel() {

    private val _branchDetailState =
        MutableStateFlow<BranchDetailsState>(BranchDetailsState.Initial)
    val branchDetailState get() = _branchDetailState.asLiveData()

    private val _validateFuelSavePurchaseState =
        MutableStateFlow<ValidateFuelSavePurchaseState>(ValidateFuelSavePurchaseState.Initial)
    val validateFuelSavePurchaseState get() = _validateFuelSavePurchaseState.asLiveData()

    private val _confirmFuelSavePurchaseState =
        MutableStateFlow<ConfirmFuelSavePurchaseState>(ConfirmFuelSavePurchaseState.Initial)
    val confirmFuelSavePurchaseState get() = _confirmFuelSavePurchaseState.asLiveData()

    private val _loyaltyPointsState =
        MutableStateFlow<LoyaltyPointsState>(LoyaltyPointsState.Initial)
    val loyaltyPointsState get() = _loyaltyPointsState.asLiveData()

    fun getBranchDetails(branchId: String) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                getBranchDetailsUseCase.invoke(GetBranchDetailsUseCase.Param(branchId))
                    .collect { branchDetails ->

                        when (branchDetails) {
                            is Resource.Loading -> {
                                _branchDetailState.value = BranchDetailsState.Loading
                            }

                            is Resource.Error -> {
                                _branchDetailState.value =
                                    BranchDetailsState.Error(branchDetails.exception)
                            }

                            is Resource.Success -> {
                                _branchDetailState.value =
                                    BranchDetailsState.Success(branchDetails.data)
                            }
                        }
                    }
            }
        }
    }

    fun validateFuelSavePurchase(
        deviceId: String,
        model: String,
        imei: String,
        longitude: String,
        latitude: String,
        branchCode: String,
        amount: String,
        fuelTypeId: String
    ) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                validateFuelSavePurchaseUseCase.invoke(
                    ValidateFuelSavePurchaseUseCase.Param(
                        deviceId,
                        model,
                        imei,
                        longitude,
                        latitude,
                        branchCode,
                        amount,
                        fuelTypeId
                    )
                ).collect {
                    when (it) {
                        is Resource.Loading -> {
                            _validateFuelSavePurchaseState.value =
                                ValidateFuelSavePurchaseState.Loading
                        }

                        is Resource.Error -> {
                            _validateFuelSavePurchaseState.value =
                                ValidateFuelSavePurchaseState.Error(it.exception)
                        }

                        is Resource.Success -> {
                            _validateFuelSavePurchaseState.value =
                                ValidateFuelSavePurchaseState.Success(it.data)
                        }

                        else -> Unit
                    }
                }
            }

        }
    }


    fun confirmFuelSavePurchase(
        deviceId: String,
        model: String,
        imei: String,
        longitude: String,
        latitude: String,
        pin: String,
        branchCode: String,
        amount: String,
        fuelTypeId: String,
        indicator: String
    ) {
        viewModelScope.launch {
            withContext(dispatcher.io) {
                confirmFuelSavePurchaseUseCase.invoke(
                    ConfirmFuelSavePurchaseUseCase.Param(
                        deviceId,
                        model,
                        imei,
                        longitude,
                        latitude,
                        pin,
                        branchCode,
                        amount,
                        fuelTypeId,
                        indicator
                    )
                ).collect {
                    when (it) {
                        is Resource.Loading -> {
                            _confirmFuelSavePurchaseState.value =
                                ConfirmFuelSavePurchaseState.Loading
                        }

                        is Resource.Error -> {
                            _confirmFuelSavePurchaseState.value =
                                ConfirmFuelSavePurchaseState.Error(it.exception)
                        }

                        is Resource.Success -> {
                            _confirmFuelSavePurchaseState.value =
                                ConfirmFuelSavePurchaseState.Success(it.data)
                        }
                    }
                }
            }
        }
    }

    fun getLoyaltyPoints(
        deviceId: String,
        model: String,
        imei: String,
        longitude: String,
        latitude: String
    ) {
        viewModelScope.launch {
            withContext(dispatcher.io) {
                getDriverLoyaltyPointsUseCase.invoke(
                    GetDriverLoyaltyPointsUseCase.Param(
                        deviceId,
                        model,
                        imei,
                        longitude,
                        latitude
                    )
                ).collect {
                    when (it) {
                        is Resource.Loading -> {
                            _loyaltyPointsState.value =
                                LoyaltyPointsState.Loading
                        }

                        is Resource.Error -> {
                            _loyaltyPointsState.value =
                                LoyaltyPointsState.Error(it.exception)
                        }

                        is Resource.Success -> {
                            _loyaltyPointsState.value =
                                LoyaltyPointsState.Success(it.data)
                        }
                    }
                }
            }
        }
    }


    sealed class BranchDetailsState {
        object Initial : BranchDetailsState()
        object Loading : BranchDetailsState()
        data class Error(val message: String) : BranchDetailsState()
        data class Success(
            val data: Branch
        ) : BranchDetailsState()
    }

    sealed class ValidateFuelSavePurchaseState {
        object Initial : ValidateFuelSavePurchaseState()
        object Loading : ValidateFuelSavePurchaseState()
        data class Error(val message: String) : ValidateFuelSavePurchaseState()
        data class Success(
            val data: ValidateFuelPurchaseEntity
        ) : ValidateFuelSavePurchaseState()
    }

    sealed class ConfirmFuelSavePurchaseState {
        object Initial : ConfirmFuelSavePurchaseState()
        object Loading : ConfirmFuelSavePurchaseState()
        data class Error(val message: String) : ConfirmFuelSavePurchaseState()
        data class Success(
            val data: FuelSaveTransaction
        ) : ConfirmFuelSavePurchaseState()
    }

    sealed class LoyaltyPointsState {
        object Initial : LoyaltyPointsState()
        object Loading : LoyaltyPointsState()
        data class Error(val message: String) : LoyaltyPointsState()
        data class Success(
            val data: Float
        ) : LoyaltyPointsState()
    }
}