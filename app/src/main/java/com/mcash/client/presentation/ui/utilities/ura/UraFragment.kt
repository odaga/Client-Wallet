package com.mcash.client.presentation.ui.utilities.ura

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.mcash.client.R
import com.mcash.client.core.utils.CurrencyTextWatcher
import com.mcash.client.core.utils.PinConfirmClickListener
import com.mcash.client.core.utils.SuccessDialogClickLister
import com.mcash.client.core.base.BaseFragment
import com.mcash.client.core.utils.ToastUtils.showAlert
import com.mcash.client.core.utils.ZeroTextRemover
import com.mcash.client.core.utils.formatCurrency
import com.mcash.client.databinding.FragmentUraBinding
import com.mcash.client.presentation.MainActivity
import com.mcash.client.presentation.ui.utilities.UtilityViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class UraFragment : BaseFragment<FragmentUraBinding>(FragmentUraBinding::inflate) {

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
                mtvTitle.text = getString(R.string.ura)
            }

            etPhoneNumber.addTextChangedListener(ZeroTextRemover(etPhoneNumber))
            ccp.registerCarrierNumberEditText(etPhoneNumber)
            etAmount.addTextChangedListener(CurrencyTextWatcher(etAmount))

            with(viewModel) {
                viewModel.validateUraState.observe(viewLifecycleOwner) {
                    when (it) {
                        is UtilityViewModel.ValidateUraState.Loading -> showProgressDialog()
                        is UtilityViewModel.ValidateUraState.Error -> {
                            hideProgressDialog()
//                            showAlert(getString(R.string.validation_error), it.message, com.mcash.client.core.utils.AlertType.FAILURE)
                            showErrorDialog(it.message)
                        }

                        is UtilityViewModel.ValidateUraState.Success -> {
                            hideProgressDialog()
                            editUra.visibility = View.GONE
                            uraLabel.visibility = View.VISIBLE
                            if (amount.toInt() != it.data.amount) {
                                showAlert(
                                    getString(R.string.validation_error),
                                    "Incorrect Amount",
                                    com.mcash.client.core.utils.AlertType.FAILURE
                                )
                            } else {

                                customerName = it.data.customerName ?: "---"

                                transRef = it.data.transactionRef ?: "TransRef"
                                shortTransRef = it.data.shortTransactionRef ?: "ShortTransRef"
                                transToken = it.data.transactionToken ?: "TransTok"

                                charge = ((it.data.surcharge
                                    ?: 1) / 100) //+ ((it.data.balance ?: 100) / 100)
                                total = ((it.data.amount ?: 1) / 100) + charge


                                mtvAccountNameValue.text = it.data.customerName ?: "---"
                                mtvAccountValue.text = customerPhone
                                mtvPrnValue.text = it.data.customerId ?: "Unavailable"
                                mtvAmountValue.text =
                                    "UGX. ${amount.toInt().toDouble().formatCurrency()}"
                                mtvChargeValue.text = "UGX. ${charge.toDouble().formatCurrency()}"
                                mtvTotalValue.text = "UGX. ${total.toDouble().formatCurrency()}"
                            }
                        }

                        else -> {}
                    }
                }

                confirmUraState.observe(viewLifecycleOwner) {
                    when (it) {
                        is UtilityViewModel.ConfirmUraState.Loading -> {
                            showProgressDialog()
                        }

                        is UtilityViewModel.ConfirmUraState.Error -> {
                            hideProgressDialog()
                            val activity = requireActivity() as MainActivity

                            activity.showSuccessMessage(
                                "URA Payment",
                                "URA Payment transaction has Failed: ${it.message}",
                                0,
                                object : SuccessDialogClickLister {
                                    override fun onDoneClicked() {
                                        navigate(UraFragmentDirections.actionUraFragmentToFragmentHome())
                                        exitTransition
                                    }
                                }
                            )
                        }

                        is UtilityViewModel.ConfirmUraState.Success -> {
                            hideProgressDialog()
                            val activity = requireActivity() as MainActivity

                            activity.showSuccessMessage(
                                "URA Payment",
                                "URA Payment successful with payment reference ${it.data.payment_ref}",
                                1,
                                object : SuccessDialogClickLister {
                                    override fun onDoneClicked() {
                                        navigate(UraFragmentDirections.actionUraFragmentToFragmentHome())
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


            btnValidateUra.setOnClickListener {
                when {
                    prn.text.toString().isEmpty() -> {
                        showAlert(
                            getString(R.string.validation_error),
                            "Enter valid Payment Registration Number",
                            com.mcash.client.core.utils.AlertType.FAILURE
                        )
                    }

                    prn.text.toString().length <= 2 -> {
                        showAlert(
                            getString(R.string.validation_error),
                            "Invalid Payment Registration Number",
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
                        customerAccount = prn.text.toString()
                        customerPhone = ccp.fullNumber
                        amount = etAmount.text.toString().replace(",", "")

                        viewModel.validateUraCustomer(
                            getDeviceID() ?: "NULL",
                            getPhoneModel() ?: "NULL",
                            customerAccount,
                            customerPhone,
                            amount
                        )
                    }
                }
            }


            btnUra.setOnClickListener {
                val activity = requireActivity() as MainActivity
                activity.pinDialog?.let { dialog ->
                    dialog.showPinConfirmationDialog(object : PinConfirmClickListener {
                        override fun onConfirmed() {

                            if (!viewModel.savedUser.isEmpty) {
                                if (dialog.getPinText != viewModel.savedUser.pin) {
                                    dialog.showError()
                                } else {
                                    dialog.hidePinConfirmationDialog()

                                    viewModel.confirmUraPayment(
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