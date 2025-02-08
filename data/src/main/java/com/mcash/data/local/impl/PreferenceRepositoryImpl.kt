package com.mcash.data.local.impl

import android.util.Base64
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import com.mcash.data.UserProto
import com.mcash.data.local.room.dao.FormMapDao
import com.mcash.data.local.room.dao.IdCardDao
import com.mcash.data.local.room.dao.TvDao
import com.mcash.data.local.utils.*
import com.mcash.domain.model.AppPreference
import com.mcash.domain.model.CardInfoEntity
import com.mcash.domain.model.FormMapEntity
import com.mcash.domain.model.TvEntity
import com.mcash.domain.model.UserEntity
import com.mcash.domain.repository.PreferenceRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import mcash.data.BuildConfig
import javax.crypto.Cipher
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec
import javax.inject.Inject

class PreferenceRepositoryImpl @Inject constructor(
    private val datastorePreference: DataStore<Preferences>,
    private val userPrefDatastore: DataStore<UserProto>,
    private val tvDao: TvDao,
    private val formMapDao: FormMapDao,
    private val idCardDao: IdCardDao
) : PreferenceRepository {

    override fun getAppPreferences(): Flow<AppPreference> = datastorePreference.data.map {
        val dark = it[IS_DARK_MODE] ?: false
        val sysDefault = it[IS_SYSTEM_DEFAULT] ?: false
        AppPreference(dark, sysDefault)
    }

    override fun getCartCount(): Flow<Int> = datastorePreference.data.map {
        it[CART_COUNT] ?: 0
    }

    override suspend fun updateDarkModeTheme(isDarkMode: Boolean) {
        datastorePreference.edit {
            it[IS_DARK_MODE] = isDarkMode
        }
    }

    override suspend fun updateSystemDefaultTheme(isDefaultTheme: Boolean) {
        datastorePreference.edit {
            it[IS_SYSTEM_DEFAULT] = isDefaultTheme
        }
    }

    override suspend fun saveUserToDatastore(user: UserEntity) {
        Log.d("PIn", "${AESEncryption.encrypt(user.pin)}")
        userPrefDatastore.updateData { currentUserData ->
            currentUserData.toBuilder()
                .setId(user.id)
                .setName(user.name)
                .setUsername(user.username)
                .setPin(AESEncryption.encrypt(user.pin))
                .setEmail(user.email)
                .setPhone(user.phone)
                .setToken(user.token)
                .setIp(user.ip)
                .setSip(user.sip)
                .build()
        }
    }

    override suspend fun clearUser() {
        userPrefDatastore.updateData {
            it.toBuilder().clear().build()
        }
    }

    override suspend fun saveBalance(balance: Long) {
        datastorePreference.edit {
            it[ACCOUNT_BALANCE] = balance
        }
    }

    override fun getAccountBalance(): Flow<Long> = datastorePreference.data.map {
        it[ACCOUNT_BALANCE] ?: 0L
    }

    override suspend fun saveNwscAreas(areas: List<String>) {
        val set = HashSet<String>().apply {
            addAll(areas)
        }

        datastorePreference.edit { it[NWSC_AREAS] = set }
    }

    override fun getNwscAreas(): Flow<List<String>> = datastorePreference.data.map {
        it[NWSC_AREAS]?.toList() ?: emptyList()
    }

    override suspend fun saveHelpInfo(info: String) {
        datastorePreference.edit {
            it[HELP_INFO] = info
        }
    }

    override fun getHelpInfo(): Flow<String> = datastorePreference.data.map {
        it[HELP_INFO] ?: ""
    }

    override fun getTvPackagesByProvider(provider: String): List<TvEntity> {
        return tvDao.getTvPackageByProvider(provider)
    }


    override fun getDataStoreUser(): Flow<UserEntity> {
        return userPrefDatastore.data.map { user ->
            UserEntity(
                id = user.id,
                username = user.username.orEmpty(),
                name = user.name.orEmpty(),
                email = user.email.orEmpty(),
                pin = AESEncryption.decrypt(user.pin).orEmpty(),
                phone = user.phone.orEmpty(),
                token = user.token.orEmpty(),
                ip = user.ip.orEmpty(),
                sip = user.sip.orEmpty()
            )
        }
    }

    override suspend fun saveSignInState() {
        datastorePreference.edit {
            it[IS_SIGNED_IN] = true
        }
    }

    override suspend fun saveSignOutState() {
        datastorePreference.edit { it.clear() }
    }

    override suspend fun getSignInState(): Flow<Boolean> = datastorePreference.data.map {
        it[IS_SIGNED_IN] ?: false
    }

    override suspend fun saveSessionJobId(workId: String) {
        datastorePreference.edit {
            it[SESSION_JOB_ID] = workId
        }
    }

    override suspend fun getSessionJobId(): Flow<String> = datastorePreference.data.map {
        it[SESSION_JOB_ID] ?: ""
    }

    override suspend fun saveFormGroupData(formMapEntity: FormMapEntity) {
        formMapDao.insert(formMapEntity)
    }

    override suspend fun getAllFormGroupData(): List<FormMapEntity> {
        return formMapDao.getAllFormMap()
    }

    override suspend fun clearFormMaps() {
        val data = formMapDao.getAllFormMap()
        if (data.isNotEmpty()) {
            data.forEach {
                formMapDao.deleteFormMap(it)
            }
        }
    }

    override suspend fun getFormGroupByLabel(formGroupLabel: String): List<FormMapEntity> {
        return formMapDao.getFormMapByFormLabel(formGroupLabel)

    }

    override suspend fun checkFormGroupStatus(formGroupLabel: String): Boolean {
        val formMap = formMapDao.getFormMapByFormLabel(formGroupLabel)
        return true
    }

    override suspend fun saveIdCard(cardInfoEntity: CardInfoEntity) {
        idCardDao.insertIdCard(cardInfoEntity)
    }

    override suspend fun getIdCard(nin: String): CardInfoEntity {
        return idCardDao.getCardInfo(nin)
    }

    override suspend fun deleteCardInfo(nin: String) {
        idCardDao.clearCardInfo()
    }

    object AESEncryption {

        const val secretKey = BuildConfig.CLIENT_SECRET_KEY
        const val salt = BuildConfig.CLIENT_SALT // base64 decode => AiF4sa12SAfvlhiWu
        const val iv = BuildConfig.CLIENT_IV // base64 decode => mT34SaFD5678QAZX
        fun encrypt(strToEncrypt: String): String? {
            try {
                val ivParameterSpec = IvParameterSpec(Base64.decode(iv, Base64.DEFAULT))

                val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1")
                val spec = PBEKeySpec(
                    secretKey.toCharArray(),
                    Base64.decode(salt, Base64.DEFAULT),
                    10000,
                    256
                )
                val tmp = factory.generateSecret(spec)
                val secretKey = SecretKeySpec(tmp.encoded, "AES")

                val cipher = Cipher.getInstance("AES/CBC/PKCS7Padding")
                cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivParameterSpec)
                return Base64.encodeToString(
                    cipher.doFinal(strToEncrypt.toByteArray(Charsets.UTF_8)),
                    Base64.DEFAULT
                )
            } catch (e: Exception) {
                println("Error while encrypting: $e")
            }
            return null
        }

        fun decrypt(strToDecrypt: String): String? {
            try {

                val ivParameterSpec = IvParameterSpec(Base64.decode(iv, Base64.DEFAULT))

                val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1")
                val spec = PBEKeySpec(
                    secretKey.toCharArray(),
                    Base64.decode(salt, Base64.DEFAULT),
                    10000,
                    256
                )
                val tmp = factory.generateSecret(spec);
                val secretKey = SecretKeySpec(tmp.encoded, "AES")

                val cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");
                cipher.init(Cipher.DECRYPT_MODE, secretKey, ivParameterSpec);
                return String(cipher.doFinal(Base64.decode(strToDecrypt, Base64.DEFAULT)))
            } catch (e: Exception) {
                println("Error while decrypting: $e");
            }
            return null
        }
    }
}