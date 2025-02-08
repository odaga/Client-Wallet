package com.mcash.client.presentation.ui.utilities.loans

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.mcash.client.R
import com.mcash.client.core.base.BaseFragment
import com.mcash.client.databinding.FragmentLoansBinding
import com.mcash.client.presentation.ui.home.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoansFragment : BaseFragment<FragmentLoansBinding>(FragmentLoansBinding::inflate) {
    private val viewModel: HomeViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            with(layoutToolbar) {
                btnBack.setOnClickListener { navigateUp() }
                mtvTitle.text = getString(R.string.loan_request)
            }

//            etPhoneNumber.addTextChangedListener(ZeroTextRemover(etPhoneNumber))
//            //etAmount.addTextChangedListener(CurrencyTextWatcher(etAmount))
//            ccp.registerCarrierNumberEditText(etPhoneNumber)

            val user = viewModel.savedUser
            etPhoneNumber.setText(user.phone)
            packageName.setText(getString(R.string.bright_bridge))
        }
    }
}