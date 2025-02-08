package com.mcash.client.presentation

import android.app.Application
import android.database.Cursor
import android.provider.ContactsContract
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mcash.client.core.models.home.PromoEntity
import com.mcash.domain.model.Contact
import com.mcash.domain.repository.PreferenceRepository
import com.mcash.domain.usecases.ContactUseCase
import com.mcash.domain.usecases.user.ClearUserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.Timer
import java.util.TimerTask
import javax.inject.Inject

@HiltViewModel
class MainViewModel
@Inject constructor(
    private val context: Application,
    private val contactUseCase: ContactUseCase,
    private val preferenceRepository: PreferenceRepository,
    private val clearUserUseCase: ClearUserUseCase
) : ViewModel() {

    private lateinit var timer: Timer

    init {
        getSavedPromos()
    }

    private val _promoState = MutableStateFlow<PromoState>(PromoState.Loading)
    val promoState get() = _promoState.asStateFlow()

    fun getSavedPromos() {
//        viewModelScope.launch(Dispatchers.IO) {
//            promoUseCase.getPromos()
//                .catch { e ->
//                    val error = Utils.resolveError(e)
//                    _promoState.value = PromoState.Error(error)
//                }
//                .collect {
//                    _promoState.value = PromoState.Success(it)
//                }
//        }
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
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC"
        )
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
            val contactIdIndex =
                phoneCursor!!.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID)
            val numberIndex =
                phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
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

    suspend fun signOut() {
        preferenceRepository.saveSignOutState()
        clearUserUseCase()
    }

    suspend fun saveSessionJobId(workerId: String) {
        preferenceRepository.saveSessionJobId(workerId)
    }

    suspend fun getSessionJobId(): String {
        return preferenceRepository.getSessionJobId().firstOrNull() ?: ""
    }

    sealed class PromoState {
        object Loading : PromoState()
        data class Success(val data: List<PromoEntity>) : PromoState()
        data class Error(val message: String) : PromoState()
    }

    companion object {
        const val INITIAL_COUNTDOWN_TIME: Long = 60 // Initial countdown time in seconds
        const val TIMER_DELAY_MS: Long = 1000 // Delay before the timer starts (1 second)
        const val TIMER_PERIOD_MS: Long = 1000 // Timer period (1 second)
    }
}