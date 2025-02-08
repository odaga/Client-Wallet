package com.mcash.client.presentation.ui.topup

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.viewModels
import com.mcash.client.R
import com.mcash.client.core.utils.CurrencyTextWatcher
import com.mcash.client.core.utils.PinConfirmClickListener
import com.mcash.client.core.utils.SuccessDialogClickLister
import com.mcash.client.core.base.BaseFragment
import com.mcash.client.core.utils.ConfirmationClickLister
import com.mcash.client.core.utils.ToastUtils.showAlert
import com.mcash.client.core.utils.ZeroTextRemover
import com.mcash.client.core.utils.formatCurrency
import com.mcash.client.core.utils.isAirtelNumber
import com.mcash.client.core.utils.isMtnNumber
import com.mcash.client.databinding.FragmentTopupBinding
import com.mcash.client.presentation.MainActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TopupFragment : BaseFragment<FragmentTopupBinding>(FragmentTopupBinding::inflate) {

    private val viewModel: TopupViewModel by viewModels()

    var transactionToken: String = ""
    var transactionRef = ""
    var amount: String = ""

    var customerName = ""
    var customerNumber = ""

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            with(layoutToolbar) {
                btnBack.setOnClickListener { navigateUp() }
                mtvTitle.text = getString(R.string.topup)
            }

            etPhoneNumber.addTextChangedListener(ZeroTextRemover(etPhoneNumber))
            ccp.registerCarrierNumberEditText(etPhoneNumber)
            etAmount.addTextChangedListener(CurrencyTextWatcher(etAmount))

            etPhoneNumber.doOnTextChanged { text, _, _, count ->
                mtvPhoneError.isVisible = count < 1

                if (text.toString().isNotEmpty()) {
                    if (text.toString().length > 1) {
                        when {
                            text.toString().isMtnNumber() -> {
                                llIspLogo.visibility = View.VISIBLE
                                civIspLogo.setImageResource(R.drawable.mtn_logo)
                            }

                            text.toString().isAirtelNumber() -> {
                                llIspLogo.visibility = View.VISIBLE
                                civIspLogo.setImageResource(R.drawable.airtel_logo)
                            }

                            else -> {
                                llIspLogo.visibility = View.GONE
                            }
                        }

                    } else {
                        llIspLogo.visibility = View.GONE
                    }
                }
            }

            etAmount.doOnTextChanged { text, _, _, count ->
                mtvMoneyError.isVisible = count < 1

                if (text.toString().isNotEmpty()) {
                    btnValidateTopUp.text = "TopUp UGX ${text.toString().trim()}"
                } else {
                    btnValidateTopUp.text = getString(R.string.topup)
                }
            }

            viewModel.validateState.observe(viewLifecycleOwner) {
                when (it) {
                    is ValidateState.Loading -> showProgressDialog()
                    is ValidateState.Error -> {
                        hideProgressDialog()
                        showErrorDialog(it.message)
                    }

                    is ValidateState.Success -> {
                        hideProgressDialog()
                        phoneLay.visibility = View.GONE
                        amountLay.visibility = View.VISIBLE

                        customerName = it.data.name ?: "---"
                        customerNumber = "0${ccp.fullNumber.takeLast(9)}"

                        transactionToken = it.data.transactionToken ?: ""
                        transactionRef = it.data.transactionRef ?: ""
                        amount = etAmount.text.toString().replace(",", "")

                        mtvAccountNameValue.text = it.data.name
                        mtvAccountValue.text = customerNumber
                        mtvAmountValue.text = "UGX. ${amount.toInt().toDouble().formatCurrency()}"
                        mtvChargeValue.text = "UGX. 0"
                        mtvTotalValue.text = "UGX. ${amount.toInt().toDouble().formatCurrency()}"
                    }

                    else -> {

                    }
                }
            }


            viewModel.depositState.observe(viewLifecycleOwner) {
                when (it) {
                    is DepositState.Loading -> showProgressDialog()

                    is DepositState.Error -> {
                        hideProgressDialog()
                        val activity = requireActivity() as MainActivity

                        activity.showSuccessMessage(
                            "Wallet TopUp Bundles",
                            "Wallet TopUp transaction has Failed: ${it.message}",
                            0,
                            object : SuccessDialogClickLister {
                                override fun onDoneClicked() {
                                    navigateUp()
                                    exitTransition
                                }
                            }
                        )
                    }

                    is DepositState.Success -> {
                        hideProgressDialog()
                        val activity = requireActivity() as MainActivity

                        activity.showAwaitConfirmation(
                            "Top up",
                            "If you don't receive the USSD prompt within 60 seconds please retry",
                            object : ConfirmationClickLister {
                                override fun onRetry() {
                                    val phone = "0${ccp.fullNumber.takeLast(9)}"
                                    viewModel.depositToWallet(
                                        getDeviceID() ?: "",
                                        getPhoneModel() ?: "",
                                        phone,
                                        etAmount.text.toString().replace(",", ""),
                                        transactionRef,
                                        transactionToken
                                    )
                                }

                                override fun onDismiss() {
                                    navigateUp()
                                }

                            }
                        )

                    }

                    else -> {}
                }
            }

            btnValidateTopUp.setOnClickListener {
                when {
                    etPhoneNumber.text.toString().isEmpty() -> {
                        mtvPhoneError.text = getString(R.string.phone_number_required)
                        mtvPhoneError.isVisible = true
                    }

                    ccp.fullNumber.length != 12 -> {
                        mtvPhoneError.text = getString(R.string.invalid_phone_number)
                        mtvPhoneError.isVisible = true
                    }
//                    !etPhoneNumber.text.toString().isMtnNumber() -> {
//                        mtvPhoneError.text = getString(R.string.only_mtn_numbers)
//                        mtvPhoneError.isVisible = true
//                    }

                    !etPhoneNumber.text.toString()
                        .isAirtelNumber() && !etPhoneNumber.text.toString().isMtnNumber() -> {
                        mtvPhoneError.text = getString(R.string.only_airtel_mtn_numbers)
                        mtvPhoneError.isVisible = true
                    }

                    etAmount.text.toString().isEmpty() -> {
                        mtvMoneyError.text = getString(R.string.amunt_required)
                        mtvMoneyError.isVisible = true
                    }

//                    etAmount.text.toString().replace(",", "").toLong() > viewModel.savedBalance -> {
//                        mtvMoneyError.text = getString(R.string.insufficient_balance)
//                        mtvMoneyError.isVisible = true
//                    }

                    else -> {

                        val phone = "0${ccp.fullNumber.takeLast(9)}"
                        viewModel.validateNumber(
                            getDeviceID() ?: "",
                            getPhoneModel() ?: "",
                            phone,
                            etAmount.text.toString().replace(",", "")
                        )
                    }
                }
            }

            btnDeposit.setOnClickListener {
                val phone = "0${ccp.fullNumber.takeLast(9)}"
                val activity = requireActivity() as MainActivity

                activity.pinDialog?.let { dialog ->
                    dialog.showPinConfirmationDialog(object : PinConfirmClickListener {
                        override fun onConfirmed() {
                            if (!viewModel.savedUser.isEmpty) {
                                if (dialog.getPinText != viewModel.savedUser.pin) {
                                    dialog.showError()
                                } else {
                                    dialog.hidePinConfirmationDialog()
                                    viewModel.depositToWallet(
                                        getDeviceID() ?: "",
                                        getPhoneModel() ?: "",
                                        phone,
                                        etAmount.text.toString().replace(",", ""),
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
}