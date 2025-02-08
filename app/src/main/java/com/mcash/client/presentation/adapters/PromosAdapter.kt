package com.mcash.client.presentation.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
//import coil.load
import com.mcash.client.R
import com.mcash.client.core.models.home.PromoEntity
import com.mcash.client.databinding.LayoutPromoBannerItemBinding

class PromosAdapter(
    private val promos: List<PromoEntity>,
    private val promoClickListener: PromoClickListener
): RecyclerView.Adapter<PromosAdapter.PromosViewHolder>() {

    inner class PromosViewHolder(val binding: LayoutPromoBannerItemBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PromosViewHolder {
        return PromosViewHolder(
            LayoutPromoBannerItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: PromosViewHolder, position: Int) {
        val promo = promos[position]
        with(holder.binding) {

//            civBanner.load(promo.banner) {
//                crossfade(true)
//                placeholder(R.drawable.default_placeholder)
//                error(R.drawable.default_placeholder)
//            }

            root.setOnClickListener {
                promoClickListener.onPromoClicked(promo)
            }
        }
    }

    override fun getItemCount(): Int = promos.size

    interface PromoClickListener {
        fun onPromoClicked(promo: PromoEntity)
    }
}


