package com.mcash.client.presentation.adapters

import android.app.Activity
import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.widget.ImageViewCompat
import androidx.recyclerview.widget.RecyclerView
import com.mcash.client.core.models.home.HomeIcon
import com.mcash.client.databinding.LayoutHomeIconItemBinding
import com.mcash.client.core.utils.AlertType
import com.mcash.client.core.utils.Constants.HomeIds.AIRTIME
import com.mcash.client.core.utils.Constants.HomeIds.BUNDLES
import com.mcash.client.core.utils.Constants.HomeIds.INTERNET_BUNDLES
import com.mcash.client.core.utils.Constants.HomeIds.LOANS
import com.mcash.client.core.utils.Constants.HomeIds.MORE
import com.mcash.client.core.utils.Constants.HomeIds.PAY_MERCHANT
import com.mcash.client.core.utils.Constants.HomeIds.PAY_NWSC
import com.mcash.client.core.utils.Constants.HomeIds.PAY_TV
import com.mcash.client.core.utils.Constants.HomeIds.PAY_UMEME
import com.mcash.client.core.utils.Constants.HomeIds.SACCO
import com.mcash.client.core.utils.Constants.HomeIds.SCHOOL_FEES
import com.mcash.client.core.utils.Constants.HomeIds.STATEMENT
import com.mcash.client.core.utils.Constants.HomeIds.URA
import com.mcash.client.core.utils.Constants.HomeIds.VOICE_BUNDLES
import com.mcash.client.core.utils.ToastUtils.showAlert

class HomeIconAdapter(
    private val list: List<HomeIcon>,
    private val activity: Activity,
    private val homeIconClickListener: HomeIconClickListener
): RecyclerView.Adapter<HomeIconAdapter.HomeIconViewHolder>() {
    inner class HomeIconViewHolder(val binding: LayoutHomeIconItemBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): HomeIconViewHolder {
        return HomeIconViewHolder(
            LayoutHomeIconItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: HomeIconViewHolder, position: Int) {
        val icon = list[position]
        with(holder.binding) {
            civIcon.setImageResource(icon.icon)
            if((position == 0)|| (position == 1) || (position == 2))
            {
                ImageViewCompat.setImageTintList(civIcon, null)
            }
            mtvTitle.text = icon.title

            root.setOnClickListener {
                when(icon.id) {
                    AIRTIME -> {
                        homeIconClickListener.onAirtimeClicked()
                    }

                    INTERNET_BUNDLES -> {
                        homeIconClickListener.onInternetClicked()
                    }

                    PAY_UMEME -> {
                        homeIconClickListener.onUmemeClicked()
                    }

                    BUNDLES -> {
                        homeIconClickListener.onBundlesClicked()
                    }

                    PAY_TV -> {
                        homeIconClickListener.onTVClicked()
                    }

                    PAY_NWSC -> {
                        homeIconClickListener.onNwscClicked()
                    }

                    SCHOOL_FEES -> {
                        activity.showAlert("Mcash", "Fees", AlertType.INFO)
                    }

                    PAY_MERCHANT -> {
                        homeIconClickListener.onMerchantClicked()
                        //activity.showAlert("Mcash", "Merchant", AlertType.INFO)
                    }

                    STATEMENT -> {
                        activity.showAlert("Mcash", "Statement", AlertType.INFO)
                    }
                    URA -> {
                        homeIconClickListener.onUraClicked()
                    }
                    VOICE_BUNDLES -> {
                        homeIconClickListener.onVoiceClicked()
                    }
                    LOANS -> {
                        homeIconClickListener.onLoansClicked()
                    }
                    SACCO -> {
                        homeIconClickListener.onSaccoClicked()
                    }

                    MORE -> {
                        homeIconClickListener.onMoreClicked()
                    }
                }
            }
        }
    }

    override fun getItemCount(): Int = list.size

    interface HomeIconClickListener {
        fun onMoreClicked()
        fun onNwscClicked()
        fun onUmemeClicked()
        fun onAirtimeClicked()
        fun onInternetClicked()
        fun onTVClicked()
        fun onVoiceClicked()
        fun onUraClicked()
        fun onMerchantClicked()
        fun onLoansClicked()

        fun onSaccoClicked()

        fun onBundlesClicked()
    }
}