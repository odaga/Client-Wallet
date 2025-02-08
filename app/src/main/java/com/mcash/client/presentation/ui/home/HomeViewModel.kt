package com.mcash.client.presentation.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.mcash.domain.dispacher.AppDispatcher
import com.mcash.domain.model.HistoryEntity
import com.mcash.domain.model.sealed.Resource
import com.mcash.domain.usecases.GetTransactionHistoryUseCase
import com.mcash.domain.usecases.NotificationUseCase
import com.mcash.domain.usecases.account.GetSavedBalanceUseCase
import com.mcash.domain.usecases.user.GetSavedUserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getSavedUserUseCase: GetSavedUserUseCase,
    private val getSavedBalanceUseCase: GetSavedBalanceUseCase,
    private val getTransactionHistoryUseCase: GetTransactionHistoryUseCase,
    private val notificationUseCase: NotificationUseCase,
    private val dispatcher: AppDispatcher
) : ViewModel() {

    init {
        getBalance()
    }

    private val _balanceState = MutableStateFlow<BalanceState>(BalanceState.Initial)
    val balanceState get() = _balanceState.asLiveData()

    private val _historyState =
        MutableStateFlow<TransactionHistoryState>(TransactionHistoryState.Initial)
    val historyState get() = _historyState.asLiveData()

    val savedUser = runBlocking(dispatcher.io) { getSavedUserUseCase().first() }

    private fun getBalance() {
        viewModelScope.launch {
            withContext(dispatcher.io) {
                getSavedBalanceUseCase().collect {
                    _balanceState.value = BalanceState.Success(it)
                }
            }
        }
    }

    suspend fun getNotifications(): Int {
        return withContext(dispatcher.io) {
            notificationUseCase.getNotifications().size
        }
    }

    suspend fun getTransactionHistory() {
        viewModelScope.launch {
            withContext(dispatcher.io) {
                getTransactionHistoryUseCase(GetTransactionHistoryUseCase.Param()).collect {

                    when (it) {
                        is Resource.Loading -> _historyState.value = TransactionHistoryState.Loading
                        is Resource.Error -> _historyState.value =
                            TransactionHistoryState.Error(it.exception)
                        is Resource.Success -> _historyState.value =
                            TransactionHistoryState.Success(it.data)
                    }

                }
            }
        }
    }
}

sealed class BalanceState {
    object Initial : BalanceState()
    data class Success(val data: Long) : BalanceState()
}

sealed class TransactionHistoryState {
    object Initial : TransactionHistoryState()
    object Loading : TransactionHistoryState()
    data class Error(val message: String) : TransactionHistoryState()
    data class Success(val data: List<HistoryEntity>) : TransactionHistoryState()
}