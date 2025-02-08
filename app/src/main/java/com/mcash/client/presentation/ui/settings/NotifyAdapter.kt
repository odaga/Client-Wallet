package com.mcash.client.presentation.ui.settings


import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.mcash.client.R
import com.mcash.client.databinding.LayoutTransactionItemBinding
import com.mcash.domain.model.NotificationEntity
import com.mcash.domain.model.TransType

class NotifyAdapter(val updateNotification: UpdateNotification) : ListAdapter<NotificationEntity, NotifyAdapter.NotifyViewHolder>(
    UiTransferDiffUtil
) {

//    val layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    object UiTransferDiffUtil : DiffUtil.ItemCallback<NotificationEntity>() {
        override fun areItemsTheSame(
            oldItem: NotificationEntity,
            newItem: NotificationEntity
        ): Boolean = oldItem.id == newItem.id

        override fun areContentsTheSame(
            oldItem: NotificationEntity,
            newItem: NotificationEntity
        ): Boolean = oldItem == newItem
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotifyViewHolder {
        return NotifyViewHolder(LayoutTransactionItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: NotifyViewHolder, position: Int) {
        val transaction = getItem(position)
        holder.bind(transaction)
    }

    inner class NotifyViewHolder(val binding: LayoutTransactionItemBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(entity: NotificationEntity) {
            with(binding) {

                mtvStatus.text = entity.message
                mtvAmount.visibility = View.GONE
                tvName.text = entity.title
                tvDate.text = entity.date
                mtvStatus.text = entity.message
                mtvStatus.background.setTint(Color.WHITE)

                updateNotification.onClick()

            }
        }
    }
}

interface UpdateNotification {
    fun onClick()
}