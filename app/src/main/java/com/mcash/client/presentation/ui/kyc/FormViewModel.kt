package com.mcash.client.presentation.ui.kyc

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.mcash.client.core.models.IdCardInfo
import com.mcash.domain.dispacher.AppDispatcher
import com.mcash.domain.model.CardInfoEntity
import com.mcash.domain.model.FormMapEntity
import com.mcash.domain.model.sealed.Resource
import com.mcash.domain.repository.KycRepository
import com.mcash.domain.repository.PreferenceRepository
import com.mcash.domain.usecases.kyc.SaveIdCardInfoUseCase
import com.mcash.domain.usecases.kyc.UploadKycDataUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.Normalizer.Form
import javax.inject.Inject

@HiltViewModel
class FormViewModel @Inject constructor(
    private val preferenceRepository: PreferenceRepository,
    private val dispatcher: AppDispatcher,
    private val saveIdCardInfoUseCase: SaveIdCardInfoUseCase,
    private val uploadKycDataUseCase: UploadKycDataUseCase
) : ViewModel() {

    private val _formUploadState = MutableStateFlow<UploadFormState>(UploadFormState.Initial)
    val formUploadState get() = _formUploadState.asLiveData()

    private val _idCardInfoSaveState = MutableStateFlow<IdCardSaveState>(IdCardSaveState.Initial)
    val idCardInfoSaveState get() = _idCardInfoSaveState.asLiveData()


    suspend fun saveKycFormData(data: String, formGroupLabel: String, type: String) {
        val formMapData = FormMapEntity(
            formGroupLabel = formGroupLabel,
            type = type,
            data = data
        )
        preferenceRepository.saveFormGroupData(formMapData)
    }

    suspend fun getFormSaveStatus(formGroupLabel: String): Boolean {
        val data = preferenceRepository.getFormGroupByLabel(formGroupLabel)
        return data.isNotEmpty()
    }

    suspend fun getSavedFormGroups(): List<FormMapEntity> {
        return preferenceRepository.getAllFormGroupData()
    }

    fun uploadKycForm(
        deviceId: String,
        model: String,
        androidVersion: String,
        longitude: String,
        latitude: String
    ) {
        viewModelScope.launch {
            withContext(dispatcher.io) {
                uploadKycDataUseCase(
                    UploadKycDataUseCase.Param(
                        deviceId,
                        model,
                        androidVersion,
                        longitude,
                        latitude
                    )
                ).collect {
                    listOf(
                        when (it) {
                            is Resource.Loading -> {
                                _formUploadState.value = UploadFormState.Loading
                            }

                            is Resource.Success -> {
                                _formUploadState.value = UploadFormState.Success(it.data)
                            }

                            is Resource.Error -> {
                                _formUploadState.value = UploadFormState.Error(it.exception)
                            }
                        }
                    )
                }
            }
        }
    }

    suspend fun saveCardInfo(data: IdCardInfo) {
        val card = CardInfoEntity(
            lastName = data.lastName,
            firstName = data.firstName,
            middleName = data.middleName,
            expiryDate = data.expiryDate,
            issueDate = data.expiryDate,
            dateOfBirth = data.dateOfBirth,
            gender = data.gender,
            nationality = data.nationality,
            nin = data.nin,
            cardNumber = data.cardNumber
        )
        preferenceRepository.saveIdCard(card)
        println("=== saved Card Info: ${preferenceRepository.getIdCard(data.nin)}")
    }

    suspend fun getIdCardInfo(nin: String): CardInfoEntity {
        return preferenceRepository.getIdCard(nin)
    }

    fun saveIdCardData(data: IdCardInfo) {
        viewModelScope.launch {
            withContext(dispatcher.io) {
                saveIdCardInfoUseCase.invoke(
                    CardInfoEntity(
                        lastName = data.lastName,
                        firstName = data.firstName,
                        middleName = data.middleName,
                        expiryDate = data.expiryDate,
                        issueDate = data.expiryDate,
                        dateOfBirth = data.dateOfBirth,
                        gender = data.gender,
                        nationality = data.nationality,
                        nin = data.nin,
                        cardNumber = data.cardNumber
                    )
                )
                    .collect {
                        when (it) {
                            is Resource.Loading -> {
                                _idCardInfoSaveState.value = IdCardSaveState.Loading
                            }

                            is Resource.Error -> {
                                _idCardInfoSaveState.value = IdCardSaveState.Error(it.exception)
                            }

                            is Resource.Success -> {
                                _idCardInfoSaveState.value = IdCardSaveState.Success(it.data)
                            }
                        }
                    }
            }
        }
    }

    sealed class UploadFormState {
        object Initial : UploadFormState()
        object Loading : UploadFormState()
        data class Success(val data: Boolean) : UploadFormState()
        data class Error(val message: String) : UploadFormState()
    }

    sealed class IdCardSaveState {
        object Initial : IdCardSaveState()
        object Loading : IdCardSaveState()
        data class Success(val data: CardInfoEntity) : IdCardSaveState()
        data class Error(val message: String) : IdCardSaveState()
    }

}