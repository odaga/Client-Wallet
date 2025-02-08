package com.mcash.client.presentation.ui.transfer

import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.view.View
import androidx.fragment.app.viewModels
import com.google.gson.Gson
import com.mcash.client.R
import com.mcash.client.core.utils.CurrencyTextWatcher
import com.mcash.client.core.utils.SuccessDialogClickLister
import com.mcash.client.databinding.FragmentMcashWalletBinding
import com.mcash.client.core.base.BaseFragment
import com.mcash.client.core.utils.Constants.Permissions.PICK_BENEFICIARY
import com.mcash.client.core.utils.Constants.Permissions.PICK_CONTACT
import com.mcash.client.core.utils.Constants.Permissions.READ_CONTACT_PERMISSION
import com.mcash.client.core.utils.Constants.Permissions.READ_PHONE_CONTACTS
import com.mcash.client.core.utils.Constants.SELECTED_BENEFICIARY
import com.mcash.client.core.utils.Constants.SELECTED_CONTACT
import com.mcash.client.core.utils.PinConfirmClickListener
import com.mcash.client.core.utils.ToastUtils.showAlert
import com.mcash.client.core.utils.ZeroTextRemover
import com.mcash.client.core.utils.formatContact
import com.mcash.client.core.utils.formatCurrency
import com.mcash.client.core.utils.hasPermission
import com.mcash.client.core.utils.removeFocus
import com.mcash.client.core.utils.requestPermissionWithRationale
import com.mcash.domain.model.Beneficiary
import com.mcash.domain.model.Contact
import com.mcash.client.presentation.MainActivity
import com.mcash.client.presentation.ui.beneficiaries.BeneficiaryActivity
import com.mcash.client.presentation.ui.contacts.ContactsActivity
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import java.util.*

@AndroidEntryPoint
class McashWalletFragment :
    BaseFragment<FragmentMcashWalletBinding>(FragmentMcashWalletBinding::inflate) {
    private var selectedContact: Contact? = null

    private val viewModel: TransferViewModel by viewModels()
    var transactionToken:String = ""
    var transactionRef = ""
    var amount:String = ""
    var charge = ""
    var totalAmount = ""

    var customerName = ""
    var customerNumber = ""

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {

            with(layoutToolbar) {
                btnBack.setOnClickListener { navigateUp() }
                mtvTitle.text = getString(R.string.mcash_wallet)
            }

            etPhoneNumber.addTextChangedListener(ZeroTextRemover(etPhoneNumber))
            etAmount.addTextChangedListener(CurrencyTextWatcher(etAmount))
            ccp.registerCarrierNumberEditText(etPhoneNumber)

            llFavorite.setOnClickListener {
                val contactIntent = Intent(requireContext(), BeneficiaryActivity::class.java)
                startActivityForResult(contactIntent, PICK_BENEFICIARY)
            }

            llPhonebook.setOnClickListener {
                if (requireContext().hasPermission(Manifest.permission.READ_CONTACTS)) {
                    pickContacts()
                } else {
                    requireActivity().requestPermissionWithRationale(
                        Manifest.permission.READ_CONTACTS, READ_CONTACT_PERMISSION, getString(
                            R.string.contact_permission_rationale
                        )
                    )
                }
            }

            viewModel.validateState.observe(viewLifecycleOwner) {
                when(it) {
                    is ValidateState.Loading -> showProgressDialog()
                    is ValidateState.Error -> {
                        hideProgressDialog()
                        showAlert(
                            getString(R.string.error),
                            it.message,
                            com.mcash.client.core.utils.AlertType.FAILURE
                        )
                    }
                    is ValidateState.Success -> {
                        hideProgressDialog()

                        phoneLay.visibility = View.GONE
                        amountLay.visibility = View.VISIBLE


                        customerName = it.data.name ?: "---"
                        customerNumber = "0${ccp.fullNumber.takeLast(9)}"

                        transactionToken = it.data.transactionToken?: ""
                        transactionRef = it.data.transactionRef ?: ""
                        amount = etAmount.text.toString().replace(",", "")
                        charge = "${it.data.charge?:0}"
                        totalAmount = "${amount.toInt() + (it.data.charge?:0)}"

                        mtvAccountNameValue.text = customerName
                        mtvAccountValue.text = customerNumber
                        mtvAmountValue.text = "UGX. ${amount.toInt().toDouble().formatCurrency()}"
                        mtvChargeValue.text = "UGX. ${(it.data.charge?:0).toDouble().formatCurrency()}"
                        mtvTotalValue.text = "UGX. ${totalAmount.toInt().toDouble().formatCurrency()}"
                    }
                    else -> {}
                }
            }

            viewModel.transferState.observe(viewLifecycleOwner) {
                when (it) {
                    is TransferState.Loading -> showProgressDialog()

                    is TransferState.Error -> {
                        hideProgressDialog()
                        val activity = requireActivity() as MainActivity

                        activity.showSuccessMessage(
                            "Wallet Transfer",
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

                    is TransferState.Success -> {
                        hideProgressDialog()
                        val message =
                            "You have successfully transferred UGX $amount to $customerNumber"
                        val activity = requireActivity() as MainActivity
                        activity.showSuccessMessage(
                            "Wallet Transfer Successful",
                            message,
                            1,
                            object : SuccessDialogClickLister {
                                override fun onDoneClicked() {
                                    navigateUp()
                                }
                            }
                        )
                    }
                    else -> {}
                }
            }

            btnTransfer.setOnClickListener {
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
                        val phone = "0${ccp.fullNumber.takeLast(9)}"

                        if (cbSaveBeneficiary.isChecked) {
                            if (selectedContact != null) {
                                selectedContact?.let {
                                    val beneficiary = Beneficiary().apply {
                                        id = it.id
                                        name = it.name
                                        numbers = it.numbers
                                    }

                                    viewModel.saveBeneficiary(beneficiary)
                                }
                            } else {
                                val phn = etPhoneNumber.text.toString()

                                val beneficiary = Beneficiary().apply {
                                    id = UUID.randomUUID().toString()
                                    name = phn
                                    numbers = listOf(phn)
                                }

                                viewModel.saveBeneficiary(beneficiary)
                            }
                        }

                        viewModel.validateTransferToWallet(
                            getDeviceID()?:"",
                            getPhoneModel()?:"",
                            phone,
                            etAmount.text.toString().replace(",", "")
                        )

//                        val activity = requireActivity() as MainActivity
//                        activity.pinDialog?.let { dialog ->
//                            dialog.showPinConfirmationDialog(object : PinConfirmClickListener {
//                                override fun onConfirmed() {
//                                    if (!viewModel.savedUser.isEmpty) {
//                                        if (dialog.getPinText != viewModel.savedUser.pin) {
//                                            dialog.showError()
//                                        } else {
//                                            dialog.hidePinConfirmationDialog()
//                                            viewModel.validateTransferToWallet(
//                                                getDeviceID()?:"",
//                                                getPhoneModel()?:"",
//                                                phone,
//                                                etAmount.text.toString().replace(",", "")
//                                            )
//                                        }
//                                    }
//                                }
//                            })
//                        }

                    }

                }
            }

            btnConfirmTransfer.setOnClickListener {
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
                                    viewModel.confirmTransferToWallet(
                                        getDeviceID()?:"",
                                        getPhoneModel()?:"",
                                        phone,
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

    private fun pickContacts() {
        // Pick from phone book

//        val contactPickerIntent = Intent(
//            Intent.ACTION_PICK,
//            ContactsContract.CommonDataKinds.Phone.CONTENT_URI
//        )
//        startActivityForResult(contactPickerIntent, READ_CONTACTS)

        // Pick from custom phone book
        val contactIntent = Intent(requireContext(), ContactsActivity::class.java)
        startActivityForResult(contactIntent, PICK_CONTACT)
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == READ_CONTACT_PERMISSION && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//            viewModel.fetchContacts()
            pickContacts()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == RESULT_OK) {
            when (requestCode) {
                READ_PHONE_CONTACTS -> {
                    var cursor: Cursor? = null
                    try {
                        var phoneNo: String? = null
                        var name: String? = null
                        val uri: Uri? = data?.data
                        cursor =
                            requireActivity().contentResolver.query(uri!!, null, null, null, null)
                        cursor?.moveToFirst()
                        val phoneIndex: Int? =
                            cursor?.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
                        val nameIndex: Int? =
                            cursor?.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
                        phoneNo = cursor?.getString(phoneIndex!!)
                        name = cursor?.getString(nameIndex!!)

                        binding.etPhoneNumber.setText(phoneNo?.formatContact())

                        Timber.d("Name and Contact number is $name, ${phoneNo?.formatContact()}")
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                PICK_CONTACT -> {
                    val contact =
                        Gson().fromJson(data?.getStringExtra(SELECTED_CONTACT), Contact::class.java)
                    selectedContact = contact

                    contact?.let {
                        binding.cbSaveBeneficiary.visibility = View.VISIBLE
                        if (it.numbers.isNotEmpty()) {
                            binding.etPhoneNumber.setText(it.numbers[0].formatContact())
                        }
                    }
                    Timber.d("Selected contact: $contact")
                }

                PICK_BENEFICIARY -> {
                    val beneficiary = Gson().fromJson(
                        data?.getStringExtra(SELECTED_BENEFICIARY),
                        Beneficiary::class.java
                    )

                    beneficiary?.let {
                        binding.cbSaveBeneficiary.visibility = View.GONE
                        binding.cbSaveBeneficiary.isChecked = false

                        if (it.numbers.isNotEmpty()) {
                            binding.etPhoneNumber.setText(it.numbers[0].formatContact())
                        }
                    }
                    Timber.d("Selected beneficiary: $beneficiary")
                }
            }
        } else {
            Timber.e("Not able to pick contact")
        }
    }
}