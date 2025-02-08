package com.mcash.data.remote.remoteMappers

import com.mcash.data.remote.model.DataPackage
import com.mcash.domain.model.DataEntity
import javax.inject.Inject

class DataPackageMapper @Inject constructor(): BaseRemoteMapper<DataPackage, DataEntity> {
    override fun mapToDomain(entity: DataPackage): DataEntity {
        return DataEntity(
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