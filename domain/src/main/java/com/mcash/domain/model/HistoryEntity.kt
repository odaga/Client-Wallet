package com.mcash.domain.model

import android.os.Build
import androidx.annotation.RequiresApi
import com.mcash.domain.utils.formatDateTime
import com.mcash.domain.utils.formatDomainCurrency

data class HistoryEntity(
    val id:String?,
    val date:String?,
    val amount:String?,
    val type:String?,
    val description:String?,
    val relatedAccount:RelatedAccount?,
    val transaction:Transaction?
) {
    val transactionDate = date?.take(19)?.formatDateTime()

    val money = formatCash(amount?:"0.0")

    private fun formatCash(money: String): String {
        val cash = money.toDouble()
        return when {
            cash < 0.0 -> "-${money.replace("-", "").toDouble().formatDomainCurrency()}"
            cash > 0.0 -> "+${cash.formatDomainCurrency()}"
            else -> cash.formatDomainCurrency()
        }
    }

    val formattedAmount = isAmount(amount?:"0.0")

    private fun isAmount(money: String): String {
        val cash = money.toDouble()
        return when {
            cash < 0.0 -> "Negative"
            cash > 0.0 -> "Positive"
            else -> "Zero"
        }
    }

    val transactionType = TransactionType(type?:"0").typeName

    val transactionId = TransactionType(type ?: "0").type

}


data class RelatedAccount(
    val id:String?,
    val type: Type?,
    val user:RelatedAccountUser?,
    val kind:String?
)

data class RelatedAccountUser(
    val id: String?,
    val display:String?
)

data class Transaction(
    val id:String?,
    val kind: String?
)

data class Type(
    val id:String?,
    val name:String?,
    val internalName:String?
)


data class TransactionType(
    val typeId: String
) {
    //785930453088158322 - commission

    val typeName = when (typeId) {
        "0" -> "Pay TV"
        "agent_trans.nwsc_direct" -> "Pay NWSC"
        "agent_trans.quickteller_startimes" -> "Star times"
        "agent_trans.quickteller_dstv_gotv_zuku" -> "MultiChoice"
        "client_trans.cashOut" -> "Client CashOut"
        "agent_trans.clientCashin" -> "Client CashIn"
        "agent_trans.quickteller_airtime_data" -> "Buy Data/Airtime"
        "agent_trans.quickteller_umeme" -> "Pay Umeme"
        "client_trans.client_client" -> "Client to Client transfer"
        "client_trans.clientTransactionCharge" -> "Client Transaction Charges"
        "client_trans.interswitchBalanceInquriy" -> "Interswitch ATM Balance Inquiry"
        "agent_trans.cente_agent_deposit_cente" -> "Centenary Deposit"
        "merchant_trans.cente_agent_withdraw_voucher" -> "Centenary Withdraw"
        "agent_trans.cente_school_pay" -> "School Pay"
        "client_trans.fuel_purchase" -> "Fuel Plus"
        //"merchant_trans.cente_agent_withdraw_voucher" -> "Agent Float TopUp"
        else -> "Transaction"
    }

    val type = when (typeId) {
        "0" -> TransType.TV
        "agent_trans.nwsc_direct" -> TransType.NWSC
        "agent_trans.quickteller_startimes" -> TransType.TV
        "agent_trans.quickteller_dstv_gotv_zuku" -> TransType.TV
        "client_trans.cashOut"-> TransType.MCASH_TRANSFER
        "agent_trans.clientCashin"-> TransType.DEPOSIT
        "client_trans.client_client "-> TransType.MCASH_TRANSFER
        "agent_trans.quickteller_airtime_data"-> TransType.AIRTIME
        "agent_trans.quickteller_umeme"-> TransType.UMEME
        "client_trans.clientTransactionCharge"-> TransType.BANK_TRANSFER
        "client_trans.interswitchBalanceInquriy" -> TransType.MTN_TOP_UP
        "client_trans.fuel_purchase" -> TransType.FUEL_SAVE
        "194" -> TransType.AIRTEL_TOP_UP
        else -> TransType.DEFAULT
    }
}