package com.mcash.data.remote.remoteMappers

import com.mcash.data.remote.model.VoicePackage
import com.mcash.domain.model.VoiceEntity
import javax.inject.Inject

class VoicePackageMapper @Inject constructor(): BaseRemoteMapper<VoicePackage, VoiceEntity> {
    override fun mapToDomain(entity: VoicePackage): VoiceEntity {
        return VoiceEntity(
            packageId = entity.packageId,
            isAmountFixed = entity.isAmountFixed,
            paymentitemname = entity.paymentitemname,
            amount = entity.amount,
            code = entity.code,
            currencyCode = entity.currencyCode,
            currencySymbol = entity.currencySymbol,
            itemCurrencySymbol = entity.itemCurrencySymbol,
            sortOrder = entity.sortOrder,
            pictureId = entity.pictureId
        )
    }
}