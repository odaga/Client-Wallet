package com.mcash.client.presentation.ui.utilities.umeme

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.viewModels
import com.mcash.client.R
import com.mcash.client.core.utils.CurrencyTextWatcher
import com.mcash.client.core.utils.PinConfirmClickListener
import com.mcash.client.core.utils.SuccessDialogClickLister
import com.mcash.client.core.base.BaseFragment
import com.mcash.client.core.utils.ToastUtils.showAlert
import com.mcash.client.core.utils.ZeroTextRemover
import com.mcash.client.databinding.FragmentUmemeBinding
import com.mcash.client.presentation.MainActivity
import com.mcash.client.presentation.ui.utilities.UtilityViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class UmemeFragment : BaseFragment<FragmentUmemeBinding>(FragmentUmemeBinding::inflate) {

    val viewModel: UtilityViewModel by viewModels()

    var customerAccount = ""
    var customerPhone = ""
    var customerName = ""
    var amount = ""
    var transRef = ""
    var shortTransRef = ""
    var transToken = ""
    var charge = 1
    var total = 1

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {

            with(layoutToolbar) {
                btnBack.setOnClickListener { navigateUp() }
                mtvTitle.text = getString(R.string.umeme)
            }

            etPhoneNumber.addTextChangedListener(ZeroTextRemover(etPhoneNumber))
            ccp.registerCarrierNumberEditText(etPhoneNumber)

            etAmount.addTextChangedListener(CurrencyTextWatcher(etAmount))

            with(viewModel) {
                viewModel.validateUmemeState.observe(viewLifecycleOwner) {
                    when (it) {
                        is UtilityViewModel.ValidateUmemeState.Loading -> showProgressDialog()
                        is UtilityViewModel.ValidateUmemeState.Error -> {
                            hideProgressDialog()
//                            showAlert(getString(R.string.validation_error), it.message, com.mcash.client.core.utils.AlertType.FAILURE)
                            showErrorDialog(it.message)
                        }

                        is UtilityViewModel.ValidateUmemeState.Success -> {
                            hideProgressDialog()
                            clOne.visibility = View.GONE
                            cloned.visibility = View.VISIBLE

                            transRef = it.data.transactionRef ?: "TransRef"
                            shortTransRef = it.data.shortTransactionRef ?: "ShortTransRef"
                            transToken = it.data.transactionToken ?: "TransTok"

                            charge =
                                ((it.data.surcharge ?: 1) / 100) + ((it.data.balance ?: 100) / 100)
                            total = (it.data.amount ?: 1) + charge
                            customerName = it.data.customerName ?: "---"

                            mtvAccountNameValue.text = it.data.customerName ?: "Unavailable"
                            mtvMeterValue.text = it.data.customerId ?: "Unavailable"
                            mtvAmountValue.text = "UGX. ${it.data.amount}"
                            mtvChargeValue.text = "UGX. $charge"
                            mtvTotalValue.text = "UGX. $total"
                        }

                        else -> {}
                    }
                }

                confirmUmemeState.observe(viewLifecycleOwner) {
                    when (it) {
                        is UtilityViewModel.ConfirmUmemeState.Loading -> {
                            showProgressDialog()
                        }

                        is UtilityViewModel.ConfirmUmemeState.Error -> {
                            hideProgressDialog()
                            val activity = requireActivity() as MainActivity

                            activity.showSuccessMessage(
                                "Umeme Purchase",
                                "Umeme Purchase transaction has Failed: ${it.message}",
                                0,
                                object : SuccessDialogClickLister {
                                    override fun onDoneClicked() {
                                        navigate(UmemeFragmentDirections.actionUmemeFragmentToFragmentHome())
                                        exitTransition
                                    }
                                }
                            )
                        }

                        is UtilityViewModel.ConfirmUmemeState.Success -> {
                            hideProgressDialog()
                            val activity = requireActivity() as MainActivity

                            activity.showSuccessMessage(
                                "Umeme Purchase Successful",
                                "Umeme bill payment has been successfully processed",
                                1,
                                object : SuccessDialogClickLister {
                                    override fun onDoneClicked() {
                                        navigate(UmemeFragmentDirections.actionUmemeFragmentToFragmentHome())
                                        exitTransition
                                    }
                                }
                            )
                        }

                        else -> {

                        }
                    }
                }
            }

            val list = listOf<String>("Pay Bill", "Buy Yaka", "New Connection")
            val adapter =
                ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, list)
            umemeReason.setAdapter(adapter)


            umemeReason.doOnTextChanged { text, start, before, count ->
                if (umemeReason.text.toString() == "Pay Bill") {
                    //ilMeternumber.prefixText = "04"
                    etUmemeNumber.hint = "e.g 20123456789"
                } else if (umemeReason.text.toString() == "Buy Yaka") {
                    //ilMeternumber.prefixText = "20"
                    etUmemeNumber.hint = "e.g 04123456789"
                } else if (umemeReason.text.toString() == "New Connection") {
                    //ilMeternumber.prefixText = "E2"
                    etUmemeNumber.hint = "e.g E2013456789"
                }
            }


            btnContinue.setOnClickListener {
                when {
                    etUmemeNumber.text.toString().isEmpty() -> {
                        showAlert(
                            getString(R.string.validation_error),
                            "No Account selected",
                            com.mcash.client.core.utils.AlertType.FAILURE
                        )
                    }

                    etUmemeNumber.text.toString().length <= 2 -> {
                        showAlert(
                            getString(R.string.validation_error),
                            "Invalid Account Number",
                            com.mcash.client.core.utils.AlertType.FAILURE
                        )
                    }

                    etPhoneNumber.text.toString().isEmpty() -> {
                        showAlert(
                            getString(R.string.warning),
                            getString(R.string.phone_number_required),
                            com.mcash.client.core.utils.AlertType.FAILURE
                        )
                    }

                    etAmount.text.toString().isEmpty() -> {
                        showAlert(
                            getString(R.string.warning),
                            getString(R.string.amount_required),
                            com.mcash.client.core.utils.AlertType.FAILURE
                        )
                    }

//                    etAmount.text.toString().replace(",", "").toLong() > viewModel.savedBalance -> {
//                        showAlert(
//                            getString(R.string.warning),
//                            getString(R.string.insufficient_balance),
//                            com.mcash.client.core.utils.AlertType.FAILURE
//                        )
//                    }

                    ccp.fullNumber.length != 12 -> {
                        showAlert(
                            getString(R.string.warning),
                            getString(R.string.invalid_phone_number),
                            com.mcash.client.core.utils.AlertType.FAILURE
                        )
                    }

                    else -> {
                        customerAccount = etUmemeNumber.text.toString()
                        customerPhone = ccp.fullNumber
                        amount = etAmount.text.toString().replace(",", "")

                        viewModel.validateUmemeCustomer(
                            getDeviceID() ?: "NULL",
                            getPhoneModel() ?: "NULL",
                            customerAccount,
                            customerPhone,
                            amount
                        )
                    }
                }
            }

            btnPayUmeme.setOnClickListener {
                val activity = requireActivity() as MainActivity
                activity.pinDialog?.let { dialog ->
                    dialog.showPinConfirmationDialog(object : PinConfirmClickListener {
                        override fun onConfirmed() {

                            if (!viewModel.savedUser.isEmpty) {
                                if (dialog.getPinText != viewModel.savedUser.pin) {
                                    dialog.showError()
                                } else {
                                    dialog.hidePinConfirmationDialog()

                                    viewModel.confirmUmemePayment(
                                        getDeviceID() ?: "NULL",
                                        getPhoneModel() ?: "NULL",
                                        customerAccount,
                                        customerPhone,
                                        amount,
                                        transRef,
                                        shortTransRef,
                                        transToken
                                    )
                                }
                            }
                        }
                    })
                }
            }


        }
    }
}