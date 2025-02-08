package com.mcash.domain.model

import android.os.Build
import androidx.annotation.RequiresApi
import com.mcash.domain.utils.formatDateTime
import com.mcash.domain.utils.formatDomainCurrency

data class TransferEntity(
    val amount: String,
    val date: String,
    val description: String,
    val formattedAmount: String,
    val formattedDate: String,
    val formattedProcessDate: String,
    val id: Long,
    val processDate: String,
    val status: String,
    val systemAccountName: String,
    val transferType: TransferType?,
    val member: UserEntity?
) {

    val formattedStatus = if (status.equals("processed", ignoreCase = true)) "Success" else "Failed"

    val isIncrease = amount.toDouble() > 0.0

    @RequiresApi(Build.VERSION_CODES.O)
    val transactionDate = date.take(19).formatDateTime()

    val money = formatCash(amount)

    private fun formatCash(money: String): String {
        val cash = money.toDouble()
        return when {
            cash < 0.0 -> "-${money.replace("-", "").toDouble().formatDomainCurrency()}"
            cash > 0.0 -> "+${cash.formatDomainCurrency()}"
            else -> cash.formatDomainCurrency()
        }
    }
}

data class TransferType(
    val id: Int,
    val name: String
) {

    val transferName = when (id) {
        0 -> "Pay TV"
        226 -> "Pay NWSC"
        34 -> "Mcash to Mcash transfer"
        37 -> "Cash-In Deposit"
        39 -> "Transaction charge"
        54 -> "Buy Airtime"
        55 -> "Pay Umeme"
        67 -> "Bank transfer"
        101 -> "MTN to wallet topup"
        194 -> "Airtel to wallet topup"
        else -> "Transaction"
    }

    val type = when (id) {
        0 -> TransType.TV
        226 -> TransType.NWSC
        34 -> TransType.MCASH_TRANSFER
        37 -> TransType.DEPOSIT
        39 -> TransType.COMMISSION
        54 -> TransType.AIRTIME
        55 -> TransType.UMEME
        67 -> TransType.BANK_TRANSFER
        101 -> TransType.MTN_TOP_UP
        194 -> TransType.AIRTEL_TOP_UP
        else -> TransType.DEFAULT
    }
}

enum class TransType {
    TV,
    NWSC,
    MCASH_TRANSFER,
    DEPOSIT,
    COMMISSION,
    AIRTIME,
    UMEME,
    BANK_TRANSFER,
    MTN_TOP_UP,
    AIRTEL_TOP_UP,
    FUEL_SAVE,
    DEFAULT
}