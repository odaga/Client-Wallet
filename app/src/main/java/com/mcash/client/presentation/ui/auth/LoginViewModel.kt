package com.mcash.client.presentation.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.mcash.domain.dispacher.AppDispatcher
import com.mcash.domain.model.UserEntity
import com.mcash.domain.model.sealed.Resource
import com.mcash.domain.repository.PreferenceRepository
import com.mcash.domain.usecases.auth.AuthenticateUseCase
import com.mcash.domain.usecases.auth.ChangePinUseCase
import com.mcash.domain.usecases.auth.VerifyOtpUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authenticateUseCase: AuthenticateUseCase,
    private val verifyOtpUseCase: VerifyOtpUseCase,
    private val changePinUseCase: ChangePinUseCase,
    private val dispatcher: AppDispatcher,
    private val preference: PreferenceRepository
) : ViewModel() {

    private val _loginState = MutableStateFlow<LoginState>(LoginState.Initial)
    val loginState get() = _loginState.asLiveData()

    private val _verifyOtpState = MutableStateFlow<VerifyOtpState>(VerifyOtpState.Initial)
    val verifyOtpState get() = _verifyOtpState.asLiveData()

    private val _changePinState = MutableStateFlow<ChangePinState>(ChangePinState.Initial)
    val changePinState get() = _changePinState.asLiveData()

    fun authenticateUser(
        phone: String,
        pin: String,
        device_id: String,
        model: String,
        device_token: String
    ) {
        viewModelScope.launch {
            withContext(dispatcher.io) {
                authenticateUseCase(
                    AuthenticateUseCase.Param(
                        phone,
                        pin,
                        device_id,
                        model,
                        device_token
                    )
                ).collect {
                    when (it) {
                        is Resource.Loading -> _loginState.value = LoginState.Loading
                        is Resource.Success -> _loginState.value = LoginState.Success(it.data)
                        is Resource.Error -> _loginState.value = LoginState.Error(it.exception)
                    }
                }
            }
        }
    }

    fun verifyUserOtp(
        phone: String,
        pin: String,
        otp: String,
        device_id: String,
        model: String,
        device_token: String
    ) {
        viewModelScope.launch {
            withContext(dispatcher.io) {
                verifyOtpUseCase(
                    VerifyOtpUseCase.Param(
                        phone,
                        pin,
                        otp,
                        device_id,
                        model,
                        device_token
                    )
                ).collect {
                    when (it) {
                        is Resource.Loading -> _verifyOtpState.value = VerifyOtpState.Loading
                        is Resource.Success -> _verifyOtpState.value =
                            VerifyOtpState.Success(it.data)

                        is Resource.Error -> _verifyOtpState.value =
                            VerifyOtpState.Error(it.exception)
                    }
                }
            }
        }
    }

    fun changePin(oldPin: String, newPin: String, device_id: String, model: String) {
        viewModelScope.launch {
            withContext(dispatcher.io) {
                changePinUseCase(ChangePinUseCase.Param(oldPin, newPin, device_id, model)).collect {
                    when (it) {
                        is Resource.Loading -> _changePinState.value = ChangePinState.Loading
                        is Resource.Error -> _changePinState.value =
                            ChangePinState.Error(it.exception)

                        is Resource.Success -> _changePinState.value =
                            ChangePinState.Success(it.data)
                    }
                }
            }
        }
    }

    suspend fun saveLoginState() {
        preference.saveSignInState()
    }

    suspend fun getAuthState(): Boolean {
        return preference.getSignInState().first()
    }

//    fun saveUserToPref(user: User) {
//        viewModelScope.launch {
//            withContext(dispatcher.io) {
//                saveLocalUserCase(user)
//            }
//        }
//    }
}

sealed class LoginState {
    object Initial : LoginState()
    object Loading : LoginState()
    data class Success(val data: Int) : LoginState()
    data class Error(val message: String) : LoginState()
}

sealed class VerifyOtpState {
    object Initial : VerifyOtpState()
    object Loading : VerifyOtpState()
    data class Success(val data: UserEntity) : VerifyOtpState()
    data class Error(val message: String) : VerifyOtpState()
}

sealed class ChangePinState {
    object Initial : ChangePinState()
    object Loading : ChangePinState()
    data class Success(val data: String) : ChangePinState()
    data class Error(val message: String) : ChangePinState()
}