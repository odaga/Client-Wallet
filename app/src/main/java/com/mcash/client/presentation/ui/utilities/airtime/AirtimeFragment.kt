package com.mcash.client.presentation.ui.utilities.airtime

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
import com.mcash.client.core.utils.removeFocus
import com.mcash.client.databinding.FragmentAirtimeBinding
import com.mcash.client.presentation.MainActivity
import com.mcash.client.presentation.ui.utilities.UtilityViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.runBlocking


@AndroidEntryPoint
class AirtimeFragment : BaseFragment<FragmentAirtimeBinding>(FragmentAirtimeBinding::inflate) {

    private val viewModel: UtilityViewModel by viewModels()

    var amount: String = ""
    var phone: String = ""
    var transactionRef: String = ""
    var shortTransRef = ""
    var transToken = ""
    var customerName = ""

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {

            with(layoutToolbar) {
                btnBack.setOnClickListener { navigateUp() }
                mtvTitle.text = getString(R.string.airtime)
            }

            etPhoneNumber.addTextChangedListener(ZeroTextRemover(etPhoneNumber))
            etAmount.addTextChangedListener(CurrencyTextWatcher(etAmount))
            ccp.registerCarrierNumberEditText(etPhoneNumber)

            viewModel.validateAirtimeState.observe(viewLifecycleOwner) {
                when (it) {
                    is UtilityViewModel.ValidateAirtimeState.Loading -> showProgressDialog()
                    is UtilityViewModel.ValidateAirtimeState.Error -> {
                        hideProgressDialog()
                        showErrorDialog(it.message)
                    }

                    is UtilityViewModel.ValidateAirtimeState.Success -> {
                        hideProgressDialog()
                        airtimeEntry.visibility = View.GONE
                        airtimeLabel.visibility = View.VISIBLE
                        mtvAccountNameValue.text = it.data.customerName
                        mtvAccountValue.text = it.data.customerId
                        mtvPackageValue.text = it.data.biller
                        mtvAmountValue.text =
                            "UGX. ${(it.data.amount ?: 0).toDouble().formatCurrency()}"
                        mtvChargeValue.text =
                            "UGX. ${(it.data.surcharge ?: 0).toDouble().formatCurrency()}"
                        mtvTotalValue.text =
                            "UGX. ${(it.data.amount ?: 0).toDouble().formatCurrency()}"

                        customerName = it.data.customerName ?: "---"
                        phone = it.data.customerId ?: "Mcash"
                        amount = it.data.amount.toString()
                        transactionRef = it.data.transactionRef ?: "Empty Ref"
                        shortTransRef = it.data.shortTransactionRef ?: "Short Ref"
                        transToken = it.data.transactionToken ?: "Trans Tok"
                    }

                    else -> {

                    }
                }
            }

            viewModel.buyAirtimeState.observe(viewLifecycleOwner) {
                when (it) {
                    is UtilityViewModel.BuyAirtimeState.Loading -> showProgressDialog()

                    is UtilityViewModel.BuyAirtimeState.Error -> {
                        hideProgressDialog()
                        val activity = requireActivity() as MainActivity

                        activity.showSuccessMessage(
                            "Airtime Purchase",
                            "Airtime Purchase transaction has Failed: ${it.message}",
                            0,
                            object : SuccessDialogClickLister {
                                override fun onDoneClicked() {
                                    navigate(AirtimeFragmentDirections.actionAirtimeFragmentToFragmentHome())
                                    exitTransition
                                }
                            }
                        )
                    }

                    is UtilityViewModel.BuyAirtimeState.Success -> {
                        hideProgressDialog()
                        val activity = requireActivity() as MainActivity

                        activity.showSuccessMessage(
                            "Airtime Purchase",
                            "Airtime Purchase successful with payment reference ${it.data.payment_ref}",
                            1,
                            object : SuccessDialogClickLister {
                                override fun onDoneClicked() {
                                    navigate(AirtimeFragmentDirections.actionAirtimeFragmentToFragmentHome())
                                    exitTransition
                                }
                            }
                        )
                    }

                    else -> {}
                }
            }

            //validate airtime
            btnValidateAirtime.setOnClickListener {
                requireActivity().removeFocus()

                when {
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
                        amount = etAmount.text.toString().replace(",", "")
                        phone = "0${ccp.fullNumber.takeLast(9)}"

                        runBlocking {
                            viewModel.validateAirtime(
                                getDeviceID() ?: "NULL",
                                getPhoneModel() ?: "NULL",
                                phone,
                                amount
                            )
                        }

                    }

                }
            }

            //Confirm airtime purchase
            btnAirtime.setOnClickListener {
                requireActivity().removeFocus()

                val activity = requireActivity() as MainActivity
                activity.pinDialog?.let { dialog ->
                    dialog.showPinConfirmationDialog(object : PinConfirmClickListener {
                        override fun onConfirmed() {
                            if (!viewModel.savedUser.isEmpty) {
                                if (dialog.getPinText != viewModel.savedUser.pin) {
                                    dialog.showError()
                                } else {
                                    dialog.hidePinConfirmationDialog()
                                    viewModel.buyAirtime(
                                        getDeviceID() ?: "NULL",
                                        getPhoneModel() ?: "NULL",
                                        phone,
                                        amount,
                                        transactionRef,
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