package com.mcash.data.remote.remoteMappers

import com.mcash.data.remote.model.TVPackage
import com.mcash.domain.model.TvEntity
import javax.inject.Inject

class TVPackageMapper @Inject constructor(): BaseRemoteMapper<TVPackage, TvEntity> {
    override fun mapToDomain(entity: TVPackage): TvEntity {
        return TvEntity(
            packageId = entity.packageId,
            isAmountFixed = entity.isAmountFixed,
            paymentitemname = entity.paymentitemname,
            amount = entity.amount,
            code = entity.code,
            currencyCode = entity.currencyCode,
            currencySymbol = entity.currencySymbol,
            itemCurrencySymbol = entity.itemCurrencySymbol,
            sortOrder = entity.sortOrder,
            pictureId = entity.pictureId,
            provider = ""

        )
    }
}