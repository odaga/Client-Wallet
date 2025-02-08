package com.mcash.client.presentation.ui.fuelSave

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.core.app.ActivityCompat
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.mcash.client.R
import com.mcash.client.core.base.BaseFragment
import com.mcash.client.core.utils.AlertType
import com.mcash.client.core.utils.Constants
import com.mcash.client.core.utils.ToastUtils.showAlert
import com.mcash.client.core.utils.formatCurrency
import com.mcash.client.core.utils.hasPermission
import com.mcash.client.core.utils.requestPermissionWithRationale
import com.mcash.client.databinding.FragmentFuelSaveBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.runBlocking
import timber.log.Timber

@AndroidEntryPoint
class FuelSaveFragment : BaseFragment<FragmentFuelSaveBinding>(FragmentFuelSaveBinding::inflate) {

    private val fuelsSaveViewModel: FuelSaveViewModel by viewModels()
    private val driverViewModel: FuelSaveDriverViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        requireActivity().onBackPressedDispatcher.addCallback(
            requireActivity(),
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (findNavController().currentDestination?.id == R.id.fuelSaveFragment)
                        navigate(FuelSaveFragmentDirections.actionFuelSaveFragmentToFragmentHome())
                }
            })

        // Request Location Permission
        getLocationPermission()

        driverViewModel.getFuelSaveDriver()


        with(binding) {
            with(layoutToolbar) {
                btnBack.setOnClickListener { navigate(FuelSaveFragmentDirections.actionFuelSaveFragmentToFragmentHome()) }
                mtvTitle.text = "Fuel Plus"
            }

            btnFindDriver.setOnClickListener {
                driverViewModel.getFuelSaveDriver()
            }

            btnToDriverOnboarding.setOnClickListener {
                navigate(FuelSaveFragmentDirections.actionFuelSaveFragmentToDriverOnboardingFragment())
            }

            scanStationCodeCard.setOnClickListener {
                navigate(FuelSaveFragmentDirections.actionFuelSaveFragmentToScanFuelStationCodeFragment())
            }

            btnGetStationDetails.setOnClickListener {
                if (fuelStationCodeField.text.toString().isEmpty()) {
                    showAlert(
                        "Incorrect Input",
                        "Fuel Station Code is required",
                        AlertType.INFO,
                        2000
                    )
                } else {
                    // get Station details
                    val bundle = Bundle()
                    bundle.putString(BRANCH_CODE_NAME, fuelStationCodeField.text.toString())
                    navigateWithBundle(
                        R.id.action_fuelSaveFragment_to_fuelStationDetailsFragment,
                        bundle
                    )
                }
            }

            refreshLoyaltyPoints.setOnClickListener {
                val location = passiveProvider(requireContext())
                location?.let { data ->
                    fuelsSaveViewModel.getLoyaltyPoints(
                        getDeviceID(),
                        getPhoneModel(),
                        getDeviceID(),
                        data.longitude.toString(),
                        data.latitude.toString()
                    )
                }
            }

            with(driverViewModel) {

                getFuelSaveDriverState.observe(viewLifecycleOwner) { driverStatus ->

                    when (driverStatus) {

                        is FuelSaveDriverViewModel.GetFuelSaveDriverState.Loading -> {
                            showProgressDialog()
                            driverLookupErrorView.isVisible = false
                            driverNotFoundView.isVisible = false
                        }

                        is FuelSaveDriverViewModel.GetFuelSaveDriverState.Error -> {
                            hideProgressDialog()
                            if (driverStatus.message != DRIVER_NOT_FOUND_MESSAGE) {
                                driverLookupErrorView.isVisible = true
                                driverLookupErrorMessage.text = driverStatus.message
                            } else {
                                driverNotFoundView.isVisible = true
                            }

                        }

                        is FuelSaveDriverViewModel.GetFuelSaveDriverState.Success -> {
                            hideProgressDialog()
                            driverOnboardingView.isVisible = false
                            inputOptionsView.isVisible = true


                            val location = passiveProvider(requireContext())

                            location?.let { data ->
                                fuelsSaveViewModel.getLoyaltyPoints(
                                    getDeviceID(),
                                    getPhoneModel(),
                                    getDeviceID(),
                                    data.longitude.toString(),
                                    data.latitude.toString()
                                )
                            }


                        }

                        else -> Unit
                    }
                }
            }

            with(fuelsSaveViewModel) {
                branchDetailState.observe(viewLifecycleOwner) {
                    when (it) {
                        is FuelSaveViewModel.BranchDetailsState.Initial -> Unit
                        is FuelSaveViewModel.BranchDetailsState.Loading -> {
                            showProgressDialog()
                        }

                        is FuelSaveViewModel.BranchDetailsState.Error -> {
                            hideProgressDialog()
                            showErrorDialog(it.message)
                        }

                        is FuelSaveViewModel.BranchDetailsState.Success -> {
                            hideProgressDialog()
                            val bundle = Bundle()
                            bundle.putSerializable(BRANCH_DETAILS_LABEL, it.data)
                            navigateWithBundle(
                                R.id.action_fuelSaveFragment_to_fuelStationDetailsFragment,
                                bundle
                            )

                        }
                    }
                }

                loyaltyPointsState.observe(viewLifecycleOwner) {
                    when (it) {
                        is FuelSaveViewModel.LoyaltyPointsState.Initial -> Unit
                        is FuelSaveViewModel.LoyaltyPointsState.Error -> {
                            pointsShimmerLoader.stopShimmer()
                            loyaltyPointsText.text = "0.0"
                        }

                        is FuelSaveViewModel.LoyaltyPointsState.Loading -> {
                            pointsShimmerLoader.startShimmer()
                            pointsShimmerLoader.showShimmer(true)

                            loyaltyPointsText.text = "0000"
                        }

                        is FuelSaveViewModel.LoyaltyPointsState.Success -> {
                            pointsShimmerLoader.stopShimmer()
                            pointsShimmerLoader.hideShimmer()
                            loyaltyPointsText.text = it.data.toDouble().formatCurrency()
                        }
                    }
                }
            }
        }
    }


    private fun getLocationPermission() {
        // Accept location permissions to access location services
        if (!getParentActivity().hasPermission(Manifest.permission.ACCESS_FINE_LOCATION)) {
            getParentActivity().requestPermissionWithRationale(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Constants.Permissions.ACCESS_FINE_LOCATION, getString(
                    R.string.location_permission_rationale
                )
            )
            Timber.d("Location permissions denied")
        }
    }

    private fun isLocationPermissionGranted(): Boolean {
        return if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                Constants.Permissions.ACCESS_FINE_LOCATION
            )
            false
        } else {
            true
        }
    }


    companion object {
        const val BRANCH_CODE_NAME = "BRANCH_CODE"
        const val BRANCH_DETAILS_LABEL = "BRANCH_DETAILS"
        const val DRIVER_NOT_FOUND_MESSAGE = "Driver not found"
    }
}