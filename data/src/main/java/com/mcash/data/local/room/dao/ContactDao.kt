package com.mcash.data.local.room.dao

import androidx.room.*
import com.mcash.domain.model.Contact
import kotlinx.coroutines.flow.Flow

@Dao
interface ContactDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertContact(contact: Contact)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertContactList(contacts: List<Contact>)

    @Query("SELECT * FROM contact")
    fun getContacts(): Flow<List<Contact>>

    @Delete
    suspend fun deleteContact(contact: Contact)

    @Query("DELETE FROM contact")
    suspend fun clearContacts()

    @Query("SELECT * FROM contact WHERE name LIKE :search")
    fun searchContact(search: String): Flow<List<Contact>>
}