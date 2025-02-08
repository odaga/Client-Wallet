package com.mcash.data.remote.remoteMappers

import com.mcash.data.remote.model.RemoteHistoryData
import com.mcash.domain.model.HistoryEntity
import javax.inject.Inject

class RemoteHistoryMapper @Inject constructor(): BaseRemoteMapper<RemoteHistoryData, HistoryEntity> {
    override fun mapToDomain(entity: RemoteHistoryData): HistoryEntity {
        return HistoryEntity(
            id = entity.id.orEmpty(),
            date = entity.date.orEmpty(),
            amount = entity.amount.orEmpty(),
            type = entity.type?.internalName.orEmpty(),
            //type = entity.type?.id.orEmpty(),
            description = entity.description,
            relatedAccount = null,
            transaction = null,
        )
    }
}