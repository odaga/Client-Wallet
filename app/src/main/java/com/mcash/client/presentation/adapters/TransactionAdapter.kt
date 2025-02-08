package com.mcash.client.presentation.adapters

import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.mcash.client.R
import com.mcash.client.databinding.LayoutTransactionItemBinding
import com.mcash.domain.model.HistoryEntity
import com.mcash.domain.model.TransType

class TransactionAdapter() : ListAdapter<HistoryEntity, TransactionAdapter.TransactionViewHolder>(
    UiTransferDiffUtil
) {

    object UiTransferDiffUtil : DiffUtil.ItemCallback<HistoryEntity>() {
        override fun areItemsTheSame(
            oldItem: HistoryEntity,
            newItem: HistoryEntity
        ): Boolean = oldItem.id == newItem.id

        override fun areContentsTheSame(
            oldItem: HistoryEntity,
            newItem: HistoryEntity
        ): Boolean = oldItem == newItem
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        return TransactionViewHolder(LayoutTransactionItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        val transaction = getItem(position)
        holder.bind(transaction)
    }

    inner class TransactionViewHolder(val binding: LayoutTransactionItemBinding) : RecyclerView.ViewHolder(binding.root) {

        @RequiresApi(Build.VERSION_CODES.O)
        fun bind(transfer: HistoryEntity) {
            with(binding) {
                val icon = when(transfer.transactionId) {
                    TransType.AIRTIME -> R.drawable.ic_phone
                    TransType.AIRTEL_TOP_UP, TransType.MTN_TOP_UP -> R.drawable.ic_wallet
                    TransType.TV -> R.drawable.ic_tv
                    TransType.NWSC -> R.drawable.ic_tap
                    TransType.UMEME -> R.drawable.ic_bulb
                    TransType.MCASH_TRANSFER, TransType.BANK_TRANSFER -> R.drawable.ic_compare_arrows
                    TransType.DEPOSIT -> R.drawable.ic_credit_card
                    TransType.COMMISSION -> R.drawable.ic_money_circle
                    TransType.FUEL_SAVE -> R.drawable.ic_station
                    else -> R.drawable.ic_apps
                }

                civStatusIcon.load(icon)

                if (transfer.formattedAmount == "Positive") {
                    mtvAmount.setTextColor(ContextCompat.getColor(binding.root.context, R.color.success))
                    mtvStatus.setTextColor(ContextCompat.getColor(binding.root.context, R.color.success))
                } else {
                    mtvAmount.setTextColor(ContextCompat.getColor(binding.root.context, R.color.danger))
                    mtvStatus.setTextColor(ContextCompat.getColor(binding.root.context, R.color.success))
                }


                mtvStatus.text = "Processed"
                mtvAmount.text = transfer.money
                tvName.text = transfer.transactionType
                tvDate.text = transfer.transactionDate
            }
        }
    }
}