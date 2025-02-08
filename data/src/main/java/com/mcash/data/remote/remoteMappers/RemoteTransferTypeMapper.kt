package com.mcash.data.remote.remoteMappers

import com.mcash.data.remote.model.RemoteTransferType
import com.mcash.domain.model.TransferType
import javax.inject.Inject

class RemoteTransferTypeMapper @Inject constructor(): BaseRemoteMapper<RemoteTransferType, TransferType> {
    override fun mapToDomain(entity: RemoteTransferType): TransferType {
        return TransferType(
            id = entity.id,
            name = entity.name.orEmpty()
        )
    }
}