package com.mcash.client.presentation.ui.transactions

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mcash.client.R
import com.mcash.client.core.base.BaseFragment
import com.mcash.client.databinding.FragmentTransactionSummaryBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TransactionSummaryFragment :
    BaseFragment<FragmentTransactionSummaryBinding>(FragmentTransactionSummaryBinding::inflate) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val bundle = arguments

        if(bundle != null) {
            buildTransactionDetails(bundle)
            println("=== data : $bundle")
        }

        binding.buttonCompleteTxn.setOnClickListener {

        }
    }

    private fun buildTransactionDetails(bundle: Bundle) {
        with(bundle) {
            with(binding) {
                narrationField.text =  getString("NARRATION")
                amount.text = getString("AMOUNT")
                totalAmount.text = getString("TOTAL_AMOUNT")
                charge.text = getString("CHARGE")
                txnRef.text = getString("TRANSACTION_REFERENCE")
            }
        }
    }

}