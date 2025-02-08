package com.mcash.client.presentation.ui.contacts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mcash.client.core.sealed.ContactState
import com.mcash.domain.usecases.ContactUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ContactViewModel @Inject constructor(
    private val contactUseCase: ContactUseCase,
) : ViewModel() {

    private val _contactsState = MutableStateFlow<ContactState>(ContactState.Loading)
    val contactsState get() = _contactsState

    fun getContacts() {
        viewModelScope.launch {
            contactUseCase.getContacts()
                .catch { e ->
                    _contactsState.value = ContactState.Error(e.localizedMessage)
                }
                .collect {
                    _contactsState.value = ContactState.Success(it)
                }
        }
    }

    fun searchContact(beneficiary: String) {
        viewModelScope.launch(Dispatchers.IO) {
            contactUseCase.searchContact(beneficiary)
                .catch { e ->
                    _contactsState.value = ContactState.Error(e.localizedMessage)
                }
                .collect {
                    _contactsState.value = ContactState.Success(it)
                }
        }
    }
}