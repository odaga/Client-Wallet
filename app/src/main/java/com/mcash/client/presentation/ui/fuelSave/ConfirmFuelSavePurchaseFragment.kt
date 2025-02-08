package com.mcash.client.presentation.ui.fuelSave

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation.findNavController
import com.mcash.client.R
import com.mcash.client.core.base.BaseFragment
import com.mcash.client.core.utils.PinConfirmClickListener
import com.mcash.client.core.utils.removeFocus
import com.mcash.client.databinding.FragmentConfirmFuelSavePurchaseBinding
import com.mcash.client.databinding.FragmentFuelSaveBinding
import com.mcash.client.presentation.MainActivity
import com.mcash.client.presentation.ui.fuelSave.FuelStationDetailsFragment.Companion
import com.mcash.domain.model.Branch
import com.mcash.domain.model.FuelSaveTransaction
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ConfirmFuelSavePurchaseFragment :
    BaseFragment<FragmentConfirmFuelSavePurchaseBinding>(FragmentConfirmFuelSavePurchaseBinding::inflate) {

    private val fuelSaveViewModel: FuelSaveViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requireActivity().onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (purchaseIsSuccessful) {
                        navigate(ConfirmFuelSavePurchaseFragmentDirections.actionConfirmFuelSavePurchaseFragmentToFragmentHome())
                    }
                }
            })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (arguments != null) {
            branchCode = arguments?.getString("branch_code").toString()

            buildConfirmationView(requireArguments())

        } else {
            navigateUp()
            showErrorDialog("There was problem getting branch details")
        }

        with(binding) {

            buttonCompleteTxn.setOnClickListener {
                navigate(ConfirmFuelSavePurchaseFragmentDirections.actionConfirmFuelSavePurchaseFragmentToFragmentHome())
            }

            buttonConfirmPurchase.setOnClickListener {
                requireActivity().removeFocus()

                val location = passiveProvider(requireContext())
                val activity = requireActivity() as MainActivity
                location?.let { locationData ->
                    activity.pinDialog?.let { dialog ->

                        dialog.showPinConfirmationDialog(object : PinConfirmClickListener {
                            override fun onConfirmed() {
                                dialog.hidePinConfirmationDialog()
                                fuelSaveViewModel.confirmFuelSavePurchase(
                                    getDeviceID(),
                                    getPhoneModel(),
                                    getDeviceID(),
                                    longitude = locationData.longitude.toString(),
                                    latitude = locationData.latitude.toString(),
                                    pin = dialog.getPinText,
                                    branchCode = branchCode,
                                    amount = purchaseAmount,
                                    fuelTypeId = fuelTypeId,
                                    indicator = indicator
                                )
                            }
                        })
                    }
                }
            }

            fuelSaveViewModel.confirmFuelSavePurchaseState.observe(viewLifecycleOwner) { purchaseState ->
                when (purchaseState) {

                    is FuelSaveViewModel.ConfirmFuelSavePurchaseState.Initial -> Unit
                    is FuelSaveViewModel.ConfirmFuelSavePurchaseState.Loading -> {
                        showProgressDialog()
                    }

                    is FuelSaveViewModel.ConfirmFuelSavePurchaseState.Error -> {
                        hideProgressDialog()
                        showErrorDialog(purchaseState.message)

                    }

                    is FuelSaveViewModel.ConfirmFuelSavePurchaseState.Success -> {
                        hideProgressDialog()
                        purchaseIsSuccessful = true
                        buildSuccessView(purchaseState.data)
                        println("==== purchase success state: ${purchaseState.data}")
                    }
                }
            }
        }
    }

    private fun buildSuccessView(data: FuelSaveTransaction) {
        with(data) {
            with(binding) {
                confirmationView.isVisible = false
                successView.isVisible = true
                successDriverCode.text = data.driverCode
                commissionStatus.text = data.commissionStatus.toString()
                transactionRef.text = data.transactionRef.toString()
                successFuelType.text = data.fuelTypeId
                successPricePerLiter.text = data.pricePerLiter
                transactionDate.text = getCurrentDateTime().first
                transactionTime.text = getCurrentDateTime().second
            }
        }
    }

    private fun buildConfirmationView(arguments: Bundle) {
        with(binding) {
            with(arguments) {
                binding.layoutToolbar.mtvTitle.text = "Confirm Fuel Purchase"
                validationBranchCode.text = getString("BRANCH_CODE")
                branchCode = getString("BRANCH_CODE").toString()

                validationDriverCode.text = getString("DRIVER_CODE")
                driverCode = getString("DRIVER_CODE").toString()

                validationFuelTypeId.text = getString("FUEL_TYPE_ID")
                fuelTypeId = getString("FUEL_TYPE_ID").toString()

                validationAmount.text = "uGX. ${getString("AMOUNT")}"
                purchaseAmount = getString("AMOUNT").toString()

                validationPricePerLiter.text = getString("PRICE_PER_LITER")
                pricePerLiter = getString("PRICE_PER_LITER").toString()

                validationStationId.text = getString("STATION_ID")
                stationId = getString("STATION_ID").toString()

                validationAssociationId.text = getString("ASSOCIATION_ID")
                associationId = getString("ASSOCIATION_ID").toString()

                indicator = getString("INDICATOR").toString()

                validationLiterEquivalent.text = getString("LITER_EQUIVALENT").toString()
            }
        }
    }

    companion object {
        private var purchaseIsSuccessful = false
        private var stationId = ""
        private var associationId = ""
        private var branchCode = ""
        private var driverCode = ""
        private var fuelTypeId = ""
        private var purchaseAmount = ""
        private var pricePerLiter = ""
        private var indicator = ""
    }


}