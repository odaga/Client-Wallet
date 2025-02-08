package com.mcash.client.presentation.ui.transfer

import android.os.Bundle
import com.mcash.client.databinding.FragmentBankTransferBinding
import com.mcash.client.core.base.BaseFragment

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class BankTransferFragment : BaseFragment<FragmentBankTransferBinding>(FragmentBankTransferBinding::inflate) {
    private var param1: String? = null
    private var param2: String? = null

    var banks = arrayOf(
        "ABC Capital Bank",
        "Bank Of Africa",
        "Barclays Bank",
        "Bank Of Baroda",
        "Bank Of India",
        "Cairo Bank",
        "Centenary Bank",
        "CitiBank",
        "Crane Bank",
        "DFCU Bank",
        "Diamond Trust Bank",
        "Ecobank",
        "Equity Bank",
        "Fina Bank",
        "Finance Trust Bank",
        "GT Bank",
        "Housing Finance Bank",
        "Imperial Bank",
        "KCB Bank",
        "NC Bank",
        "Opportunity Bank",
        "Post Bank",
        "Stanbic Bank",
        "Standard Chattered Bank",
        "Tropical Bank",
        "(UBA)United Bank Of Africa"
    )
    var transferTypes = arrayOf("EFT", "RTGS")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            MobileMoneyFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}