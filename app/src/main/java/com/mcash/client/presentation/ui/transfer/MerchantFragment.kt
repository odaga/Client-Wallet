package com.mcash.client.presentation.ui.transfer

import android.os.Bundle
import android.view.View
import com.mcash.client.R
import com.mcash.client.core.base.BaseFragment
import com.mcash.client.databinding.FragmentMerchantBinding


class MerchantFragment : BaseFragment<FragmentMerchantBinding>(FragmentMerchantBinding::inflate) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            with(layoutToolbar) {
                btnBack.setOnClickListener { navigateUp() }
                mtvTitle.text = getString(R.string.merchant)
            }
        }
    }
}