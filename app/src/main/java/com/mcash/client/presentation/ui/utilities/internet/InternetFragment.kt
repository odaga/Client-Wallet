package com.mcash.client.presentation.ui.utilities.internet

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
import com.mcash.client.databinding.FragmentInternetBinding
import com.mcash.client.presentation.MainActivity
import com.mcash.client.presentation.ui.utilities.UtilityViewModel
import com.mcash.domain.model.DataEntity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.runBlocking

@AndroidEntryPoint
class InternetFragment : BaseFragment<FragmentInternetBinding>(FragmentInternetBinding::inflate) {

    val viewModel: UtilityViewModel by viewModels()
    lateinit var packageAdapter: ArrayAdapter<String>
    var phoneNetwork = ""
    var amount = ""
    var account = ""

    var selectedNetwork = ""
    var selectedId = ""

    var transactionRef: String = ""
    var shortTransRef = ""
    var transToken = ""
    var customerName = ""
    var dataPackage = ""


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {

            with(layoutToolbar) {
                btnBack.setOnClickListener { navigateUp() }
                mtvTitle.text = getString(R.string.data)
            }

            etPhoneNumber.addTextChangedListener(ZeroTextRemover(etPhoneNumber))
            ccp.registerCarrierNumberEditText(etPhoneNumber)

            getDataPackages()

            val list = arrayOf("Airtel", "MTN")
            val networkAdapter =
                ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_dropdown_item_1line,
                    dataProviders
                )
            networkType.setAdapter(networkAdapter)


//            val airtelList = viewModel.getDataBundles("airtel")
//            val airtelStringList = ArrayList<String>()
//            val mtnList = viewModel.getDataBundles("mtn")
//            val mtnStringList = ArrayList<String>()
//
//
//
//            for (i in airtelList) {
//                airtelStringList.add(displayInformation(i.paymentitemname))
//            }
//
//            for (i in mtnList) {
//                mtnStringList.add(displayInformation(i.paymentitemname))
//            }
//
            etPhoneNumber.doOnTextChanged { text, start, before, count ->
                phoneNetwork = etPhoneNumber.text.toString()
                packageName.setText("")
                etAmount.setText("")

                if (phoneNetwork.isAirtelNumber()) {
                    packageAdapter = ArrayAdapter(
                        requireContext(),
                        android.R.layout.simple_dropdown_item_1line,
                        airtelStringList
                    )
                    packageName.setAdapter(packageAdapter)

                } else if (phoneNetwork.isMtnNumber()) {
                    packageAdapter = ArrayAdapter(
                        requireContext(),
                        android.R.layout.simple_dropdown_item_1line,
                        mtnStringList
                    )
                    packageName.setAdapter(packageAdapter)
                }
            }

            packageName.doOnTextChanged { text, start, before, count ->
                etAmount.setText("")
                dataPackage = packageName.text.toString()

                if (phoneNetwork.isAirtelNumber()) {
                    airtelPackages.asReversed().forEach {
                        if (displayInformation(it.paymentitemname) == dataPackage) {
                            amount = it.amount ?: "1"
                            etAmount.setText(amount)

                            selectedNetwork = "airtel"
                            selectedId = it.packageId
                        }
                    }

                } else if (phoneNetwork.isMtnNumber()) {
                    mtnPackages.forEach {
                        if (displayInformation(it.paymentitemname) == dataPackage) {
                            amount = it.amount ?: "1"
                            etAmount.setText(amount)

                            selectedNetwork = "mtn"
                            selectedId = it.packageId
                        }
                    }
                }

            }


            //validate data state
            with(viewModel) {

                // Observe the data packages list state
                datePackagesState.observe(viewLifecycleOwner) {
                    when (it) {

                        is UtilityViewModel.DatePackagesState.Loading -> {
                            showProgressDialog()
                        }

                        is UtilityViewModel.DatePackagesState.Error -> {
                            hideProgressDialog()
                            showErrorDialog(it.message)
                        }

                        is UtilityViewModel.DatePackagesState.Success -> {
                            hideProgressDialog()

                            // Load the UI with the fresh data packages data

                            // Get Airtel Packages
                            getDataBundlesByProvider("airtel").forEach { airtelPackage ->
                                airtelPackages.add(airtelPackage)
                            }

                            airtelPackages.forEach { a ->
                                airtelStringList.add(displayInformation(a.paymentitemname))
                            }

                            // Get MTN Packages
                            getDataBundlesByProvider("airtel").forEach { mtnPackage ->
                                mtnPackages.add(mtnPackage)
                            }
                            mtnPackages.forEach { mtn ->
                                mtnStringList.add(displayInformation(mtn.paymentitemname))
                            }


                        }

                        else -> Unit
                    }
                }

                validateDataState.observe(viewLifecycleOwner) {
                    when (it) {
                        is UtilityViewModel.ValidateDataState.Loading -> showProgressDialog()
                        is UtilityViewModel.ValidateDataState.Error -> {
                            hideProgressDialog()
                            showErrorDialog(it.message)
                        }

                        is UtilityViewModel.ValidateDataState.Success -> {
                            hideProgressDialog()
                            editVoice.visibility = View.GONE
                            voiceLabel.visibility = View.VISIBLE

                            transactionRef = it.data.transactionRef ?: "Empty Ref"
                            shortTransRef = it.data.shortTransactionRef ?: "Short Ref"
                            transToken = it.data.transactionToken ?: "Trans Tok"
                            customerName = it.data.customerName ?: "---"

                            mtvAccountNameValue.text = it.data.customerName
                            mtvAccountValue.text = it.data.customerId
                            mtvPackageValue.text = packageName.text.toString()
                            if (phoneNetwork.isMtnNumber()) {
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

                confirmDataState.observe(viewLifecycleOwner) {
                    when (it) {
                        is UtilityViewModel.ConfirmDataState.Loading -> showProgressDialog()
                        is UtilityViewModel.ConfirmDataState.Error -> {
                            hideProgressDialog()
                            val activity = requireActivity() as MainActivity

                            activity.showSuccessMessage(
                                "Data Bundles",
                                "Data Bundles Purchase transaction has Failed: ${it.message}",
                                0,
                                object : SuccessDialogClickLister {
                                    override fun onDoneClicked() {
                                        navigate(InternetFragmentDirections.actionInternetFragmentToFragmentHome())
                                        exitTransition
                                    }
                                }
                            )
                        }

                        is UtilityViewModel.ConfirmDataState.Success -> {
                            hideProgressDialog()
                            val activity = requireActivity() as MainActivity

                            activity.showSuccessMessage(
                                "Data Bundles",
                                "Data Purchase successful with payment reference ${it.data.payment_ref}",
                                1,
                                object : SuccessDialogClickLister {
                                    override fun onDoneClicked() {
                                        navigate(InternetFragmentDirections.actionInternetFragmentToFragmentHome())
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

            //validate data button
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
                            viewModel.validateDataCustomer(
                                selectedNetwork,
                                getDeviceID() ?: "",
                                getPhoneModel() ?: "",
                                account,
                                selectedId,
                                amount
                            )
                        }
                    }
                }
            }

            btnData.setOnClickListener {
                val activity = requireActivity() as MainActivity
                activity.pinDialog?.let { dialog ->
                    dialog.showPinConfirmationDialog(object : PinConfirmClickListener {
                        override fun onConfirmed() {

                            if (!viewModel.savedUser.isEmpty) {
                                if (dialog.getPinText != viewModel.savedUser.pin) {
                                    dialog.showError()
                                } else {
                                    dialog.hidePinConfirmationDialog()

                                    viewModel.confirmDataPayment(
                                        selectedNetwork,
                                        getDeviceID() ?: "NULL",
                                        getPhoneModel() ?: "NULL",
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

    private fun getDataPackages() {
        runBlocking {
            viewModel.fetchDataBundles(dataProviders)
        }
    }

    companion object {
        private val dataProviders = listOf("airtel", "mtn")
        private val airtelPackages = ArrayList<DataEntity>()
        private val mtnPackages = ArrayList<DataEntity>()
        private val airtelStringList = ArrayList<String>()
        private val mtnStringList = ArrayList<String>()
    }
}