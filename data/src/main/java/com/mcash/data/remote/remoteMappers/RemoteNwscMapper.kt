package com.mcash.data.remote.remoteMappers

import com.mcash.data.remote.model.ConfirmNwscTransaction
import com.mcash.data.remote.model.ValidateNwscCustomer
import com.mcash.data.remote.model.ValidateNwscTransaction
import com.mcash.domain.model.NwscConfirmEntity
import com.mcash.domain.model.NwscCustomerEntity
import com.mcash.domain.model.NwscValidateEntity
import javax.inject.Inject

class RemoteNwscCustomerMapper @Inject constructor() : BaseRemoteMapper<ValidateNwscCustomer, NwscCustomerEntity> {
    override fun mapToDomain(entity: ValidateNwscCustomer): NwscCustomerEntity {
        return NwscCustomerEntity(
            customer_account = entity.customer_account,
            property_ref = entity.property_ref,
            customer_name = entity.customer_name,
            customer_area  = entity.customer_area,
            outstanding_balance = entity.outstanding_balance
        )
    }
}

class RemoteValidateNwscMapper @Inject constructor( ): BaseRemoteMapper<ValidateNwscTransaction, NwscValidateEntity> {
    override fun mapToDomain(entity: ValidateNwscTransaction): NwscValidateEntity {
        return NwscValidateEntity(
            customer_account = entity.customer_account,
            customer_name = entity.customer_name,
            outstanding_balance = entity.outstanding_balance,
            amount = entity.amount,
            amount_payable = entity.amount_payable,
            charge = entity.charge,
            transactionToken = entity.transactionToken,
            transaction_ref = entity.transaction_ref
        )
    }
}


class RemoteConfirmNwscMapper @Inject constructor() : BaseRemoteMapper<ConfirmNwscTransaction, NwscConfirmEntity> {
    override fun mapToDomain(entity: ConfirmNwscTransaction): NwscConfirmEntity {
        return NwscConfirmEntity(
            customer_account = entity.customer_account,
            customer_name = entity.customer_name,
            outstanding_balance = entity.outstanding_balance,
            amount = entity.amount,
            amount_payable = entity.amount_payable,
            charge = entity.charge,
            transactionToken = entity.transactionToken,
            transaction_ref = entity.transaction_ref
        )
    }

}