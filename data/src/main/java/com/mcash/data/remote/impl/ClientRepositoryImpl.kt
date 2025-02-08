package com.mcash.data.remote.impl

import android.util.Log
import com.mcash.data.local.room.dao.DataDao
import com.mcash.data.local.room.dao.FAQDao
import com.mcash.data.local.room.dao.TvDao
import com.mcash.data.local.room.dao.VoiceDao
import com.mcash.data.remote.model.TVPackage
import com.mcash.data.remote.remoteMappers.*
import com.mcash.data.remote.services.ClientService
import com.mcash.data.remote.services.VersionService
import com.mcash.domain.model.*
import com.mcash.domain.repository.ClientRepository
import com.mcash.domain.repository.PreferenceRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class ClientRepositoryImpl @Inject constructor(
    private val service: ClientService,
    private val versionService: VersionService,
    private val preferences: PreferenceRepository,
    private val remoteUserMapper: RemoteUserMapper,
    private val remoteValidateDepositMapper: RemoteValidateDepositMapper,
    private val remoteHistoryMapper: RemoteHistoryMapper,
    private val remoteAccountMapper: RemoteAccountMapper,
    private val remoteTransferMapper: RemoteTransferMapper,
    private val remoteNwscCustomerMapper: RemoteNwscCustomerMapper,
    private val remoteValidateNwscMapper: RemoteValidateNwscMapper,
    private val remoteConfirmNwscMapper: RemoteConfirmNwscMapper,
    private val validateUtilityMapper: ValidateUtilityMapper,
    private val confirmUtilityMapper: ConfirmUtilityMapper,
    private val tvDao: TvDao,
    private val tvPackageMapper: TVPackageMapper,
    private val voiceDao: VoiceDao,
    private val voicePackageMapper: VoicePackageMapper,
    private val dataDao: DataDao,
    private val dataPackageMapper: DataPackageMapper,
    private val faqMapper: FAQMapper,
    private val faqDao: FAQDao
) : ClientRepository {
    override suspend fun getAppVersion(): String {
        return try {
            val response = versionService.getVersionNumber()
            response.data.version ?: "1"
        } catch (throwable: Throwable) {
            throw throwable
        }
    }

    override suspend fun authenticateUser(data: HashMap<String, Any>): Int {
        return try {
            val response = service.authenticateUser(data)
            response.status_code
        } catch (throwable: Throwable) {
            throw throwable
        }
    }

    override suspend fun verifyUserOTP(data: HashMap<String, Any>): UserEntity {
        return try {
            val response = service.verifyUserOtp(data)
            val user = remoteUserMapper.mapToDomain(response.data)
            user
        } catch (throwable: Throwable) {
            throw throwable
        }
    }

    override suspend fun transferMoneyToWallet(
        data: HashMap<String, Any>, authHeader: String
    ): TransferEntity {
        return try {
            val response = service.transferMoney(data, authHeader)
            getClientBalance()

            remoteTransferMapper.mapToDomain(response.data.transfer)
        } catch (throwable: Throwable) {
            throw throwable
        }
    }

    override suspend fun validateTransferToMobileMoney(
        data: HashMap<String, Any>, authHeader: String
    ): DepositEntity {
        return try {
            val response = service.validateTransferToMobileMoney(data, authHeader).transaction
            remoteValidateDepositMapper.mapToDomain(response)
        } catch (throwable: Throwable) {
            throw throwable
        }
    }

    override suspend fun confirmTransferToMobileMoney(
        data: HashMap<String, Any>, authHeader: String
    ): String {
        return try {
            val response = service.confirmTransferToMobileMoney(data, authHeader)
            response.data?.message ?: response.message ?: "Successful"
        } catch (throwable: Throwable) {
            throw throwable
        }
    }


    override suspend fun fetchMiniStatement(): List<TransferEntity> {
        return try {
            val user = preferences.getDataStoreUser().first()
            val data = HashMap<String, Any>().apply {
                this["account_number"] = user.phone
                this["pin"] = user.pin
            }
            val response = service.getMiniStatement(data)
            val list = response.data.transfers
            list.map { remoteTransferMapper.mapToDomain(it) }
        } catch (throwable: Throwable) {
            throw throwable
        }
    }

    override suspend fun validateDepositToWallet(
        data: HashMap<String, Any>, authHeader: String
    ): DepositEntity {
        return try {
            val response = service.validateDepositToWallet(data, authHeader).transaction
            remoteValidateDepositMapper.mapToDomain(response)
        } catch (throwable: Throwable) {
            throw throwable
        }
    }

    override suspend fun confirmDepositToWallet(
        data: HashMap<String, Any>, authHeader: String
    ): String {
        return try {
            val response = service.confirmDepositToWallet(data, authHeader)
            response.data?.message ?: response.message ?: "Successful"
        } catch (throwable: Throwable) {
            throw throwable
        }
    }

    override suspend fun changeClientPin(data: HashMap<String, Any>, authHeader: String): String {
        return try {
            val response = service.changePin(data, authHeader)
            response.message ?: ""
        } catch (throwable: Throwable) {
            throw throwable
        }
    }

    override suspend fun getClientHistory(
        data: HashMap<String, Any>, authHeader: String
    ): List<HistoryEntity> {
        return try {
            val list = ArrayList<HistoryEntity>()
            val response = service.getClientHistory(data, authHeader).data
            for (i in response) {
                list.add(remoteHistoryMapper.mapToDomain(i))
            }
            list
        } catch (t: Throwable) {
            throw t
        }
    }

    override suspend fun fetchNwscAreas(authHeader: String): List<String> {
        return try {
            val response = service.getNwscAreas(authHeader).data
            preferences.saveNwscAreas(response)
            response
        } catch (throwable: Throwable) {
            throw throwable
        }
    }

    override suspend fun validateNwscCustomer(
        data: HashMap<String, Any>, authHeader: String
    ): NwscCustomerEntity {
        return try {
            val response = service.validateNwscCustomer(data, authHeader).data
            remoteNwscCustomerMapper.mapToDomain(response)
        } catch (throwable: Throwable) {
            throw throwable
        }
    }

    override suspend fun validateNwscTransaction(
        data: HashMap<String, Any>, authHeader: String
    ): NwscValidateEntity {
        return try {
            val response = service.validateNwscTransaction(data, authHeader).transaction
            remoteValidateNwscMapper.mapToDomain(response)
        } catch (throwable: Throwable) {
            throw throwable
        }
    }

    override suspend fun confirmNwscTransaction(
        data: HashMap<String, Any>, authHeader: String
    ): NwscConfirmEntity {
        return try {
            val response = service.confirmNwscTransaction(data, authHeader).transaction
            remoteConfirmNwscMapper.mapToDomain(response)
        } catch (throwable: Throwable) {
            throw throwable
        }
    }


    override suspend fun getClientBalance(): AccountBalance {
        return try {
            val user = preferences.getDataStoreUser().first()
            val param = HashMap<String, Any>().apply {
                this["account_number"] = user.username
                this["pin"] = user.pin
            }
            Log.d("resp", "$user")
            val response = service.getAccountBalance(param).data
            Log.d("resp", "$response")
            preferences.saveBalance(response.amount)
            remoteAccountMapper.mapToDomain(response)
        } catch (t: Throwable) {
            throw t
        }
    }

    override suspend fun validateTransferToWallet(
        data: HashMap<String, Any>, authHeader: String
    ): DepositEntity {
        return try {
            val response = service.validateTransferToWallet(data, authHeader).transaction
            remoteValidateDepositMapper.mapToDomain(response)
        } catch (throwable: Throwable) {
            throw throwable
        }
    }

    override suspend fun confirmTransferToWallet(
        data: HashMap<String, Any>, authHeader: String
    ): String {
        return try {
            val response = service.confirmTransferToWallet(data, authHeader).transaction
            getClientBalance()
            response.description ?: "Description"
        } catch (throwable: Throwable) {
            throw throwable
        }
    }

    override suspend fun validateAirtimeCustomer(
        data: HashMap<String, Any>, authHeader: String
    ): ValidateUtilityEntity {
        return try {
            val response = service.validateAirtimeCustomer(data, authHeader)
            validateUtilityMapper.mapToDomain(response.transaction)
        } catch (e: Throwable) {
            throw e
        }
    }

    override suspend fun buyCustomerAirtime(
        data: HashMap<String, Any>, authHeader: String
    ): ConfirmUtilityEntity {
        return try {
            val response = service.buyCustomerAirtime(data, authHeader)
            getClientBalance()
            confirmUtilityMapper.mapToDomain(response.transaction)
        } catch (e: Throwable) {
            throw e
        }
    }

    override suspend fun validateUmemeCustomer(
        data: HashMap<String, Any>, authHeader: String
    ): ValidateUtilityEntity {
        return try {
            val response = service.validateUmemeCustomer(data, authHeader)
            validateUtilityMapper.mapToDomain(response.transaction)
        } catch (t: Throwable) {
            throw t
        }
    }

    override suspend fun confirmUmemePayment(
        data: HashMap<String, Any>, authHeader: String
    ): ConfirmUtilityEntity {
        return try {
            val response = service.confirmUmemePayment(data, authHeader)
            getClientBalance()
            confirmUtilityMapper.mapToDomain(response.transaction)
        } catch (t: Throwable) {
            throw t
        }
    }

    override suspend fun getTVPackages(type: String, authHeader: String): List<TvEntity> {
        return try {
            val list = ArrayList<TvEntity>()
            val localPackages = tvDao.getTvPackageByProvider(type)

            if (localPackages.isEmpty()) {
                val response = service.getTVPackages(type, authHeader).packages
                for (i in response) {
                    val tvPackage = filterTvProvider(i, type)
                    tvDao.insertTvPackage(tvPackage)
                    list.add(tvPackage)
                }
            } else {
               localPackages.forEach {
                   list.add(it)
               }
            }
            list

        } catch (t: Throwable) {
            throw t
        }
    }

    override suspend fun validateTVCustomer(
        type: String, data: HashMap<String, Any>, authHeader: String
    ): ValidateUtilityEntity {
        return try {
            val response = service.validateTVCustomer(type, data, authHeader)
            validateUtilityMapper.mapToDomain(response.transaction)
        } catch (t: Throwable) {
            throw t
        }
    }

    override suspend fun confirmTVPayment(
        type: String, data: HashMap<String, Any>, authHeader: String
    ): ConfirmUtilityEntity {
        return try {
            val response = service.confirmTVPayment(type, data, authHeader)
            getClientBalance()
            confirmUtilityMapper.mapToDomain(response.transaction)
        } catch (t: Throwable) {
            throw t
        }
    }

    override suspend fun getVoicePackages(type: String, authHeader: String): List<VoiceEntity> {
        return try {
            val response = service.getVoicePackages(type, authHeader).packages
            val list = ArrayList<VoiceEntity>()
            for (i in response) {
                val voicePackage = voicePackageMapper.mapToDomain(i)
                voiceDao.insertVoiceBundle(voicePackage)
                list.add(voicePackage)

            }
            list
        } catch (t: Throwable) {
            throw t
        }
    }

    override suspend fun validateVoiceCustomer(
        type: String, data: HashMap<String, Any>, authHeader: String
    ): ValidateUtilityEntity {
        return try {
            val response = service.validateVoiceCustomer(type, data, authHeader)
            validateUtilityMapper.mapToDomain(response.transaction)
        } catch (t: Throwable) {
            throw t
        }
    }

    override suspend fun confirmVoicePayment(
        type: String, data: HashMap<String, Any>, authHeader: String
    ): ConfirmUtilityEntity {
        return try {
            val response = service.confirmVoicePayment(type, data, authHeader)
            getClientBalance()
            confirmUtilityMapper.mapToDomain(response.transaction)
        } catch (t: Throwable) {
            throw t
        }
    }

    override suspend fun getDataPackages(type: String, authHeader: String): List<DataEntity> {
        return try {
            val response = service.getDataPackages(type, authHeader).packages
            val list = ArrayList<DataEntity>()
            for (i in response) {
                val datPackage = dataPackageMapper.mapToDomain(i)
                list.add(datPackage)
            }
            dataDao.insertDataBundle(Data(type, list))
            list
        } catch (t: Throwable) {
            throw t
        }
    }

    override suspend fun validateDataCustomer(
        type: String, data: HashMap<String, Any>, authHeader: String
    ): ValidateUtilityEntity {
        return try {
            val response = service.validateDataCustomer(type, data, authHeader)
            validateUtilityMapper.mapToDomain(response.transaction)
        } catch (t: Throwable) {
            throw t
        }
    }

    override suspend fun confirmDataPayment(
        type: String, data: HashMap<String, Any>, authHeader: String
    ): ConfirmUtilityEntity {
        return try {
            val response = service.confirmDataPayment(type, data, authHeader)
            getClientBalance()
            confirmUtilityMapper.mapToDomain(response.transaction)
        } catch (t: Throwable) {
            throw t
        }
    }

    override suspend fun validateUraCustomer(
        data: HashMap<String, Any>, authHeader: String
    ): ValidateUtilityEntity {
        return try {
            val response = service.validateUraPayment(data, authHeader)
            validateUtilityMapper.mapToDomain(response.transaction)
        } catch (t: Throwable) {
            throw t
        }
    }

    override suspend fun confirmUraPayment(
        data: HashMap<String, Any>, authHeader: String
    ): ConfirmUtilityEntity {
        return try {
            val response = service.confirmUraPayment(data, authHeader)
            getClientBalance()
            confirmUtilityMapper.mapToDomain(response.transaction)
        } catch (t: Throwable) {
            throw t
        }
    }

    override suspend fun getFAQs(): List<FAQEntity> {
        return try {
            val list = ArrayList<FAQEntity>()
            Log.d("List", "$list")
            val response = service.getFAQs().data
            Log.d("Respo", "$response")
            for (i in response) {
                val x = faqMapper.mapToDomain(i)
                faqDao.insertFAQ(i)
                list.add(x)
            }
            list

        } catch (t: Throwable) {
            throw t
        }
    }

    override suspend fun getHelp(): String {
        return try {
            val response = service.getHelp()
            preferences.saveHelpInfo(response)
            response
        } catch (t: Throwable) {
            throw t
        }
    }

    private fun filterTvProvider(tvPackage: TVPackage, type: String): TvEntity {
        var provider = ""
        when (type) {
            "dstv" -> {
                if ((tvPackage.paymentitemname?.contains("GOTv") == true) || (tvPackage.code?.contains(
                        "GO"
                    ) == true)
                ) provider = "gotv"
                else {
                    provider = "dstv"
                }
            }

            else -> {
                provider = type
            }
        }
        return TvEntity(
            packageId = tvPackage.packageId,
            isAmountFixed = tvPackage.isAmountFixed,
            paymentitemname = tvPackage.paymentitemname,
            amount = tvPackage.amount,
            code = tvPackage.code,
            currencyCode = tvPackage.currencyCode,
            currencySymbol = tvPackage.currencySymbol,
            itemCurrencySymbol = tvPackage.itemCurrencySymbol,
            sortOrder = tvPackage.sortOrder,
            pictureId = tvPackage.pictureId,
            provider = provider
        )
    }

}