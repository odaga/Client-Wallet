package com.mcash.client.presentation.ui.utilities.voice

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.viewModels
import com.mcash.client.R
import com.mcash.client.core.utils.PinConfirmClickListener
import com.mcash.client.core.utils.SuccessDialogClickLister
import com.mcash.client.core.base.BaseFragment
import com.mcash.client.core.utils.ToastUtils.showAlert
import com.mcash.client.core.utils.ZeroTextRemover
import com.mcash.client.core.utils.isAirtelNumber
import com.mcash.client.core.utils.isMtnNumber
import com.mcash.domain.model.VoiceEntity
import com.mcash.client.presentation.MainActivity
import com.mcash.client.databinding.FragmentVoiceBinding
import com.mcash.client.presentation.ui.utilities.UtilityViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.runBlocking

@AndroidEntryPoint
class VoiceFragment : BaseFragment<FragmentVoiceBinding>(FragmentVoiceBinding::inflate) {

    val viewModel: UtilityViewModel by viewModels()
    lateinit var packageAdapter: ArrayAdapter<String>
    var phoneNetwork = ""
    var amount = ""
    var account = ""
    var customerName = ""

    var selectedNetwork = ""
    var selectedId = ""

    var transactionRef:String = ""
    var shortTransRef = ""
    var transToken= ""
    var voicePackage= ""

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {

            with(layoutToolbar) {
                btnBack.setOnClickListener { navigateUp() }
                mtvTitle.text = getString(R.string.voice)
            }

            etPhoneNumber.addTextChangedListener(ZeroTextRemover(etPhoneNumber))
            //etAmount.addTextChangedListener(CurrencyTextWatcher(etAmount))
            ccp.registerCarrierNumberEditText(etPhoneNumber)

            val list = arrayOf("Airtel", "MTN")
            val networkAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, list)
            networkType.setAdapter(networkAdapter)

            val voiceBundleList = viewModel.getVoiceBundles()
            val airtelList = ArrayList<VoiceEntity>()
            val airtelStringList = ArrayList<String>()
            val mtnList = ArrayList<VoiceEntity>()
            val mtnStringList = ArrayList<String>()

            for (i in voiceBundleList) {
                if ((i.paymentitemname?.contains("Mtn") == true) || (i.code?.contains("VOI")==true) || (i.code?.contains("RACT")==true)) {
                    mtnList.add(i)
                    mtnStringList.add(displayInformation(i.paymentitemname))
                } else {
                    airtelList.add(i)
                    airtelStringList.add(displayInformation(i.paymentitemname))
                }
            }


            etPhoneNumber.doOnTextChanged { text, start, before, count ->
                phoneNetwork = etPhoneNumber.text.toString()
                packageName.setText("")
                etAmount.setText("")

                if (phoneNetwork.isAirtelNumber()){
                    packageAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, airtelStringList)
                    packageName.setAdapter(packageAdapter)

                } else if (phoneNetwork.isMtnNumber()){
                    packageAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, mtnStringList)
                    packageName.setAdapter(packageAdapter)
                }
            }

            packageName.doOnTextChanged { text, start, before, count ->
                etAmount.setText("")
                voicePackage = packageName.text.toString()

                if (phoneNetwork.isAirtelNumber()) {
                    airtelList.asReversed().forEach {
                        if (displayInformation(it.paymentitemname) == voicePackage) {
                            amount = it.amount ?: "1"
                            etAmount.setText(amount)

                            selectedNetwork = "airtel"
                            selectedId = it.packageId
                        }
                    }

                } else if (phoneNetwork.isMtnNumber()){
                    mtnList.forEach {
                        if (displayInformation(it.paymentitemname) == voicePackage) {
                            amount = it.amount ?: "1"
                            etAmount.text = amount

                            selectedNetwork = "mtn"
                            selectedId = it.packageId
                        }
                    }
                }

            }

            //validate voice state
            with(viewModel) {
                validateVoiceState.observe(viewLifecycleOwner) {
                    when(it) {
                        is UtilityViewModel.ValidateVoiceState.Loading -> showProgressDialog()
                        is UtilityViewModel.ValidateVoiceState.Error -> {
                            hideProgressDialog()
                            showErrorDialog(it.message)
                        }
                        is UtilityViewModel.ValidateVoiceState.Success -> {
                            hideProgressDialog()
                            editVoice.visibility = View.GONE
                            voiceLabel.visibility = View.VISIBLE

                            transactionRef = it.data.transactionRef ?: "Empty Ref"
                            shortTransRef = it.data.shortTransactionRef ?: "Short Ref"
                            transToken = it.data.transactionToken ?: "Trans Tok"

                            customerName = it.data.customerName ?:""

                            mtvAccountNameValue.text = it.data.customerName
                            mtvAccountValue.text = it.data.customerId
                            if (phoneNetwork.isMtnNumber()){
                                mtvAmountValue.text = "UGX. ${(it.data.amount)?.div(100)}"
                                mtvTotalValue.text = "UGX. ${(it.data.amount)?.div(100)}"
                            } else {
                                mtvAmountValue.text = "UGX. ${(it.data.amount)}"
                                mtvTotalValue.text = "UGX. ${(it.data.amount)}"
                            }

                            mtvChargeValue.text = "UGX. 0"

                        }
                        else -> {

                        }
                    }
                }

                confirmVoiceState.observe(viewLifecycleOwner) {
                    when(it) {
                        is UtilityViewModel.ConfirmVoiceState.Loading -> showProgressDialog()
                        is UtilityViewModel.ConfirmVoiceState.Error -> {
                            hideProgressDialog()
                            val activity = requireActivity() as MainActivity

                            activity.showSuccessMessage(
                                "Voice Bundles",
                                "Voice Bundle Purchase transaction has Failed: ${it.message}",
                                0,
                                object : SuccessDialogClickLister {
                                    override fun onDoneClicked() {
                                        navigate(VoiceFragmentDirections.actionVoiceFragmentToFragmentHome())
                                        exitTransition
                                    }
                                }
                            )
                        }
                        is UtilityViewModel.ConfirmVoiceState.Success -> {
                            hideProgressDialog()

                            val activity = requireActivity() as MainActivity

                            activity.showSuccessMessage(
                                "Voice Bundles",
                                "Voice Bundle purchase successful with payment reference ${it.data.payment_ref}",
                                1,
                                object : SuccessDialogClickLister {
                                    override fun onDoneClicked() {
                                        navigate(VoiceFragmentDirections.actionVoiceFragmentToFragmentHome())
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

            //validate voice button
            btnValidateVoice.setOnClickListener {
                amount = etAmount.text.toString()
                account = "0${ccp.fullNumber.takeLast(9)}"
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
                    packageName.text.toString().isEmpty() -> {
                        showAlert(
                            getString(R.string.warning),
                            getString(R.string.select_package),
                            com.mcash.client.core.utils.AlertType.FAILURE
                        )
                    }

                    else -> {
                        runBlocking {
                            viewModel.validateVoiceCustomer(
                                selectedNetwork,
                                getDeviceID()?:"",
                                getPhoneModel()?:"",
                                account,
                                selectedId,
                                amount
                            )
                        }
                    }
                }
            }

            //confirm voice payment
            btnVoice.setOnClickListener {
                val activity = requireActivity() as MainActivity
                activity.pinDialog?.let { dialog ->
                    dialog.showPinConfirmationDialog( object : PinConfirmClickListener {
                        override fun onConfirmed() {

                            if (!viewModel.savedUser.isEmpty) {
                                if (dialog.getPinText != viewModel.savedUser.pin) {
                                    dialog.showError()
                                } else {
                                    dialog.hidePinConfirmationDialog()

                                    viewModel.confirmVoicePayment(
                                        selectedNetwork,
                                        getDeviceID()?:"NULL",
                                        getPhoneModel()?:"NULL",
                                        account,
                                        selectedId,
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
