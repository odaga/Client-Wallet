package com.mcash.domain.usecases

import com.mcash.domain.model.Contact
import com.mcash.domain.repository.LocalRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ContactUseCase @Inject constructor(
    private val local: LocalRepository
) {

    fun getContacts(): Flow<List<Contact>> = local.getContacts()

    fun searchContact(search: String): Flow<List<Contact>> = local.searchContact(search)

    suspend fun addContact(contact: Contact) = local.insertContact(contact)

    suspend fun  addAllContacts(contacts: List<Contact>) = local.insertAllContacts(contacts)

    suspend fun clearContacts() = local.clearContacts()
}