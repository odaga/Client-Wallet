package com.mcash.client.presentation.ui.transfer

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.mcash.client.R
import com.mcash.client.databinding.FragmentMobileMoneyBinding
import com.mcash.client.core.base.BaseFragment
import com.mcash.client.core.utils.CurrencyTextWatcher
import com.mcash.client.core.utils.PinConfirmClickListener
import com.mcash.client.core.utils.SuccessDialogClickLister
import com.mcash.client.core.utils.ToastUtils.showAlert
import com.mcash.client.core.utils.ZeroTextRemover
import com.mcash.client.core.utils.formatCurrency
import com.mcash.client.presentation.MainActivity
import com.mcash.client.presentation.ui.home.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.sql.Time


@AndroidEntryPoint
class MobileMoneyFragment :
    BaseFragment<FragmentMobileMoneyBinding>(FragmentMobileMoneyBinding::inflate) {

    private val viewModel: HomeViewModel by viewModels()
    private val transferViewModel: TransferViewModel by viewModels()
    var amount: String = ""
    var phone: String = ""
    var transactionToken = ""
    var transactionRef = ""

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {

            etPhoneNumber.addTextChangedListener(ZeroTextRemover(etPhoneNumber))
            etAmount.addTextChangedListener(CurrencyTextWatcher(etAmount))

            val user = viewModel.savedUser
            phone = user.phone
            etPhoneNumber.setText(phone.takeLast(9))

            with(transferViewModel) {
                validateMMState.observe(viewLifecycleOwner) {
                    when (it) {
                        is ValidateMMState.Loading -> showProgressDialog()
                        is ValidateMMState.Error -> {
                            hideProgressDialog()
                            showErrorDialog(it.message)
                        }

                        is ValidateMMState.Success -> {
                            hideProgressDialog()
                            phoneLay.visibility = View.GONE
                            amountLay.visibility = View.VISIBLE


                            val customerName = user.fullName
                            val customerNumber = user.phone

                            transactionToken = it.data.transactionToken ?: ""
                            transactionRef = it.data.transactionRef ?: ""
                            val totalAmount = "${amount.toInt() + (it.data.charge ?: 0)}"

                            mtvAccountNameValue.text = customerName
                            mtvAccountValue.text = customerNumber
                            mtvAmountValue.text =
                                "UGX. ${amount.toInt().toDouble().formatCurrency()}"
                            mtvChargeValue.text =
                                "UGX. ${(it.data.charge ?: 0).toDouble().formatCurrency()}"
                            mtvTotalValue.text =
                                "UGX. ${totalAmount.toInt().toDouble().formatCurrency()}"

                            // Setting values
                            recipientName = customerName
                            recipientNumber = customerNumber
                            amount = "UGX. ${amount.toInt().toDouble().formatCurrency()}"
                            charge = "UGX. ${(it.data.charge ?: 0).toDouble().formatCurrency()}"
                            totalAmountToSend =
                                "UGX. ${totalAmount.toInt().toDouble().formatCurrency()}"
                            transactionReference = it.data.transactionRef ?: "-"

                            val narration =
                                "You have successfully transferred UGX $amount to Your mobile money account"
//

                            val bundle = Bundle()
                            bundle.putString("CUSTOMER_NAME", recipientName)
                            bundle.putString("CUSTOMER_NUMBER", recipientNumber)
                            bundle.putString("AMOUNT", amount)
                            bundle.putString("NARRATION", narration)
                            bundle.putString("CHARGE", charge)
                            bundle.putString("TOTAL_AMOUNT", totalAmountToSend)
                            bundle.putString("TRANSACTION_REFERENCE", transactionReference)
                            bundle.putString("TIME", "")

//                            navigate(MobileMoneyFragmentDirections.actionFragmentMobileMoneyToTransactionSummaryFragment())

                            navigateWithBundle(R.id.action_fragment_mobile_money_to_transactionSummaryFragment, bundle)
                        }

                        else -> {}
                    }
                }

                confirmMMState.observe(viewLifecycleOwner) {
                    when (it) {
                        is ConfirmMMState.Loading -> showProgressDialog()
                        is ConfirmMMState.Error -> {
                            hideProgressDialog()

                            val activity = requireActivity() as MainActivity
                            activity.showSuccessMessage(
                                "Mobile Money Transfer",
                                "Wallet Transfer transaction has Failed: ${it.message}",
                                0,
                                object : SuccessDialogClickLister {
                                    override fun onDoneClicked() {
                                        navigateUp()
                                        exitTransition
                                    }
                                }
                            )

                        }

                        is ConfirmMMState.Success -> {
                            hideProgressDialog()
                            val message =
                                "You have successfully transferred UGX $amount to Your mobile money account"
//                            val activity = requireActivity() as MainActivity
//                            activity.showSuccessMessage(
//                                "Mobile Money Transfer",
//                                message,
//                                1,
//                                object : SuccessDialogClickLister {
//                                    override fun onDoneClicked() {
//                                        navigateUp()
//                                    }
//                                }
//                            )
                            // Go to Transaction Summary Page
                            navigate(MobileMoneyFragmentDirections.actionFragmentMobileMoneyToTransactionSummaryFragment())


                        }

                        else -> Unit
                    }
                }
            }



            btnTransfer.setOnClickListener {
                when {
                    etAmount.text.toString().isEmpty() -> {
                        showAlert(
                            getString(R.string.warning),
                            getString(R.string.amount_required),
                            com.mcash.client.core.utils.AlertType.FAILURE
                        )
                    }

                    else -> {
                        amount = etAmount.text.toString().replace(",", "")

                        transferViewModel.validateTransferToMM(
                            getDeviceID() ?: "",
                            getPhoneModel() ?: "",
                            amount
                        )

                    }
                }
            }

            btnConfirmTransfer.setOnClickListener {
                val activity = requireActivity() as MainActivity

                activity.pinDialog?.let { dialog ->
                    dialog.showPinConfirmationDialog(object : PinConfirmClickListener {
                        override fun onConfirmed() {
                            if (!viewModel.savedUser.isEmpty) {
                                if (dialog.getPinText != viewModel.savedUser.pin) {
                                    dialog.showError()
                                } else {
                                    dialog.hidePinConfirmationDialog()
                                    transferViewModel.confirmTransferToMM(
                                        getDeviceID() ?: "",
                                        getPhoneModel() ?: "",
                                        amount,
                                        transactionRef,
                                        transactionToken
                                    )
                                }
                            }
                        }
                    })
                }
            }
        }
    }

    companion object {
        private var recipientName = ""
        private var recipientNumber = ""
        private var naration = ""
        private var amount = ""
        private var charge = ""
        private var totalAmountToSend = ""
        private var timeStamp = ""
        private var transactionReference = ""
    }
}