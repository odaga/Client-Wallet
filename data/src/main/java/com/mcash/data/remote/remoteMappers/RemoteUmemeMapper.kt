package com.mcash.data.remote.remoteMappers

import com.mcash.data.remote.model.PayUmemeData
import com.mcash.data.remote.model.VerifyUmemeData
import com.mcash.domain.model.PayUmemeEntity
import com.mcash.domain.model.VerifyUmemeEntity
import javax.inject.Inject

class RemoteVerifyUmemeMapper @Inject constructor(): BaseRemoteMapper<VerifyUmemeData, VerifyUmemeEntity> {
    override fun mapToDomain(entity: VerifyUmemeData): VerifyUmemeEntity {
        return VerifyUmemeEntity(
            CustomerRef = entity.CustomerRef,
            CustomerName = entity.CustomerName,
            CustomerType = entity.CustomerType,
            Balance = entity.Balance,
            Credit = entity.Credit
        )
    }
}

class RemotePayUmemeMapper @Inject constructor(): BaseRemoteMapper<PayUmemeData, PayUmemeEntity>{
    override fun mapToDomain(entity: PayUmemeData): PayUmemeEntity {
        return PayUmemeEntity(
            AccountPayAmount = entity.AccountPayAmount,
            AccountSaveAmount = entity.AccountSaveAmount,
            RemainingCredit = entity.RemainingCredit,
            LifeLine = entity.LifeLine,
            ServiceFee = entity.ServiceFee,
            DebtRecovery = entity.DebtRecovery,
            ReceiptNumber = entity.ReceiptNumber,
            StatusDescription = entity.StatusDescription,
            StatusCode = entity.StatusCode,
            MeterNumber = entity.MeterNumber,
            Units = entity.Units,
            TokenValue = entity.TokenValue,
            Inflation = entity.Inflation,
            Tax = entity.Tax,
            Fx = entity.Fx,
            Fuel = entity.Fuel,
            TotalAmount = entity.TotalAmount,
            PrepaidToken = entity.PrepaidToken
        )
    }

}