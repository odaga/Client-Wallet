package com.mcash.client.presentation.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.mcash.client.databinding.RowContactDataBinding
import com.mcash.domain.model.Beneficiary

class BeneficiaryAdapter(
    private val context: Context,
    private val beneficiaryClickListener: BeneficiaryClickListener
) : RecyclerView.Adapter<BeneficiaryAdapter.BeneficiaryViewHolder>() {

    val layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    var contacts = ArrayList<Beneficiary>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BeneficiaryViewHolder {
        return BeneficiaryViewHolder(RowContactDataBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount() = contacts.size

    override fun onBindViewHolder(holder: BeneficiaryViewHolder, position: Int) {
        val beneficiary = contacts[position]
        with(holder.binding) {
            civDelete.isVisible = true
            tvContactName.text = beneficiary.name
            if(beneficiary.numbers.isNotEmpty()) {
                tvContactPhone.text = beneficiary.numbers[0]
            }

            root.setOnClickListener { beneficiaryClickListener.onBeneficiaryClicked(beneficiary) }
            civDelete.setOnClickListener { beneficiaryClickListener.onBeneficiaryDelete(beneficiary) }
        }
    }

    inner class BeneficiaryViewHolder(val binding: RowContactDataBinding) : RecyclerView.ViewHolder(binding.root)

    interface BeneficiaryClickListener {
        fun onBeneficiaryClicked(beneficiary: Beneficiary)
        fun onBeneficiaryDelete(beneficiary: Beneficiary)
    }
}