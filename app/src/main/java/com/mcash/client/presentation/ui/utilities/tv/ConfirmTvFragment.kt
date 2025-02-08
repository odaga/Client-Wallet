package com.mcash.client.presentation.ui.utilities.tv

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import com.mcash.client.R
import com.mcash.client.core.base.BaseFragment
import com.mcash.client.core.utils.PinConfirmClickListener
import com.mcash.client.core.utils.SuccessDialogClickLister
import com.mcash.client.core.utils.formatCurrency
import com.mcash.client.databinding.FragmentConfirmTvBinding
import com.mcash.client.presentation.MainActivity
import com.mcash.client.presentation.ui.utilities.UtilityViewModel
import com.mcash.domain.model.ConfirmUtilityEntity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ConfirmTvFragment :
    BaseFragment<FragmentConfirmTvBinding>(FragmentConfirmTvBinding::inflate) {

    val viewModel: UtilityViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val bundle = arguments
        if (bundle != null) {
            buildDetailsView(bundle)
        }

        with(viewModel) {

            binding.buttonCompleteTxn.setOnClickListener {
                navigateClearBackstack(R.id.action_confirmTvFragment_to_fragment_home, null)
            }

            binding.btnConfirmTvPayment.setOnClickListener {
                val activity = requireActivity() as MainActivity
                activity.pinDialog?.let { dialog ->
                    dialog.showPinConfirmationDialog(object : PinConfirmClickListener {
                        override fun onConfirmed() {

                            if (!viewModel.savedUser.isEmpty) {
                                if (dialog.getPinText != viewModel.savedUser.pin) {
                                    dialog.showError()
                                } else {
                                    dialog.hidePinConfirmationDialog()

                                    viewModel.confirmTVPayment(
                                        tvProvider,
                                        getDeviceID() ?: "NULL",
                                        getPhoneModel() ?: "NULL",
                                        decoderNumber,
                                        dialog.getPinText,
                                        selectedPackageId,
                                        transactionAmount,
                                        transactionReference,
                                        shortTransactionRef,
                                        transactionToken
                                    )
                                }
                            }
                        }
                    })
                }
            }


            viewModel.confirmTVState.observe(viewLifecycleOwner) {
                when (it) {
                    is UtilityViewModel.ConfirmTVState.Loading -> showProgressDialog()
                    is UtilityViewModel.ConfirmTVState.Error -> {
                        hideProgressDialog()
                        val activity = requireActivity() as MainActivity

                        activity.showSuccessMessage(
                            "Pay TV",
                            "Pay TV transaction has Failed: ${it.message}",
                            0,
                            object : SuccessDialogClickLister {
                                override fun onDoneClicked() {
                                    exitTransition
                                }
                            }
                        )
                    }

                    is UtilityViewModel.ConfirmTVState.Success -> {
                        hideProgressDialog()
                        buildTransactionSummaryView(it.data)
//                        val activity = requireActivity() as MainActivity
//                        activity.showSuccessMessage(
//                            "Pay TV",
//                            "TV Payment successful with payment reference ${it.data.payment_ref}",  //trans ref
//                            1,
//                            object : SuccessDialogClickLister {
//                                override fun onDoneClicked() {
//                                    exitTransition
//                                }
//                            }
//                        )
                    }

                    else -> {

                    }
                }
            }
        }


    }

    private fun buildDetailsView(bundle: Bundle) {
        with(binding) {

            with(bundle) {
                tvProvider = getString("PROVIDER").toString()
                decoderNumber = getString("DECODER_NUMBER").toString()
                transactionAmount = getString("AMOUNT").toString()
                transactionCharge = getString("CHARGE").toString()
                selectedPackageId = getString("SELECTED_PACKAGE_ID").toString()
                transactionReference = getString("TRANSACTION_REF").toString()
                shortTransactionRef = getString("SHORT_TRANSACTION_REF").toString()
                transactionToken = getString("TRANSACTION_TOKEN").toString()

                amountPayable =
                    transactionAmount.toDouble().plus(transactionCharge.toDouble()).toString()

                mtvAccountNameValue.text = getString("CUSTOMER_NAME")
                mtvDecoderValue.text = decoderNumber
                mtvAmountValue.text = "UGX. ${(transactionAmount.toDouble().formatCurrency())}"
                mtvTotalValue.text = "UGX. ${(amountPayable.toDouble().formatCurrency())}"
                mtvChargeValue.text = "UGX. ${(transactionCharge.toDouble().formatCurrency())}"

                setTvProviderIcon(tvProvider)

            }
        }
    }

    private fun setTvProviderIcon(provider: String) {
        with(binding)
        {
            when (provider) {
                "dstv" -> {
                    tvProviderIcon.setImageResource(R.drawable.dstv)
                }

                "gotv" -> {
                    tvProviderIcon.setImageResource(R.drawable.gotv)

                }

                "showmax" -> {
                    tvProviderIcon.setImageResource(R.drawable.showmax)
                }

                "azamtv" -> {
                    tvProviderIcon.setImageResource(R.drawable.azam)
                }

                "zukutv" -> {
                    tvProviderIcon.setImageResource(R.drawable.zuku)
                }

                "startimestv" -> {
                    tvProviderIcon.setImageResource(R.drawable.startimes)
                }

                else -> return tvProviderIcon.setImageResource(R.drawable.ic_tv)
            }
        }
    }

    private fun buildTransactionSummaryView(data: ConfirmUtilityEntity) {
        with(binding) {
            confirmationView.isVisible = false
            transactionSummaryView.isVisible

            narrationField.text = "TV Payment successful with payment reference ${data.payment_ref}"
            amount.text = "UGX ${amountPayable.toDouble().formatCurrency()}"
            txnRef.text = data.transaction_ref
            txnCharge.text = "UGX ${transactionCharge.toDouble().formatCurrency()}"
            transactionDate.text = ""
            transactionTime.text = ""
        }
    }


    companion object {
        private var tvProvider: String = ""
        private var decoderNumber: String = ""
        private var selectedPackageId: String = ""
        private var transactionAmount: String = ""
        private var amountPayable: String = ""
        private var transactionCharge: String = ""
        private var transactionReference: String = ""
        private var shortTransactionRef: String = ""
        private var transactionToken: String = ""
    }


}