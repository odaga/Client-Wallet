package com.mcash.client.presentation.ui.utilities.nwsc

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import androidx.fragment.app.viewModels
import com.mcash.client.R
import com.mcash.client.core.utils.CurrencyTextWatcher
import com.mcash.client.core.utils.PinConfirmClickListener
import com.mcash.client.core.base.BaseFragment
import com.mcash.client.core.utils.SuccessDialogClickLister
import com.mcash.client.core.utils.ToastUtils.showAlert
import com.mcash.client.core.utils.ZeroTextRemover
import com.mcash.client.core.utils.formatCurrency
import com.mcash.client.databinding.FragmentNwscBinding
import com.mcash.client.presentation.MainActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NwscFragment : BaseFragment<FragmentNwscBinding>(FragmentNwscBinding::inflate) {
    private val viewModel: NwscViewModel by viewModels()
    var customerAccount = ""
    var customerPhone = ""
    var area = ""
    var customerName = ""
    var outstandingBalance = ""
    var amount = ""
    var transactionToken = ""
    var transactionRef = ""


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {

            with(layoutToolbar) {
                btnBack.setOnClickListener { navigateUp() }
                mtvTitle.text = getString(R.string.nwsc)
            }

            val adapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                viewModel.savedAreas
            )
            acvAreas.setAdapter(adapter)

            etPhoneNumber.addTextChangedListener(ZeroTextRemover(etPhoneNumber))
            etAmount.addTextChangedListener(CurrencyTextWatcher(etAmount))
            ccp.registerCarrierNumberEditText(etPhoneNumber)

            with(viewModel) {
                this.validateNwscCustomerState.observe(viewLifecycleOwner) {
                    when (it) {
                        is ValidateNwscCustomerState.Loading -> showProgressDialog()
                        is ValidateNwscCustomerState.Error -> {
                            hideProgressDialog()
                            showErrorDialog(it.message)
//                            showAlert(
//                                getString(R.string.error),
//                                it.message,
//                                com.mcash.client.core.utils.AlertType.FAILURE, 9000
//                            )
                        }

                        is ValidateNwscCustomerState.Success -> {
                            hideProgressDialog()
                            clOne.visibility = View.GONE
                            clTwo.visibility = View.VISIBLE

                            customerName = it.data.customer_name ?: ""
                            outstandingBalance = it.data.outstanding_balance.toString()

                            mtvAccountNameValue.text = customerName
                            mtvMeterValue.text = customerAccount
                            propertyRef.text = it.data.property_ref ?: ""
                            mtvBalanceValue.text = outstandingBalance
                            mtvAreaValue.text = area
                        }

                        else -> Unit
                    }
                }

                this.validateNwscState.observe(viewLifecycleOwner) {
                    when (it) {
                        is ValidateNwscState.Loading -> showProgressDialog()
                        is ValidateNwscState.Error -> {
                            hideProgressDialog()
                            showErrorDialog(it.message)
                        }

                        is ValidateNwscState.Success -> {
                            hideProgressDialog()

                            clTwo.visibility = View.GONE
                            nwscLabel.visibility = View.VISIBLE

                            transactionToken = it.data.transactionToken ?: ""
                            transactionRef = it.data.transaction_ref ?: ""

                            nwscAccountNameValue.text = customerName
                            mtvAccountValue.text = customerAccount
                            mtvPackageValue.text = area
                            mtvAmountValue.text =
                                "UGX. ${(it.data.amount_payable ?: 0).toDouble().formatCurrency()}"
                            mtvChargeValue.text =
                                "UGX. ${(it.data.charge ?: 0).toDouble().formatCurrency()}"
                            mtvTotalValue.text =
                                "UGX. ${(it.data.amount ?: 0).toDouble().formatCurrency()}"
                        }

                        else -> {

                        }
                    }
                }

                this.confirmNwscState.observe(viewLifecycleOwner) {
                    when (it) {
                        is ConfirmNwscState.Loading -> showProgressDialog()
                        is ConfirmNwscState.Error -> {
                            hideProgressDialog()
                            showErrorDialog(it.message)
                        }

                        is ConfirmNwscState.Success -> {
                            hideProgressDialog()

                            val message =
                                "You have successfully paid UGX $amount to NWSC"
                            val activity = requireActivity() as MainActivity
                            activity.showSuccessMessage(
                                "NWSC Payment",
                                message,
                                1,
                                object : SuccessDialogClickLister {
                                    override fun onDoneClicked() {
                                        navigateUp()
                                    }
                                }
                            )

                        }

                        else -> {

                        }
                    }
                }

            }

            btnContinue.setOnClickListener {
                when {
                    acvAreas.text.toString().isEmpty() -> {
                        showAlert(
                            getString(R.string.validation_error),
                            "No area selected",
                            com.mcash.client.core.utils.AlertType.FAILURE
                        )
                    }

                    etMeterNumber.text.toString().isEmpty() -> {
                        showAlert(
                            getString(R.string.validation_error),
                            "Enter meter number",
                            com.mcash.client.core.utils.AlertType.FAILURE
                        )
                    }

                    etPhoneNumber.text.toString().isEmpty() -> {
                        showAlert(
                            getString(R.string.validation_error),
                            "Enter Phone number",
                            com.mcash.client.core.utils.AlertType.FAILURE
                        )
                    }

                    etPhoneNumber.text.toString().length != 10 -> {
                        showAlert(
                            getString(R.string.validation_error),
                            "Phone number Invalid",
                            com.mcash.client.core.utils.AlertType.FAILURE
                        )
                    }

                    else -> {
                        customerPhone = "0${ccp.fullNumber.takeLast(9)}"
                        customerAccount = etMeterNumber.text.toString()
                        area = acvAreas.text.toString()

                        viewModel.validateNwscCustomer(
                            getDeviceID() ?: "",
                            getPhoneModel() ?: "",
                            customerAccount,
                            customerPhone,
                            area
                        )
                    }
                }
            }

            btnPayNwsc.setOnClickListener {
                when {
                    etAmount.text.toString().isEmpty() -> {
                        showAlert(
                            getString(R.string.validation_error),
                            "Enter valid Amount",
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
                    else -> {
                        amount = etAmount.text.toString().replace(",", "")
                        viewModel.validateNwscPayment(
                            getDeviceID() ?: "",
                            getPhoneModel() ?: "",
                            customerAccount,
                            customerPhone,
                            area,
                            customerName,
                            outstandingBalance,
                            amount
                        )
                    }
                }
            }

            btnNwsc.setOnClickListener {
                val activity = requireActivity() as MainActivity
                activity.pinDialog?.let { dialog ->
                    dialog.showPinConfirmationDialog(object : PinConfirmClickListener {
                        override fun onConfirmed() {
                            if (!viewModel.savedUser.isEmpty) {
                                if (dialog.getPinText != viewModel.savedUser.pin) {
                                    dialog.showError()
                                } else {
                                    dialog.hidePinConfirmationDialog()
                                    viewModel.confirmNwscPayment(
                                        getDeviceID() ?: "NULL",
                                        getPhoneModel() ?: "NULL",
                                        customerAccount,
                                        customerPhone,
                                        area,
                                        customerName,
                                        outstandingBalance,
                                        amount,
                                        transactionToken,
                                        transactionRef
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