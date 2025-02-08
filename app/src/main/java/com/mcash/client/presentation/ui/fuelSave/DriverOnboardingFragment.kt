package com.mcash.client.presentation.ui.fuelSave

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.mcash.client.R
import com.mcash.client.core.base.BaseFragment
import com.mcash.client.core.utils.AlertType
import com.mcash.client.core.utils.ToastUtils.showAlert
import com.mcash.client.databinding.FragmentDriverOnboardingBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DriverOnboardingFragment :
    BaseFragment<FragmentDriverOnboardingBinding>(FragmentDriverOnboardingBinding::inflate) {

    private val driverViewModel: FuelSaveDriverViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (arguments != null) {
            with(binding) {
                ninField.setText(arguments?.getString("ID_CARD_DRIVER_NIN_VALUE").toString())
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(
            requireActivity(),
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    navigate(DriverOnboardingFragmentDirections.actionDriverOnboardingFragmentToFuelSaveFragment())
                }
            })

        with(binding) {
            with(layoutToolbar) {
                btnBack.setOnClickListener { navigate(FuelSaveFragmentDirections.actionFuelSaveFragmentToFragmentHome()) }
                mtvTitle.text = "Driver Onboarding"
            }



            scanIdCodeCard.setOnClickListener {
                navigate(DriverOnboardingFragmentDirections.actionDriverOnboardingFragmentToIdScanFragment())
            }

            btnRegisterDriver.setOnClickListener {
                if (ninField.text.toString().isEmpty()) {
                    showInputValidationErrorMessage("NIN")
                } else {
                    driverViewModel.registerFuelSaveDriver(
                        ninField.text.toString(),

                        )
                }
            }


            driverViewModel.registerFuelSaveDriverState.observe(viewLifecycleOwner) {

                when (it) {
                    is FuelSaveDriverViewModel.RegisterFuelSaveDriverState.Loading -> {
                        showProgressDialog()
                    }

                    is FuelSaveDriverViewModel.RegisterFuelSaveDriverState.Error -> {
                        hideProgressDialog()
                        showErrorDialog(it.message)
                    }

                    is FuelSaveDriverViewModel.RegisterFuelSaveDriverState.Success -> {
                        hideProgressDialog()
                        if (findNavController().currentDestination?.id == R.id.driverOnboardingFragment)
                            navigate(DriverOnboardingFragmentDirections.actionDriverOnboardingFragmentToFuelSaveFragment())

                    }

                    else -> Unit
                }
            }
        }
    }

    private fun showInputValidationErrorMessage(fieldName: String) {
        showAlert(
            "Required Input",
            "Please enter your $fieldName to proceed",
            AlertType.INFO,
            2000
        )
    }

    companion object {
        private var driverNin = ""
    }
}