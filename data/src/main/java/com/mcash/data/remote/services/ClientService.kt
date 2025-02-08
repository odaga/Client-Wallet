package com.mcash.data.remote.services

import com.mcash.data.remote.model.*
import retrofit2.http.*

interface ClientService {

    @POST("api/client/login")
    suspend fun authenticateUser(@Body data: HashMap<String, Any>): LoginResponse

    @POST("api/client/login")
    suspend fun verifyUserOtp(@Body data: HashMap<String, Any>): VerifyOtpResponse

    @POST("api/transfer")
    suspend fun transferMoney(
        @Body data: HashMap<String, Any>,
        @Header("Authorization") authHeader: String
    ): RemoteTransferResponse

    @POST("api/client/transfer/mm/validate")
    suspend fun validateTransferToMobileMoney(
        @Body data: HashMap<String, Any>,
        @Header("Authorization") authHeader: String
    ): RemoteValidateDepositResponse

    @POST("api/client/transfer/mm/confirm")
    suspend fun confirmTransferToMobileMoney(
        @Body data: HashMap<String, Any>,
        @Header("Authorization") authHeader: String
    ): RemoteConfirmDepositResponse

    @POST("api/client/checkbalance")
    suspend fun getAccountBalance(@Body data: HashMap<String, Any>): AccountBalanceResponse

    @POST("api/client/ministatement")
    suspend fun getMiniStatement(@Body data: HashMap<String, Any>): RemoteTransactionResponse

//    @POST("nwsc/verifycustomer")
//    suspend fun verifyNwsc(@Body data: HashMap<String, Any>, @Header("Authorization") authHeader:String): VerifyNwscResponse

    @POST("api/reset/change/pin")
    suspend fun changePin(
        @Body data: HashMap<String, Any>,
        @Header("Authorization") authHeader: String
    ): ResetPinResponse

    @POST("api/client/ministatement")
    suspend fun getClientHistory(
        @Body data: HashMap<String, Any>,
        @Header("Authorization") authHeader: String
    ): RemoteHistory

    /** NWSC **/
    @GET("api/client/nwsc/listdistricts")
    suspend fun getNwscAreas(@Header("Authorization") authHeader: String): NwscAreasResponse

    @POST("api/client/nwsc/customer/validate")
    suspend fun validateNwscCustomer(
        @Body data: HashMap<String, Any>,
        @Header("Authorization") authHeader: String
    ): RemoteValidateNwscCustomer

    @POST("api/client/nwsc/transaction/validate")
    suspend fun validateNwscTransaction(
        @Body data: HashMap<String, Any>,
        @Header("Authorization") authHeader: String
    ): RemoteValidateNwscTransaction

    @POST("api/client/nwsc/transaction/confirm")
    suspend fun confirmNwscTransaction(
        @Body data: HashMap<String, Any>,
        @Header("Authorization") authHeader: String
    ): RemoteConfirmNwscTransaction

    /** Deposit **/
    @POST("api/client/deposit/validate")
    suspend fun validateDepositToWallet(
        @Body data: HashMap<String, Any>,
        @Header("Authorization") authHeader: String
    ): RemoteValidateDepositResponse

    @POST("api/client/deposit/confirm")
    suspend fun confirmDepositToWallet(
        @Body data: HashMap<String, Any>,
        @Header("Authorization") authHeader: String
    ): RemoteConfirmDepositResponse

    /** Withdraw **/
    @POST("api/client/transfer/validate")
    suspend fun validateTransferToWallet(
        @Body data: HashMap<String, Any>,
        @Header("Authorization") authHeader: String
    ): RemoteValidateDepositResponse

    @POST("api/client/transfer/confirm")
    suspend fun confirmTransferToWallet(
        @Body data: HashMap<String, Any>,
        @Header("Authorization") authHeader: String
    ): RemoteConfirmTransferResponse

    /** Airtime **/
    @POST("api/client/buyairtime/customer/validate")
    suspend fun validateAirtimeCustomer(
        @Body data: HashMap<String, Any>,
        @Header("Authorization") authHeader: String
    ): RemoteUtilityValidation

    @POST("api/client/buyairtime/customer/confirm")
    suspend fun buyCustomerAirtime(
        @Body data: HashMap<String, Any>,
        @Header("Authorization") authHeader: String
    ): RemoteConfirmUtility

    /** Umeme **/
    @POST("api/client/umeme/customer/validate")
    suspend fun validateUmemeCustomer(
        @Body data: HashMap<String, Any>,
        @Header("Authorization") authHeader: String
    ): RemoteUtilityValidation

    @POST("api/client/umeme/customer/confirm")
    suspend fun confirmUmemePayment(
        @Body data: HashMap<String, Any>,
        @Header("Authorization") authHeader: String
    ): RemoteConfirmUtility

    /** TV Packages **/
    @GET("api/client/tv/packages/{type}")
    suspend fun getTVPackages(
        @Path("type") type: String,
        @Header("Authorization") authHeader: String
    ): TVPackageResponse

    @POST("api/client/tv/customer/validate/{type}")
    suspend fun validateTVCustomer(
        @Path("type") type: String,
        @Body data: HashMap<String, Any>,
        @Header("Authorization") authHeader: String
    ): RemoteUtilityValidation

    @POST("api/client/tv/customer/validate/{type}")
    suspend fun confirmTVPayment(
        @Path("type") type: String,
        @Body data: HashMap<String, Any>,
        @Header("Authorization") authHeader: String
    ): RemoteConfirmUtility


    /** Voice Bundles **/
    @GET("api/client/bundles/voice/packages/{type}")
    suspend fun getVoicePackages(
        @Path("type") type: String,
        @Header("Authorization") authHeader: String
    ): VoicePackageResponse

    @POST("api/client/bundles/voice/customer/validate/{type}")
    suspend fun validateVoiceCustomer(
        @Path("type") type: String,
        @Body data: HashMap<String, Any>,
        @Header("Authorization") authHeader: String
    ): RemoteUtilityValidation

    @POST("api/client/bundles/voice/payment/confirm/{type}")
    suspend fun confirmVoicePayment(
        @Path("type") type: String,
        @Body data: HashMap<String, Any>,
        @Header("Authorization") authHeader: String
    ): RemoteConfirmUtility


    /** Data Bundles **/
    @GET("api/client/bundles/data/packages/{type}")
    suspend fun getDataPackages(
        @Path("type") type: String,
        @Header("Authorization") authHeader: String
    ): DataPackageResponse

    @POST("api/client/bundles/data/customer/validate/{type}")
    suspend fun validateDataCustomer(
        @Path("type") type: String,
        @Body data: HashMap<String, Any>,
        @Header("Authorization") authHeader: String
    ): RemoteUtilityValidation

    @POST("api/client/bundles/data/payment/confirm/{type}")
    suspend fun confirmDataPayment(
        @Path("type") type: String,
        @Body data: HashMap<String, Any>,
        @Header("Authorization") authHeader: String
    ): RemoteConfirmUtility

    /** URA **/
    @POST("api/client/ura/customer/validate")
    suspend fun validateUraPayment(
        @Body data: HashMap<String, Any>,
        @Header("Authorization") authHeader: String
    ): RemoteUtilityValidation

    @POST("api/client/ura/customer/confirm")
    suspend fun confirmUraPayment(
        @Body data: HashMap<String, Any>,
        @Header("Authorization") authHeader: String
    ): RemoteConfirmUtility

    @GET("faqs")
    suspend fun getFAQs(): RemoteFAQ

    @GET("help")
    suspend fun getHelp(): String


    /**
     *  Endpoints for the Kafta project
     *
     *
     */

    @GET("api/v2/kafta/branches/{branchId}")
    suspend fun getBranchDetails(
        @Path("branchId") branchId: String,
        @Header("Authentication") authHeader: String
    ): BranchDetailsResponse

    @POST("api/v2/client/kafta/purchase/validate")
    suspend fun validateKaftaPurchase(
        @Body data: HashMap<String, Any>,
        @Header("Authorization") authHeader: String
    ): ValidateKaftaPurchaseResponse


    @POST("api/v2/client/kafta/purchase/confirm")
    suspend fun confirmKaftaPurchase(
        @Body data: HashMap<String, Any>,
        @Header("Authorization") authHeader: String
    ): ConfirmKaftaPurchaseResponse

    @POST("api/v2/kafta/drivers/create")
    suspend fun registerDriver(
        @Body data: HashMap<String, Any>,
        @Header("Authorization") authHeader: String
    ): RegisterDriverResponse


    @GET("api/v2/kafta/drivers/{driverCode}")
    suspend fun getDriver(
        @Path("driverCode") driverCode: String,
        @Header("Authorization") authHeader: String
    ): GetDriverResponse


    @POST("api/v2/client/kafta/drivers/loyalty-balance")
    suspend fun getLoyaltyPoints(
        @Body data: HashMap<String, Any>,
        @Header("Authorization") authHeader: String
    ): GetLoyaltyPointsResponse



}