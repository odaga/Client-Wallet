package com.mcash.domain.repository

import com.mcash.domain.model.Beneficiary
import com.mcash.domain.model.Contact
import com.mcash.domain.model.FormMapEntity
import com.mcash.domain.model.NotificationEntity
import kotlinx.coroutines.flow.Flow

interface LocalRepository {

//    fun getPromos(): Flow<List<PromoEntity>>
//
//    suspend fun savePromo(promo: PromoEntity): Long
//
//    suspend fun savePromoList(promos: List<PromoEntity>)

//    suspend fun clearPromos()

    // Beneficiaries
    suspend fun insertBeneficiary(beneficiary: Beneficiary)

    fun getBeneficiaries(): Flow<List<Beneficiary>>

    fun searchBeneficiary(searchItem: String): Flow<List<Beneficiary>>

    suspend fun deleteBeneficiary(beneficiary: Beneficiary)

    // Contact
    suspend fun insertContact(contact: Contact)

    suspend fun insertAllContacts(contacts: List<Contact>)

    fun getContacts(): Flow<List<Contact>>

    fun searchContact(searchItem: String): Flow<List<Contact>>

    suspend fun deleteContact(contact: Contact)

    suspend fun clearContacts()

    suspend fun insertNotification(notificationEntity: NotificationEntity)

    suspend fun getNotifications(): List<NotificationEntity>

    suspend fun updateNotification(notificationEntity: NotificationEntity)

}