package com.mcash.data.local.impl

import com.mcash.data.local.room.dao.*
import com.mcash.domain.model.*
import com.mcash.domain.repository.LocalRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LocalRepositoryImpl @Inject constructor(
    private val beneficiaryDao: BeneficiaryDao,
    private val contactDao: ContactDao,
    private val notificationDao: NotificationDao
) : LocalRepository {

    override suspend fun insertBeneficiary(beneficiary: Beneficiary) =
        beneficiaryDao.insertBeneficiary(beneficiary)

    override fun getBeneficiaries(): Flow<List<Beneficiary>> = beneficiaryDao.getBeneficiaries()

    override fun searchBeneficiary(searchItem: String): Flow<List<Beneficiary>> =
        beneficiaryDao.searchBeneficiary(searchItem)

    override suspend fun deleteBeneficiary(beneficiary: Beneficiary) =
        beneficiaryDao.deleteBeneficiary(beneficiary)

    override suspend fun insertContact(contact: Contact) = contactDao.insertContact(contact)

    override suspend fun insertAllContacts(contacts: List<Contact>) =
        contactDao.insertContactList(contacts)

    override fun getContacts(): Flow<List<Contact>> = contactDao.getContacts()

    override fun searchContact(searchItem: String): Flow<List<Contact>> =
        contactDao.searchContact(searchItem)

    override suspend fun deleteContact(contact: Contact) = contactDao.deleteContact(contact)

    override suspend fun clearContacts() = contactDao.clearContacts()
    override suspend fun insertNotification(notificationEntity: NotificationEntity) =
        notificationDao.insertNotification(notificationEntity)

    override suspend fun getNotifications(): List<NotificationEntity> =
        notificationDao.getNotifications()

    override suspend fun updateNotification(notificationEntity: NotificationEntity) =
        notificationDao.updateNotification(notificationEntity)



}