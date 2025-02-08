package com.mcash.data.remote.remoteMappers

import com.mcash.data.remote.model.RemoteTransfer
import com.mcash.domain.model.TransferEntity
import javax.inject.Inject

class RemoteTransferMapper @Inject constructor(
    private val remoteUserMapper: RemoteUserMapper,
    private val remoteTransferTypeMapper: RemoteTransferTypeMapper
) : BaseRemoteMapper<RemoteTransfer, TransferEntity> {
    override fun mapToDomain(entity: RemoteTransfer): TransferEntity {
        return TransferEntity(
            id = entity.id,
            amount = entity.amount.orEmpty(),
            date = entity.date.orEmpty(),
            description = entity.description.orEmpty(),
            status = entity.status.orEmpty(),
            formattedAmount = entity.formattedAmount.orEmpty(),
            formattedDate = entity.formattedDate.orEmpty(),
            member = entity.member?.let { remoteUserMapper.mapToDomain(it) },
            formattedProcessDate = entity.formattedProcessDate.orEmpty(),
            processDate = entity.processDate.orEmpty(),
            systemAccountName = entity.systemAccountName.orEmpty(),
            transferType = entity.transferType?.let { remoteTransferTypeMapper.mapToDomain(it) }
        )
    }
}