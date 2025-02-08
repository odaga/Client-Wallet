package com.mcash.client.presentation.ui.utilities

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.mcash.data.local.room.dao.DataDao
import com.mcash.data.local.room.dao.TvDao
import com.mcash.data.local.room.dao.VoiceDao
import com.mcash.domain.dispacher.AppDispatcher
import com.mcash.domain.model.*
import com.mcash.domain.model.sealed.Resource
import com.mcash.domain.usecases.account.GetSavedBalanceUseCase
import com.mcash.domain.usecases.umeme.ConfirmUmemeUseCase
import com.mcash.domain.usecases.umeme.ValidateUmemeUseCase
import com.mcash.domain.usecases.user.GetSavedUserUseCase
import com.mcash.domain.usecases.utility.GetPackagesUseCase
import com.mcash.domain.usecases.utility.airtime.AirtimeUseCase
import com.mcash.domain.usecases.utility.airtime.ValidateAirtimeUseCase
import com.mcash.domain.usecases.utility.tv.ValidateTVUseCase
import com.mcash.domain.usecases.utility.data.ValidateDataUseCase
import com.mcash.domain.usecases.utility.ura.ValidateUraUseCase
import com.mcash.domain.usecases.utility.voice.ValidateVoiceUseCase
import com.mcash.domain.usecases.utility.data.ConfirmDataUseCase
import com.mcash.domain.usecases.utility.data.GetDataBundlesUsCase
import com.mcash.domain.usecases.utility.tv.ConfirmTVUseCase
import com.mcash.domain.usecases.utility.tv.GetTvPackagesUseCase
import com.mcash.domain.usecases.utility.ura.ConfirmUraUseCase
import com.mcash.domain.usecases.utility.voice.ConfirmVoiceUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class UtilityViewModel @Inject constructor(
    private val voiceDao: VoiceDao,
    private val tvDao: TvDao,
    private val dataDao: DataDao,
    private val dispatcher: AppDispatcher,
    private val getSavedUserUseCase: GetSavedUserUseCase,
    private val getSavedBalanceUseCase: GetSavedBalanceUseCase,
    private val validateAirtimeUseCase: ValidateAirtimeUseCase,
    private val airtimeUseCase: AirtimeUseCase,
    private val validateVoiceUseCase: ValidateVoiceUseCase,
    private val confirmVoiceUseCase: ConfirmVoiceUseCase,
    private val validateUmemeUseCase: ValidateUmemeUseCase,
    private val confirmUmemeUseCase: ConfirmUmemeUseCase,
    private val validateTVUseCase: ValidateTVUseCase,
    private val confirmTVUseCase: ConfirmTVUseCase,
    private val validateDataUseCase: ValidateDataUseCase,
    private val confirmDataUseCase: ConfirmDataUseCase,
    private val validateUraUseCase: ValidateUraUseCase,
    private val confirmUraUseCase: ConfirmUraUseCase,
    private val getDataBundlesUsCase: GetDataBundlesUsCase,
    private val getTvPackagesUseCase: GetTvPackagesUseCase
) : ViewModel() {

    val _validateAirtimeState = MutableStateFlow<ValidateAirtimeState>(ValidateAirtimeState.Initial)
    val validateAirtimeState get() = _validateAirtimeState.asLiveData()

    val _buyAirtimeState = MutableStateFlow<BuyAirtimeState>(BuyAirtimeState.Initial)
    val buyAirtimeState get() = _buyAirtimeState.asLiveData()

    private val _validateVoiceState =
        MutableStateFlow<ValidateVoiceState>(ValidateVoiceState.Initial)
    val validateVoiceState get() = _validateVoiceState.asLiveData()

    private val _confirmVoiceState = MutableStateFlow<ConfirmVoiceState>(ConfirmVoiceState.Initial)
    val confirmVoiceState get() = _confirmVoiceState.asLiveData()

    private val _validateTVState = MutableStateFlow<ValidateTVState>(ValidateTVState.Initial)
    val validateTVState get() = _validateTVState.asLiveData()

    private val _confirmTVState = MutableStateFlow<ConfirmTVState>(ConfirmTVState.Initial)
    val confirmTVState get() = _confirmTVState.asLiveData()


    private val _validateDataState = MutableStateFlow<ValidateDataState>(ValidateDataState.Initial)
    val validateDataState get() = _validateDataState.asLiveData()

    private val _confirmDataState = MutableStateFlow<ConfirmDataState>(ConfirmDataState.Initial)
    val confirmDataState get() = _confirmDataState.asLiveData()

    private val _validateUraState = MutableStateFlow<ValidateUraState>(ValidateUraState.Initial)
    val validateUraState get() = _validateUraState.asLiveData()

    private val _confirmUraState = MutableStateFlow<ConfirmUraState>(ConfirmUraState.Initial)
    val confirmUraState get() = _confirmUraState.asLiveData()

    private val _validateUmemeState =
        MutableStateFlow<ValidateUmemeState>(ValidateUmemeState.Initial)
    val validateUmemeState get() = _validateUmemeState.asLiveData()

    private val _confirmUmemeState = MutableStateFlow<ConfirmUmemeState>(ConfirmUmemeState.Initial)
    val confirmUmemeState get() = _confirmUmemeState.asLiveData()

    private val _datePackagesState = MutableStateFlow<DatePackagesState>(DatePackagesState.Initial)
    val datePackagesState get() = _datePackagesState.asLiveData()

    private val _tvPackagesState = MutableStateFlow<TvPackagesState>(TvPackagesState.Initial)
    val tvPackagesState get() = _tvPackagesState.asLiveData()


    val savedUser = runBlocking(dispatcher.io) { getSavedUserUseCase().first() }

    val savedBalance = runBlocking(dispatcher.io) { getSavedBalanceUseCase().first() }

    fun getTVList(): List<TvEntity> {
        return tvDao.getTvPackages()
    }

    fun getTVPackages(tv: String): List<TvEntity> {
        return tvDao.searchTvPackage(tv)
    }

    fun fetchTvPackages(tvProviders: List<String>) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                getTvPackagesUseCase(GetTvPackagesUseCase.Param(tvProviders)).collect {
                    when (it) {
                        is Resource.Loading -> _tvPackagesState.value = TvPackagesState.Loading
                        is Resource.Error -> _tvPackagesState.value =
                            TvPackagesState.Error(it.exception)

                        is Resource.Success -> _tvPackagesState.value =
                            TvPackagesState.Success(it.data)
                    }
                }
            }
        }
    }


    fun getTvPackageByProvider(provider: String): List<TvEntity> {
        val list = tvDao.getTvPackageByProvider(provider)
        println(list)
        return list
    }




    fun getVoiceBundles(): List<VoiceEntity> {
        return voiceDao.getVoiceBundle()
    }

    fun getDataBundlesByProvider(type: String): List<DataEntity> {
        return dataDao.getDataBundleByType(type).data
    }

    fun fetchDataBundles(providerTypes: List<String>) {
        viewModelScope.launch {
            withContext(dispatcher.io) {
                getDataBundlesUsCase(GetDataBundlesUsCase.Param(providerTypes)).collect {
                    when (it) {
                        is Resource.Loading -> _datePackagesState.value = DatePackagesState.Loading
                        is Resource.Error -> _datePackagesState.value =
                            DatePackagesState.Error(it.exception)

                        is Resource.Success -> _datePackagesState.value =
                            DatePackagesState.Success(it.data)
                    }
                }
            }
        }
    }

    fun validateAirtime(
        device_id: String,
        model: String,
        customer_account: String,
        amount: String
    ) {
        viewModelScope.launch {
            withContext(dispatcher.io) {
                validateAirtimeUseCase(
                    ValidateAirtimeUseCase.Param(
                        device_id,
                        model,
                        customer_account,
                        amount
                    )
                ).collect {
                    when (it) {
                        is Resource.Loading -> _validateAirtimeState.value =
                            ValidateAirtimeState.Loading

                        is Resource.Error -> _validateAirtimeState.value =
                            ValidateAirtimeState.Error(it.exception)

                        is Resource.Success -> _validateAirtimeState.value =
                            ValidateAirtimeState.Success(it.data)
                    }
                }
            }
        }
    }

    fun buyAirtime(
        device_id: String,
        model: String,
        customer_account: String,
        amount: String,
        transaction_ref: String,
        short_transaction_ref: String,
        transaction_token: String
    ) {
        viewModelScope.launch {
            withContext(dispatcher.io) {
                val param = AirtimeUseCase.Param(
                    device_id,
                    model,
                    customer_account,
                    amount,
                    transaction_ref,
                    short_transaction_ref,
                    transaction_token
                )
                airtimeUseCase(param).collect {
                    when (it) {
                        is Resource.Loading -> _buyAirtimeState.value = BuyAirtimeState.Loading
                        is Resource.Success -> _buyAirtimeState.value =
                            BuyAirtimeState.Success(it.data)

                        is Resource.Error -> _buyAirtimeState.value =
                            BuyAirtimeState.Error(it.exception)
                    }
                }
            }
        }
    }

    suspend fun validateVoiceCustomer(
        type: String,
        device_id: String,
        model: String,
        customer_account: String,
        package_id: String,
        amount: String
    ) {
        viewModelScope.launch {
            withContext(dispatcher.io) {
                validateVoiceUseCase.invoke(
                    ValidateVoiceUseCase.Param(
                        type,
                        device_id,
                        model,
                        customer_account,
                        package_id,
                        amount
                    )
                ).collect {
                    when (it) {
                        is Resource.Loading -> _validateVoiceState.value =
                            ValidateVoiceState.Loading

                        is Resource.Error -> _validateVoiceState.value =
                            ValidateVoiceState.Error(it.exception)

                        is Resource.Success -> _validateVoiceState.value =
                            ValidateVoiceState.Success(it.data)
                    }
                }
            }
        }

    }

    fun confirmVoicePayment(
        type: String,
        device_id: String,
        model: String,
        customer_account: String,
        package_id: String,
        amount: String,
        transaction_ref: String,
        short_transaction_ref: String,
        transaction_token: String
    ) {
        viewModelScope.launch {
            withContext(dispatcher.io) {
                confirmVoiceUseCase.invoke(
                    ConfirmVoiceUseCase.Param(
                        type,
                        device_id,
                        model,
                        customer_account,
                        package_id,
                        amount,
                        transaction_ref,
                        short_transaction_ref,
                        transaction_token
                    )
                ).collect {
                    when (it) {
                        is Resource.Loading -> _confirmVoiceState.value = ConfirmVoiceState.Loading
                        is Resource.Error -> _confirmVoiceState.value =
                            ConfirmVoiceState.Error(it.exception)

                        is Resource.Success -> _confirmVoiceState.value =
                            ConfirmVoiceState.Success(it.data)
                    }
                }
            }
        }
    }

    fun validateUmemeCustomer(
        device_id: String,
        model: String,
        customer_account: String,
        customer_phone: String,
        amount: String
    ) {
        viewModelScope.launch {
            withContext(dispatcher.io) {
                validateUmemeUseCase.invoke(
                    ValidateUmemeUseCase.Param(
                        device_id,
                        model,
                        customer_account,
                        customer_phone,
                        amount
                    )
                ).collectLatest {
                    when (it) {
                        is Resource.Loading -> _validateUmemeState.value =
                            ValidateUmemeState.Loading

                        is Resource.Error -> _validateUmemeState.value =
                            ValidateUmemeState.Error(it.exception)

                        is Resource.Success -> _validateUmemeState.value =
                            ValidateUmemeState.Success(it.data)
                    }
                }

            }
        }

    }

    fun confirmUmemePayment(
        device_id: String,
        model: String,
        customer_account: String,
        customer_phone: String,
        amount: String,
        transaction_ref: String,
        short_transaction_ref: String,
        transaction_token: String
    ) {
        viewModelScope.launch {
            withContext(dispatcher.io) {
                confirmUmemeUseCase.invoke(
                    ConfirmUmemeUseCase.Param(
                        device_id,
                        model,
                        customer_account,
                        customer_phone,
                        amount,
                        transaction_ref,
                        short_transaction_ref,
                        transaction_token
                    )
                ).collectLatest {
                    when (it) {
                        is Resource.Loading -> _confirmUmemeState.value = ConfirmUmemeState.Loading
                        is Resource.Error -> _confirmUmemeState.value =
                            ConfirmUmemeState.Error(it.exception)

                        is Resource.Success -> _confirmUmemeState.value =
                            ConfirmUmemeState.Success(it.data)
                    }
                }
            }
        }
    }


    suspend fun validateTVCustomer(
        type: String,
        device_id: String,
        model: String,
        customer_account: String,
        package_id: String,
        amount: String
    ) {
        viewModelScope.launch {
            withContext(dispatcher.io) {
                validateTVUseCase.invoke(
                    ValidateTVUseCase.Param(
                        type,
                        device_id,
                        model,
                        customer_account,
                        package_id,
                        amount
                    )
                ).collect {
                    when (it) {
                        is Resource.Loading -> _validateTVState.value = ValidateTVState.Loading
                        is Resource.Error -> _validateTVState.value =
                            ValidateTVState.Error(it.exception)

                        is Resource.Success -> _validateTVState.value =
                            ValidateTVState.Success(it.data)
                    }
                }
            }
        }

    }

    fun confirmTVPayment(
        type: String,
        deviceId: String,
        model: String,
        customerAccount: String,
        pin:String,
        packageId: String,
        amount: String,
        transactionRef: String,
        shortTransactionRef: String,
        transactionToken: String
    ) {
        viewModelScope.launch {
            withContext(dispatcher.io) {
                confirmTVUseCase.invoke(
                    ConfirmTVUseCase.Param(
                        type,
                        deviceId,
                        model,
                        customerAccount,
                        pin,
                        packageId,
                        amount,
                        transactionRef,
                        shortTransactionRef,
                        transactionToken
                    )
                ).collect {
                    when (it) {
                        is Resource.Loading -> _confirmTVState.value = ConfirmTVState.Loading
                        is Resource.Error -> _confirmTVState.value =
                            ConfirmTVState.Error(it.exception)

                        is Resource.Success -> _confirmTVState.value =
                            ConfirmTVState.Success(it.data)
                    }
                }
            }
        }
    }


    suspend fun validateDataCustomer(
        type: String,
        device_id: String,
        model: String,
        customer_account: String,
        package_id: String,
        amount: String
    ) {
        viewModelScope.launch {
            withContext(dispatcher.io) {
                validateDataUseCase.invoke(
                    ValidateDataUseCase.Param(
                        type,
                        device_id,
                        model,
                        customer_account,
                        package_id,
                        amount
                    )
                ).collect {
                    when (it) {
                        is Resource.Loading -> _validateDataState.value = ValidateDataState.Loading
                        is Resource.Error -> _validateDataState.value =
                            ValidateDataState.Error(it.exception)

                        is Resource.Success -> _validateDataState.value =
                            ValidateDataState.Success(it.data)
                    }
                }
            }
        }

    }

    fun confirmDataPayment(
        type: String,
        device_id: String,
        model: String,
        customer_account: String,
        package_id: String,
        amount: String,
        transaction_ref: String,
        short_transaction_ref: String,
        transaction_token: String
    ) {
        viewModelScope.launch {
            withContext(dispatcher.io) {
                confirmDataUseCase.invoke(
                    ConfirmDataUseCase.Param(
                        type,
                        device_id,
                        model,
                        customer_account,
                        package_id,
                        amount,
                        transaction_ref,
                        short_transaction_ref,
                        transaction_token
                    )
                ).collect {
                    when (it) {
                        is Resource.Loading -> _confirmDataState.value = ConfirmDataState.Loading
                        is Resource.Error -> _confirmDataState.value =
                            ConfirmDataState.Error(it.exception)

                        is Resource.Success -> _confirmDataState.value =
                            ConfirmDataState.Success(it.data)
                    }
                }
            }
        }
    }


    fun validateUraCustomer(
        device_id: String,
        model: String,
        customer_account: String,
        customer_phone: String,
        amount: String
    ) {
        viewModelScope.launch {
            withContext(dispatcher.io) {
                validateUraUseCase.invoke(
                    ValidateUraUseCase.Param(
                        device_id,
                        model,
                        customer_account,
                        customer_phone,
                        amount
                    )
                ).collect {
                    when (it) {
                        is Resource.Loading -> _validateUraState.value = ValidateUraState.Loading
                        is Resource.Error -> _validateUraState.value =
                            ValidateUraState.Error(it.exception)

                        is Resource.Success -> _validateUraState.value =
                            ValidateUraState.Success(it.data)
                    }
                }

            }
        }

    }

    fun confirmUraPayment(
        device_id: String,
        model: String,
        customer_account: String,
        customer_phone: String,
        amount: String,
        transaction_ref: String,
        short_transaction_ref: String,
        transaction_token: String
    ) {
        viewModelScope.launch {
            withContext(dispatcher.io) {
                confirmUraUseCase.invoke(
                    ConfirmUraUseCase.Param(
                        device_id,
                        model,
                        customer_account,
                        customer_phone,
                        amount,
                        transaction_ref,
                        short_transaction_ref,
                        transaction_token
                    )
                ).collect {
                    when (it) {
                        is Resource.Loading -> _confirmUraState.value = ConfirmUraState.Loading
                        is Resource.Error -> _confirmUraState.value =
                            ConfirmUraState.Error(it.exception)

                        is Resource.Success -> _confirmUraState.value =
                            ConfirmUraState.Success(it.data)
                    }
                }
            }
        }
    }

    sealed class ValidateAirtimeState {
        object Initial : ValidateAirtimeState()
        object Loading : ValidateAirtimeState()
        data class Success(val data: ValidateUtilityEntity) : ValidateAirtimeState()
        data class Error(val message: String) : ValidateAirtimeState()
    }

    sealed class BuyAirtimeState {
        object Initial : BuyAirtimeState()
        object Loading : BuyAirtimeState()
        data class Success(val data: ConfirmUtilityEntity) : BuyAirtimeState()
        data class Error(val message: String) : BuyAirtimeState()
    }

    sealed class ValidateVoiceState {
        object Initial : ValidateVoiceState()
        object Loading : ValidateVoiceState()
        data class Success(val data: ValidateUtilityEntity) : ValidateVoiceState()
        data class Error(val message: String) : ValidateVoiceState()
    }

    sealed class ConfirmVoiceState {
        object Initial : ConfirmVoiceState()
        object Loading : ConfirmVoiceState()
        data class Success(val data: ConfirmUtilityEntity) : ConfirmVoiceState()
        data class Error(val message: String) : ConfirmVoiceState()
    }

    sealed class ValidateUmemeState {
        object Initial : ValidateUmemeState()
        object Loading : ValidateUmemeState()
        class Error(var message: String) : ValidateUmemeState()
        class Success(var data: ValidateUtilityEntity) : ValidateUmemeState()
    }

    sealed class ConfirmUmemeState {
        object Initial : ConfirmUmemeState()
        object Loading : ConfirmUmemeState()
        class Error(var message: String) : ConfirmUmemeState()
        class Success(var data: ConfirmUtilityEntity) : ConfirmUmemeState()
    }

    sealed class ValidateTVState {
        object Initial : ValidateTVState()
        object Loading : ValidateTVState()
        data class Success(val data: ValidateUtilityEntity) : ValidateTVState()
        data class Error(val message: String) : ValidateTVState()
    }

    sealed class ConfirmTVState {
        object Initial : ConfirmTVState()
        object Loading : ConfirmTVState()
        data class Success(val data: ConfirmUtilityEntity) : ConfirmTVState()
        data class Error(val message: String) : ConfirmTVState()
    }

    sealed class ValidateDataState {
        object Initial : ValidateDataState()
        object Loading : ValidateDataState()
        data class Success(val data: ValidateUtilityEntity) : ValidateDataState()
        data class Error(val message: String) : ValidateDataState()
    }

    sealed class ConfirmDataState {
        object Initial : ConfirmDataState()
        object Loading : ConfirmDataState()
        data class Success(val data: ConfirmUtilityEntity) : ConfirmDataState()
        data class Error(val message: String) : ConfirmDataState()
    }


    sealed class ValidateUraState {
        object Initial : ValidateUraState()
        object Loading : ValidateUraState()
        data class Success(val data: ValidateUtilityEntity) : ValidateUraState()
        data class Error(val message: String) : ValidateUraState()
    }

    sealed class ConfirmUraState {
        object Initial : ConfirmUraState()
        object Loading : ConfirmUraState()
        data class Success(val data: ConfirmUtilityEntity) : ConfirmUraState()
        data class Error(val message: String) : ConfirmUraState()
    }

    sealed class DatePackagesState {
        object Initial : DatePackagesState()
        object Loading : DatePackagesState()
        data class Success(val data: Boolean) : DatePackagesState()
        data class Error(val message: String) : DatePackagesState()
    }

    sealed class TvPackagesState {
        object Initial : TvPackagesState()
        object Loading : TvPackagesState()
        data class Success(val data: Boolean) : TvPackagesState()
        data class Error(val message: String) : TvPackagesState()
    }


}