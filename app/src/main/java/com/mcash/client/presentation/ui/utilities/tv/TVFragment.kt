package com.mcash.client.presentation.ui.utilities.tv

import android.os.Bundle
import android.util.Log
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
import com.mcash.client.core.utils.formatCurrency
import com.mcash.client.databinding.FragmentTVBinding
import com.mcash.domain.model.TvEntity
import com.mcash.client.presentation.MainActivity
import com.mcash.client.presentation.ui.utilities.UtilityViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.runBlocking
import kotlin.collections.ArrayList

@AndroidEntryPoint
class TVFragment : BaseFragment<FragmentTVBinding>(FragmentTVBinding::inflate) {

    val viewModel: UtilityViewModel by viewModels()
    lateinit var packageAdapter: ArrayAdapter<String>
    var tv = ""
    var amount: String = ""
    var selectedId = ""
    var tvType = ""

    var totalAmount = ""
    var phone = ""
    var customerName = ""
    var decoder = ""

    var transactionRef: String = ""
    var shortTransRef = ""
    var transToken = ""
    var tvPackage = ""

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            with(layoutToolbar) {
                btnBack.setOnClickListener { navigateUp() }
                mtvTitle.text = getString(R.string.tv)
            }

            etPhoneNumber.addTextChangedListener(ZeroTextRemover(etPhoneNumber))
            etAmount.addTextChangedListener(CurrencyTextWatcher(etAmount))
            ccp.registerCarrierNumberEditText(etPhoneNumber)


            val list = arrayOf("DStv", "GOTV", "Showmax", "AzamTv", "Zuku", "StarTimes")
            val adapter =
                ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, list)
            tvTypes.setAdapter(adapter)

            val tvList = viewModel.getTVList()
            val dstvList = ArrayList<TvEntity>()
            val dstvStringList = ArrayList<String>()
            val gotvList = ArrayList<TvEntity>()
            val gotvStringList = ArrayList<String>()
            val showmaxList = ArrayList<TvEntity>()
            val showmaxStringList = ArrayList<String>()
            val azamList = ArrayList<TvEntity>()
            val azamStringList = ArrayList<String>()
            val zukuList = ArrayList<TvEntity>()
            val zukuStringList = ArrayList<String>()
            val startimesList = ArrayList<TvEntity>()
            val startimesStringList = ArrayList<String>()

            for (i in tvList) {
                Log.d("List", "$tvList")
                if ((i.paymentitemname?.contains("DStv") == true) || (i.code?.contains("DSTV") == true)) {
                    Log.d("List", "$i")
                    dstvList.add(i)
                    //dstvStringList.add(i.paymentitemname?.replace(("[^A-Za-z]").toRegex(), " ")?:"NULL")

                    dstvStringList.add(displayInformation(i.paymentitemname))
                } else if ((i.paymentitemname?.contains("GOTv") == true) || (i.code?.contains("GOTV") == true)) {
                    gotvList.add(i)
                    gotvStringList.add(displayInformation(i.paymentitemname))
                } else if ((i.paymentitemname?.contains("ShowMax") == true) || (i.code?.contains("SHOW") == true)) {
                    showmaxList.add(i)
                    showmaxStringList.add(displayInformation(i.paymentitemname))
                } else if ((i.paymentitemname?.contains("Azam")) == true || (i.code?.contains("Azam") == true)) {
                    azamList.add(i)
                    azamStringList.add(displayInformation(i.paymentitemname))
                } else if (i.paymentitemname?.contains("Zuku") == true) {
                    zukuList.add(i)
                    zukuStringList.add(displayInformation(i.paymentitemname))
                } else if (i.paymentitemname?.contains("StarTimes") == true || i.code?.contains("STU") == true) {
                    startimesList.add(i)
                    startimesStringList.add(displayInformation(i.paymentitemname))
                }
            }

            tvTypes.doOnTextChanged { _, _, _, _ ->
                tv = tvTypes.text.toString()
                packageName.setText("")
                etAmount.setText("")
                Log.d("Selected", "${tvTypes.text}")
                Log.d("Selected TV", tv)
                if (tv == "DStv") {
                    Log.d("Tvvv", tv)
                    Log.d("DstvList", "$dstvList")
                    packageAdapter = ArrayAdapter(
                        requireContext(),
                        android.R.layout.simple_dropdown_item_1line,
                        dstvStringList
                    )
                    packageName.setAdapter(packageAdapter)
                } else if (tv == "GOTV") {
                    packageAdapter = ArrayAdapter(
                        requireContext(),
                        android.R.layout.simple_dropdown_item_1line,
                        gotvStringList
                    )
                    packageName.setAdapter(packageAdapter)
                } else if (tv == "Showmax") {
                    packageAdapter = ArrayAdapter(
                        requireContext(),
                        android.R.layout.simple_dropdown_item_1line,
                        showmaxStringList
                    )
                    packageName.setAdapter(packageAdapter)
                } else if (tv == "AzamTv") {
                    packageAdapter = ArrayAdapter(
                        requireContext(),
                        android.R.layout.simple_dropdown_item_1line,
                        azamStringList
                    )
                    packageName.setAdapter(packageAdapter)
                } else if (tv == "Zuku") {
                    packageAdapter = ArrayAdapter(
                        requireContext(),
                        android.R.layout.simple_dropdown_item_1line,
                        zukuStringList
                    )
                    packageName.setAdapter(packageAdapter)
                } else if (tv == "StarTimes") {
                    packageAdapter = ArrayAdapter(
                        requireContext(),
                        android.R.layout.simple_dropdown_item_1line,
                        startimesStringList
                    )
                    packageName.setAdapter(packageAdapter)
                }
            }

            packageName.doOnTextChanged { _, _, _, _ ->
                etAmount.setText("")
                tvPackage = packageName.text.toString()

                if (tv == "DStv") {
                    dstvList.forEach {
                        if (displayInformation(it.paymentitemname ?: "NUll") == tvPackage) {
                            amount = it.amount ?: "1"
                            etAmount.setText(amount)

                            tvType = "dstv"
                            selectedId = it.packageId
                        }
                    }

                } else if (tv == "GOTV") {
                    gotvList.forEach {
                        if (displayInformation(it.paymentitemname ?: "NUll") == tvPackage) {
                            amount = it.amount ?: "1"
                            etAmount.setText(amount)

                            tvType = "dstv"
                            selectedId = it.packageId
                        }
                    }
                } else if (tv == "Showmax") {
                    showmaxList.forEach {
                        if (displayInformation(it.paymentitemname ?: "NUll") == tvPackage) {
                            amount = it.amount ?: "1"
                            etAmount.setText(amount)

                            tvType = "dstv"
                            selectedId = it.packageId
                        }
                    }
                } else if (tv == "AzamTv") {
                    azamList.forEach {
                        if (displayInformation(it.paymentitemname ?: "NUll") == tvPackage) {
                            amount = it.amount ?: "1"
                            etAmount.setText(amount)

                            tvType = "azamtv"
                            selectedId = it.packageId
                        }
                    }
                } else if (tv == "Zuku") {
                    zukuList.forEach {
                        if (displayInformation(it.paymentitemname ?: "NUll") == tvPackage) {
                            amount = it.amount ?: "1"
                            etAmount.setText(amount)

                            tvType = "zukutv"
                            selectedId = it.packageId
                        }
                    }
                } else if (tv == "StarTimes") {
                    startimesList.forEach {
                        if (displayInformation(it.paymentitemname ?: "NUll") == tvPackage) {
                            amount = it.amount ?: "1"
                            etAmount.setText(amount)

                            tvType = "startimestv"
                            selectedId = it.packageId
                        }
                    }
                }

            }


            //validate TV Customer State
            with(viewModel) {
                validateTVState.observe(viewLifecycleOwner) {
                    when (it) {
                        is UtilityViewModel.ValidateTVState.Loading -> showProgressDialog()

                        is UtilityViewModel.ValidateTVState.Error -> {
                            hideProgressDialog()
                            showErrorDialog(it.message)
                        }

                        is UtilityViewModel.ValidateTVState.Success -> {
                            hideProgressDialog()

                            editTv.visibility = View.GONE
                            tvLabel.visibility = View.VISIBLE

                            transactionRef = it.data.transactionRef ?: "Empty Ref"
                            shortTransRef = it.data.shortTransactionRef ?: "Short Ref"
                            transToken = it.data.transactionToken ?: "Trans Tok"

                            customerName = it.data.customerName ?: "---"

                            mtvAccountNameValue.text = it.data.customerName
                            mtvDecoderValue.text = it.data.customerId
                            mtvAmountValue.text =
                                "UGX. ${(it.data.amount ?: 0).toDouble().formatCurrency()}"
                            mtvChargeValue.text =
                                "UGX. ${(it.data.surcharge ?: 0).toDouble().formatCurrency()}"
                            mtvTotalValue.text =
                                "UGX. ${(it.data.amount ?: 0).toDouble().formatCurrency()}"
                        }

                        else -> {

                        }
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
                                        navigate(TVFragmentDirections.actionTVFragmentToFragmentHome())
                                        exitTransition
                                    }
                                }
                            )
                        }

                        is UtilityViewModel.ConfirmTVState.Success -> {
                            hideProgressDialog()
                            val activity = requireActivity() as MainActivity

                            activity.showSuccessMessage(
                                "Pay TV",
                                "TV Payment successful with payment reference ${it.data.payment_ref}",  //trans ref
                                1,
                                object : SuccessDialogClickLister {
                                    override fun onDoneClicked() {
                                        navigate(TVFragmentDirections.actionTVFragmentToFragmentHome())
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


            //validate TV customer
            btnValidateTV.setOnClickListener {
                when {
                    tvTypes.text.toString().isEmpty() -> {
                        showAlert(
                            getString(R.string.warning),
                            getString(R.string.select_tv),
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

                    decoderNo.text.toString().isEmpty() -> {
                        showAlert(
                            getString(R.string.warning),
                            getString(R.string.enter_decoder_no),
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

                    else -> {

                        Log.d("tvType", tvType)
                        Log.d("PackageID", selectedId)

                        totalAmount = etAmount.text.toString().replace(",", "")
                        phone = "0${ccp.fullNumber.takeLast(9)}"
                        decoder = decoderNo.text.toString()
                        amount = totalAmount

                        runBlocking {
                            viewModel.validateTVCustomer(
                                tvType,
                                getDeviceID() ?: "",
                                getPhoneModel() ?: "",
                                decoder,
//                                phone,
                                selectedId,
                                totalAmount,
                            )
                        }
                    }

                }
            }

            //confirm TV customer
            btnTV.setOnClickListener {
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
                                        tvType,
                                        getDeviceID() ?: "NULL",
                                        getPhoneModel() ?: "NULL",
                                        decoder,
                                        phone,
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