package com.mcash.data.remote.remoteMappers

import com.mcash.data.remote.model.RemoteAccountBalance
import com.mcash.domain.model.AccountBalance
import javax.inject.Inject

class RemoteAccountMapper @Inject constructor(): BaseRemoteMapper<RemoteAccountBalance, AccountBalance> {
    override fun mapToDomain(entity: RemoteAccountBalance): AccountBalance {
        return AccountBalance(amount = entity.amount)
    }
}