package com.mcash.domain.repository

import com.mcash.domain.model.AppPreference
import com.mcash.domain.model.CardInfoEntity
import com.mcash.domain.model.FormMapEntity
import com.mcash.domain.model.TvEntity
import com.mcash.domain.model.UserEntity
import kotlinx.coroutines.flow.Flow

interface PreferenceRepository {

    fun getAppPreferences(): Flow<AppPreference>

    fun getCartCount(): Flow<Int>

    suspend fun updateDarkModeTheme(isDarkMode: Boolean)

    suspend fun updateSystemDefaultTheme(isDefaultTheme: Boolean)

    suspend fun saveUserToDatastore(user: UserEntity)

    fun getDataStoreUser(): Flow<UserEntity>


    suspend fun saveSignInState()

    suspend fun saveSignOutState()

    suspend fun getSignInState(): Flow<Boolean>

    suspend fun saveSessionJobId(workId: String)

    suspend fun getSessionJobId(): Flow<String>

    suspend fun clearUser()

    suspend fun saveBalance(balance: Long)

    fun getAccountBalance(): Flow<Long>

    suspend fun saveNwscAreas(areas: List<String>)

    fun getNwscAreas(): Flow<List<String>>

    suspend fun saveHelpInfo(info: String)

    fun getHelpInfo(): Flow<String>

    fun getTvPackagesByProvider(provider: String): List<TvEntity>


    // KYC form fields
    suspend fun saveFormGroupData(formMapEntity: FormMapEntity)

    suspend fun getAllFormGroupData(): List<FormMapEntity>

    suspend fun clearFormMaps()

    suspend fun getFormGroupByLabel(formGroupLabel: String): List<FormMapEntity>

    suspend fun checkFormGroupStatus(formGroupLabel: String): Boolean

    // Id card
    suspend fun saveIdCard(cardInfoEntity: CardInfoEntity)

    suspend fun getIdCard(nin: String): CardInfoEntity

    suspend fun deleteCardInfo(nin: String)
}