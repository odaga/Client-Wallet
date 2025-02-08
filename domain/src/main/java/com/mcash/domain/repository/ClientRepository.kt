package com.mcash.domain.repository

import com.mcash.domain.model.*

interface ClientRepository {
    suspend fun getAppVersion():String

    suspend fun authenticateUser(data: HashMap<String, Any>): Int

    suspend fun verifyUserOTP(data: HashMap<String, Any>): UserEntity

    suspend fun transferMoneyToWallet(data: HashMap<String, Any>, authHeader:String): TransferEntity

    suspend fun validateTransferToMobileMoney(data: HashMap<String, Any>, authHeader:String):DepositEntity

    suspend fun confirmTransferToMobileMoney(data: HashMap<String, Any>, authHeader:String):String

    suspend fun fetchMiniStatement(): List<TransferEntity>

    suspend fun getClientBalance(): AccountBalance

    /** Deposit **/
    suspend fun validateDepositToWallet(data: HashMap<String, Any>, authHeader:String): DepositEntity

    suspend fun confirmDepositToWallet(data: HashMap<String, Any>, authHeader:String): String


    suspend fun changeClientPin(data: HashMap<String, Any>, authHeader:String):String

    //suspend fun agentToken(data: HashMap<String, Any>):String
    suspend fun getClientHistory(data: HashMap<String, Any>, authHeader:String): List<HistoryEntity>

    /** NWSC **/
    suspend fun fetchNwscAreas(authHeader:String): List<String>

    suspend fun validateNwscCustomer(data: HashMap<String, Any>, authHeader:String): NwscCustomerEntity

    suspend fun validateNwscTransaction(data: HashMap<String, Any>, authHeader:String): NwscValidateEntity

    suspend fun confirmNwscTransaction(data: HashMap<String, Any>, authHeader:String): NwscConfirmEntity


    /** Withdraw **/
    suspend fun validateTransferToWallet(data: HashMap<String, Any>, authHeader:String): DepositEntity

    suspend fun confirmTransferToWallet(data: HashMap<String, Any>, authHeader:String): String

    /** Airtime **/
    suspend fun validateAirtimeCustomer(data: HashMap<String, Any>, authHeader:String): ValidateUtilityEntity

    suspend fun buyCustomerAirtime(data: HashMap<String, Any>, authHeader:String): ConfirmUtilityEntity

    /** Umeme **/
    suspend fun validateUmemeCustomer(data: HashMap<String, Any>, authHeader:String): ValidateUtilityEntity

    suspend fun confirmUmemePayment(data: HashMap<String, Any>, authHeader:String): ConfirmUtilityEntity

    /** TV Packages **/
    suspend fun getTVPackages(type:String, authHeader:String):List<TvEntity>

    suspend fun validateTVCustomer(type:String, data: HashMap<String, Any>, authHeader:String): ValidateUtilityEntity

    suspend fun confirmTVPayment(type:String, data: HashMap<String, Any>, authHeader:String): ConfirmUtilityEntity


    /** Voice Packages **/
    suspend fun getVoicePackages(type:String, authHeader:String):List<VoiceEntity>

    suspend fun validateVoiceCustomer(type:String, data: HashMap<String, Any>, authHeader:String): ValidateUtilityEntity

    suspend fun confirmVoicePayment(type:String, data: HashMap<String, Any>, authHeader:String): ConfirmUtilityEntity


    /** Data Packages **/
    suspend fun getDataPackages(type:String, authHeader:String):List<DataEntity>

    suspend fun validateDataCustomer(type:String, data: HashMap<String, Any>, authHeader:String): ValidateUtilityEntity

    suspend fun confirmDataPayment(type:String, data: HashMap<String, Any>, authHeader:String): ConfirmUtilityEntity


    /** URA **/
    suspend fun validateUraCustomer(data: HashMap<String, Any>, authHeader:String): ValidateUtilityEntity

    suspend fun confirmUraPayment(data: HashMap<String, Any>, authHeader:String): ConfirmUtilityEntity

    /** Settings; FAQs and Help */
    suspend fun getFAQs(): List<FAQEntity>

    suspend fun getHelp(): String
}