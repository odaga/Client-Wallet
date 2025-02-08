package com.mcash.data.remote.remoteMappers

import com.mcash.data.remote.model.UtilityValidateData
import com.mcash.data.remote.model.ConfirmUtilityData
import com.mcash.domain.model.ConfirmUtilityEntity
import com.mcash.domain.model.ValidateUtilityEntity
import javax.inject.Inject

class ValidateUtilityMapper @Inject constructor(

): BaseRemoteMapper<UtilityValidateData, ValidateUtilityEntity> {
    override fun mapToDomain(entity: UtilityValidateData): ValidateUtilityEntity {
        return ValidateUtilityEntity(
            customerName = entity.customerName,
            responseCode = entity.responseCode,
            biller = entity.biller,
            excise = entity.excise,
            surchargeType = entity.surchargeType,
            transactionRef = entity.transactionRef,
            isAmountFixed = entity.isAmountFixed,
            surcharge = entity.surcharge,
            paymentItemId = entity.paymentItemId,
            amount = entity.amount,
            paymentItem = entity.paymentItem,
            shortTransactionRef = entity.shortTransactionRef,
            balance = entity.balance,
            customerId = entity.customerId,
            balanceNarration = entity.balanceNarration,
            collectionsAccountNumber = entity.collectionsAccountNumber,
            narration = entity.narration,
            collectionsAccountType = entity.collectionsAccountType,
            displayBalance = entity.displayBalance,
            balanceType = entity.balanceType,
            transactionToken = entity.transactionToken
        )
    }
}

class ConfirmUtilityMapper @Inject constructor(): BaseRemoteMapper<ConfirmUtilityData, ConfirmUtilityEntity> {
    override fun mapToDomain(entity: ConfirmUtilityData): ConfirmUtilityEntity {
        return ConfirmUtilityEntity(
            customer_reference = entity.customer_reference,
            customer_phone = entity.customer_phone,
            amount = entity.amount,
            service_code = entity.service_code,
            payment_ref = entity.payment_ref,
            transaction_ref = entity.transaction_ref
        )
    }
}