package com.mcash.client.presentation.adapters

import android.animation.LayoutTransition
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mcash.data.remote.model.FAQ
import com.mcash.client.databinding.ItemFaqLayoutBinding

class FaqAdapter(val faqList: List<FAQ>, val context: Context): RecyclerView.Adapter<FaqAdapter.FaqVH>() {

    class FaqVH(val binding: ItemFaqLayoutBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FaqVH {
        return FaqVH(ItemFaqLayoutBinding.inflate(LayoutInflater.from(context), parent, false))
    }

    override fun getItemCount(): Int {
        return faqList.size
    }

    override fun onBindViewHolder(holder: FaqVH, position: Int) {
        val faq = faqList[position]
        with(holder.binding) {
            this.title.text = faq.title
            this.info.text = faq.content

            this.container.setOnClickListener {

                if (this.info.visibility == View.VISIBLE) {
                    LayoutTransition.CHANGE_APPEARING
                    this.info.visibility = View.GONE
                } else  {
                    LayoutTransition.CHANGING
                    this.info.visibility = View.VISIBLE
                }

//                for (i in faqList) {
//                    if (i == faq) {
//                        this.info.visibility = View.GONE
//                    } else {
//                        this.info.visibility = View.VISIBLE
//                    }
//                }

            }
        }
    }
}