package com.mcash.client.presentation.ui.splash

import android.app.Application
import android.database.Cursor
import android.provider.ContactsContract
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.mcash.client.core.models.home.PromoEntity
import com.mcash.domain.model.Contact
import com.mcash.domain.model.sealed.Resource
import com.mcash.domain.repository.ClientRepository
import com.mcash.domain.usecases.ContactUseCase
import com.mcash.domain.usecases.version.GetVersionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val context: Application,
    private val contactUseCase: ContactUseCase,
    private val clientRepository: ClientRepository,
    private val getVersionUseCase: GetVersionUseCase
): ViewModel() {

    init {
//        getVersion()
    }
    private val _versionState = MutableStateFlow<VersionState>(VersionState.Success("1"))
    val versionState get() = _versionState.asLiveData()


    suspend fun getVersionNumber(): String = withContext(Dispatchers.IO) {
        return@withContext clientRepository.getAppVersion()
    }

    fun getVersion() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                getVersionUseCase.run(GetVersionUseCase.Param()).collect {
                    when(it) {
                        is Resource.Loading -> _versionState.value = VersionState.Loading
                        is Resource.Error -> _versionState.value = VersionState.Error(it.exception)
                        is Resource.Success -> _versionState.value = VersionState.Success(it.data)
                    }
                }
            }
        }
    }

    fun fetchContacts() {
        viewModelScope.launch {
            val contactsListAsync = async { getPhoneContacts() }
            val contactNumbersAsync = async { getContactNumbers() }

            val contacts = contactsListAsync.await()
            val contactNumbers = contactNumbersAsync.await()

            contacts.forEach {
                contactNumbers[it.id]?.let { numbers ->
                    it.numbers = numbers
                }
            }
            Timber.d("Contacts: $contacts")

            contactUseCase.getContacts()
                .catch { e -> Timber.e(e) }
                .collect {
                    if (it.size != contacts.size) {
                        contactUseCase.clearContacts()
                        contactUseCase.addAllContacts(contacts)
                    }
                }
        }
    }

    private suspend fun getPhoneContacts(): List<Contact> {
        val contactsList = ArrayList<Contact>()
        val contactsCursor = context.contentResolver?.query(
            ContactsContract.Contacts.CONTENT_URI,
            null,
            null,
            null,
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC")
        if (contactsCursor != null && contactsCursor.count > 0) {
            val idIndex = contactsCursor.getColumnIndex(ContactsContract.Contacts._ID)
            val nameIndex = contactsCursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)
            while (contactsCursor.moveToNext()) {
                val id = contactsCursor.getString(idIndex)
                val name = contactsCursor.getString(nameIndex)
                if (name != null) {
                    contactsList.add(Contact(id, name))
                }
            }
            contactsCursor.close()
        }
        return contactsList
    }

    private fun getContactNumbers(): HashMap<String, ArrayList<String>> {
        val contactsNumberMap = HashMap<String, ArrayList<String>>()
        val phoneCursor: Cursor? = context.contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            null,
            null,
            null,
            null
        )
        if (phoneCursor != null && phoneCursor.count > 0) {
            val contactIdIndex = phoneCursor!!.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID)
            val numberIndex = phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
            while (phoneCursor.moveToNext()) {
                val contactId = phoneCursor.getString(contactIdIndex)
                val number: String = phoneCursor.getString(numberIndex)
                //check if the map contains key or not, if not then create a new array list with number
                if (contactsNumberMap.containsKey(contactId)) {
                    contactsNumberMap[contactId]?.add(number)
                } else {
                    contactsNumberMap[contactId] = arrayListOf(number)
                }
            }
            //contact contains all the number of a particular contact
            phoneCursor.close()
        }
        return contactsNumberMap
    }


    sealed class PromoState {
        object Loading : PromoState()
        data class Success(val data: List<PromoEntity>) : PromoState()
        data class Error(val message: String) : PromoState()
    }

    sealed class VersionState {
        object Loading : VersionState()
        data class Error(val message: String) : VersionState()
        data class Success(val data: String) : VersionState()
    }
}