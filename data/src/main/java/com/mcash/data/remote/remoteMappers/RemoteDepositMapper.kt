package com.mcash.data.remote.remoteMappers

import com.mcash.data.remote.model.ValidateDepositResponse
import com.mcash.domain.model.DepositEntity
import javax.inject.Inject

class RemoteValidateDepositMapper @Inject constructor(

): BaseRemoteMapper<ValidateDepositResponse, DepositEntity> {
    override fun mapToDomain(entity: ValidateDepositResponse): DepositEntity {
        return DepositEntity(
            name = entity.name,
            amount = entity.amount,
            charge = entity.charge,
            transactionRef = entity.transactionRef,
            transactionToken = entity.transactionToken
        )
    }
}