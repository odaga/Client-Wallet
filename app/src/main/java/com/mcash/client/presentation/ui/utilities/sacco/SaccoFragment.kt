package com.mcash.client.presentation.ui.utilities.sacco

import android.os.Bundle
import android.view.View
import com.mcash.client.R
import com.mcash.client.core.base.BaseFragment
import com.mcash.client.databinding.FragmentSaccoBinding


class SaccoFragment : BaseFragment<FragmentSaccoBinding>(FragmentSaccoBinding::inflate) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            with(layoutToolbar) {
                btnBack.setOnClickListener { navigateUp() }
                mtvTitle.text = getString(R.string.digia)
            }
            btnValidate.setOnClickListener {
                nestedScrollView.visibility = View.GONE
                amountLay.visibility = View.VISIBLE
            }
        }

    }
}