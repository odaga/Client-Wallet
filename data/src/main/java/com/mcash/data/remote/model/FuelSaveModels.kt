package com.mcash.data.remote.model

import com.google.gson.annotations.SerializedName


data class RegisterDriverResponse(
    @SerializedName("status_code")
    val statusCode: String?,
    @SerializedName("app_status")
    val appStatus: Boolean?,
    val data: Any
)


data class GetDriverResponse(
    @SerializedName("status_code")
    val statusCode: String?,
    @SerializedName("app_status")
    val appStatus: Boolean?,
    val data: DriverData
)


data class DriverData(
    val id: String?,
    @SerializedName("association_id")
    val associationId: String?,
    @SerializedName("stage_id")
    val stageId: String?,
    @SerializedName("full_name")
    val fullName: String?,
    @SerializedName("user_id")
    val userId: String?,
    val slug: String?,
    @SerializedName("phone_number")
    val phoneNumber: String?,
    val dob: String?,
    val gender: String?,
    @SerializedName("license_number")
    val licenseNumber: String?,
    @SerializedName("license_expiry_date")
    val licenseExpiryDate: String?,
    @SerializedName("created_by_id")
    val createdById: String?,
    val email: String?,
    @SerializedName("association_name")
    val associationName: String?
)


data class BranchDetailsResponse(
    @SerializedName("status_code")
    val statusCode: Int,
    @SerializedName("app_status")
    val appStatus: Boolean,
//    val data: HashMap<String, Any>
    val data: BranchData
)

data class BranchData(
    val id: String,
    @SerializedName("branch_name")
    val branchName: String,
    @SerializedName("address_info")
    val addressInfo: String?,
    @SerializedName("station_id")
    val stationId: String?,
    val code: String?,
    val slug: String?,
    @SerializedName("created_at")
    val createdAt: HashMap<String, Any>,
    @SerializedName("updated_at")
    val updatedAt: HashMap<String, Any>,
    val station: StationData,
    val paymentItems: List<PaymentItem>

)

data class StationData(
    val id: String,
    @SerializedName("station_name")
    val stationName: String,
    @SerializedName("address_info")
    val addressInfo: String,
    @SerializedName("contact_email")
    val contactEmail: String,
    @SerializedName("cyclos_username")
    val cyclosUsername: String,
    @SerializedName("phone_number")
    val phoneNumber: String,
    @SerializedName("is_active")
    val isActive: String,
    @SerializedName("franchize_id")
    val franchizeId: String,
    val status: String,
    @SerializedName("created_by_id")
    val createdById: String,
    @SerializedName("created_at")
    val createdAt: HashMap<String, Any>,
    @SerializedName("updated_at")
    val updatedAt: HashMap<String, Any>
)

data class PaymentItem(
    val id: String,
    @SerializedName("fuel_type_id")
    val fuelTypeId: String?,
    @SerializedName("fuel_type_name")
    val fuelTypeName: String?,
    @SerializedName("station_id")
    val stationId: String?,
    val description: String?,
    @SerializedName("branch_id")
    val branchId: String?,
    @SerializedName("price_per_liter")
    val pricePerLiter: String?,
    val currency: String?,
    @SerializedName("price_date")
    val priceDate: String?,
    @SerializedName("updated_at")
    val updatedAt: String?

)


data class ValidateKaftaPurchaseResponse(
    @SerializedName("status_code")
    val statusCode: Int,
    @SerializedName("app_status")
    val appStatus: Boolean,
    val message: String?,
    val data: KaftaTransactionValidationData
)


data class KaftaTransactionValidationData(
    @SerializedName("driver_code")
    val driverCode: String?,
    @SerializedName("branch_code")
    val branchCode: String?,
    @SerializedName("fuel_type_id")
    val fuelTypeId: String?,
    val amount: String?,
    @SerializedName("price_per_liter")
    val pricePerLiter: String?,
    @SerializedName("price_date")
    val priceDate: String?,
    val status: String?,
    @SerializedName("station_id")
    val stationId: String?,
    @SerializedName("association_id")
    val associationId: String?,
    val indicator: String?,
    @SerializedName("liter_equivalent")
    val literEquivalent: String?,
    val id: String
)

data class ConfirmKaftaPurchaseResponse(
    @SerializedName("status_code")
    val statusCode: Int,
    @SerializedName("app_status")
    val appStatus: Boolean,
    val data: KaftaTransactionConfirmationData
)

data class KaftaTransactionConfirmationData(
    @SerializedName("driver_code")
    val driverCode: String?,
    @SerializedName("branch_code")
    val branchCode: String?,
    @SerializedName("fuel_type_id")
    val fuelTypeId: String?,
    val amount: String?,
    @SerializedName("price_per_liter")
    val pricePerLiter: String?,
    @SerializedName("price_date")
    val priceDate: String?,
    val status: String?,
    @SerializedName("commission_status")
    val commissionStatus: String?,
    @SerializedName("station_id")
    val stationId: String?,
    @SerializedName("association_id")
    val associationId: String?,
    val indicator: String?,
    @SerializedName("liter_equivalent")
    val literEquivalent: String?,
    @SerializedName("validated_transaction_id")
    val validatedTransactionId: String?,
    @SerializedName("transaction_ref")
    val transactionRef: String?,
    val id: String
)

data class GetLoyaltyPointsResponse(
    @SerializedName("status_code")
    val statusCode: Int,
    @SerializedName("app_status")
    val appStatus: Boolean,
    val data: LoyaltyPointsData
)

data class LoyaltyPointsData(
    val points: Float?
)