package com.mcash.data.local.room.dao

import androidx.room.*
import com.mcash.data.local.model.LocalUser
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserList(response: List<LocalUser>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: LocalUser)

    @Transaction
    @Query("SELECT * FROM users LIMIT 1")
    suspend fun getUser(): List<LocalUser>

    @Query("SELECT * FROM users LIMIT 1")
    fun getOneUser(): Flow<LocalUser?>

    @Query("DELETE FROM users")
    suspend fun clearUsers()
}