package com.mcash.client.presentation.ui.fuelSave

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.mcash.domain.dispacher.AppDispatcher
import com.mcash.domain.model.FuelSaveDriverEntity
import com.mcash.domain.model.sealed.Resource
import com.mcash.domain.repository.PreferenceRepository
import com.mcash.domain.usecases.fuelSave.GetFuelSaveDriverUseCase
import com.mcash.domain.usecases.fuelSave.RegisterFuelSaveDriverUSeCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class FuelSaveDriverViewModel @Inject constructor(
    private val getFuelSaveDriverUseCase: GetFuelSaveDriverUseCase,
    private val registerFuelSaveDriverUseCase: RegisterFuelSaveDriverUSeCase,
    private val preferenceRepository: PreferenceRepository
) : ViewModel() {

    private val _getFuelSaveDriverState =
        MutableStateFlow<GetFuelSaveDriverState>(GetFuelSaveDriverState.Initial)
    val getFuelSaveDriverState get() = _getFuelSaveDriverState.asLiveData()


    private val _registerFuelSaveDriverState =
        MutableStateFlow<RegisterFuelSaveDriverState>(RegisterFuelSaveDriverState.Initial)
    val registerFuelSaveDriverState get() = _registerFuelSaveDriverState.asLiveData()


    fun getFuelSaveDriver() {

        viewModelScope.launch {
            val driverCode = preferenceRepository.getDataStoreUser().first().phone
            withContext(Dispatchers.IO) {
                getFuelSaveDriverUseCase.invoke(GetFuelSaveDriverUseCase.Param(driverCode))
                    .collect {
                        when (it) {
                            is Resource.Loading -> {
                                _getFuelSaveDriverState.value = GetFuelSaveDriverState.Loading
                            }

                            is Resource.Error -> {
                                _getFuelSaveDriverState.value =
                                    GetFuelSaveDriverState.Error(it.exception)
                            }

                            is Resource.Success -> {
                                _getFuelSaveDriverState.value =
                                    GetFuelSaveDriverState.Success(it.data)
                            }
                        }
                    }
            }
        }
    }

    fun registerFuelSaveDriver(
        nin: String

    ) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                registerFuelSaveDriverUseCase.invoke(
                    RegisterFuelSaveDriverUSeCase.Param(

                        nin

                    )
                ).collect {
                    when (it) {
                        is Resource.Loading -> {
                            _registerFuelSaveDriverState.value = RegisterFuelSaveDriverState.Loading
                        }

                        is Resource.Error -> {
                            _registerFuelSaveDriverState.value =
                                RegisterFuelSaveDriverState.Error(it.exception)
                        }

                        is Resource.Success -> {
                            _registerFuelSaveDriverState.value =
                                RegisterFuelSaveDriverState.Success(it.data)
                        }
                    }
                }

            }
        }
    }


    sealed class GetFuelSaveDriverState {
        data object Initial : GetFuelSaveDriverState()
        data object Loading : GetFuelSaveDriverState()
        data class Error(val message: String) : GetFuelSaveDriverState()
        data class Success(val data: FuelSaveDriverEntity) : GetFuelSaveDriverState()
    }


    sealed class RegisterFuelSaveDriverState {
        data object Initial : RegisterFuelSaveDriverState()
        data object Loading : RegisterFuelSaveDriverState()
        data class Error(val message: String) : RegisterFuelSaveDriverState()
        data class Success(val data: Boolean) : RegisterFuelSaveDriverState()
    }
}