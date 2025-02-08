package com.mcash.client.presentation.ui.fuelSave

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mcash.client.R
import com.mcash.client.core.base.BaseFragment
import com.mcash.client.core.utils.AlertType
import com.mcash.client.core.utils.ToastUtils.showAlert
import com.mcash.client.core.utils.formatCurrency
import com.mcash.client.databinding.FragmentFuelStationDetailsBinding
import com.mcash.client.databinding.FuelTypeItemBinding
import com.mcash.domain.model.Branch
import com.mcash.domain.model.FuelType
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.runBlocking


@AndroidEntryPoint
class FuelStationDetailsFragment :
    BaseFragment<FragmentFuelStationDetailsBinding>(FragmentFuelStationDetailsBinding::inflate) {

    private val fuelSaveViewModel: FuelSaveViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requireActivity().onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (findNavController().currentDestination?.id == R.id.fuelStationDetailsFragment)
                        navigate(FuelStationDetailsFragmentDirections.actionFuelStationDetailsFragmentToFuelSaveFragment())
                }
            })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {
            with(layoutToolbar) {
                btnBack.setOnClickListener {
                    if (findNavController().currentDestination?.id == R.id.fuelStationDetailsFragment)
                        navigate(FuelStationDetailsFragmentDirections.actionFuelStationDetailsFragmentToFuelSaveFragment())
                }
                mtvTitle.text = SCREEN_TITLE
            }

            if (arguments != null) {
                branchCode = arguments?.getString(FuelSaveFragment.BRANCH_CODE_NAME).toString()
                fuelSaveViewModel.getBranchDetails(branchCode)
            } else {
                showErrorDialog("There was problem getting branch details")
            }

            buttonValidateFuelSave.setOnClickListener {
                if (amountInput.text.toString().isEmpty()) {
                    showAlert(
                        "Purchase Amount is required",
                        "Please enter Purchase Amount to proceed",
                        AlertType.INFO,
                        2000
                    )
                } else if (fuelTypeId.isEmpty()) {
                    showAlert(
                        "Fuel Type is required",
                        "Please select  Fuel Type to proceed",
                        AlertType.INFO,
                        2000
                    )

                } else {
                    validatePurchase()
                }
            }

            with(fuelSaveViewModel) {

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
                            buildBranchDetailsView(it.data)
                        }
                    }
                }

                validateFuelSavePurchaseState.observe(viewLifecycleOwner) {
                    when (it) {
                        is FuelSaveViewModel.ValidateFuelSavePurchaseState.Initial -> {}
                        is FuelSaveViewModel.ValidateFuelSavePurchaseState.Loading -> {
                            showProgressDialog()
                        }

                        is FuelSaveViewModel.ValidateFuelSavePurchaseState.Error -> {
                            hideProgressDialog()
                            showErrorDialog(it.message)

                        }

                        is FuelSaveViewModel.ValidateFuelSavePurchaseState.Success -> {
                            hideProgressDialog()
                            val bundle = Bundle()
                            println("=== validation Data: ${it.data}")
                            with(it.data) {
                                bundle.putString("DRIVER_CODE", driverCode)
                                bundle.putString("BRANCH_CODE", branchCode)
                                bundle.putString("FUEL_TYPE_ID", fuelTypeId)
                                bundle.putString("AMOUNT", amount)
                                bundle.putString("PRICE_PER_LITER", pricePerLiter)
                                bundle.putString("PRICE_DATE", priceDate)
                                bundle.putString("STATUS", status)
                                bundle.putString("STATION_ID", stationId)
                                bundle.putString("ASSOCIATION_ID", associationId)
                                bundle.putString("INDICATOR", indicator)
                                bundle.putString("LITER_EQUIVALENT", literEquivalent)
                                navigateWithBundle(
                                    R.id.action_fuelStationDetailsFragment_to_confirmFuelSavePurchaseFragment,
                                    bundle
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    private fun buildBranchDetailsView(data: Branch) {
        with(binding) {
            with(data) {
                stationNameText.text = stationName
                stationIdText.text = stationId

                branchNameText.text = branchName
                branchCodeText.text = code
                branchIdText.text = id
                branchLocationText.text = addressInfo
                buildFuelTpeList(pricing, -1)
                pricing.forEach {
                    println(it)
                }
            }
        }
    }

    private fun buildFuelTpeList(fuelTypes: List<FuelType>, selectedPosition: Int) {
        with(binding) {
            fuelTypeRv.layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            fuelTypeRv.adapter = FuelTypeAdapter(fuelTypes, selectedPosition)
        }
    }

    inner class FuelTypeAdapter(
        private val fuelTypes: List<FuelType>,
        private val selectedPosition: Int
    ) :
        RecyclerView.Adapter<FuelTypeAdapter.ViewHolder>() {

        inner class ViewHolder(itemView: FuelTypeItemBinding) :
            RecyclerView.ViewHolder(itemView.root) {
            val card = itemView.fuelTypeCardView
            val fuelTypeName = itemView.fuelTypeName
            val price = itemView.pricePerLiterText
            val icon = itemView.icon
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            return ViewHolder(
                FuelTypeItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            )
        }

        override fun getItemCount(): Int {
            return fuelTypes.size
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            with(holder) {
                fuelTypeName.text = fuelTypes[position].fuelTypeName
                val pricePerLiter =
                    "${fuelTypes[position].pricePerLiter?.toDouble()?.formatCurrency()} / Ltr"
                price.text = buildString {
                    append(fuelTypes[position].currency)
                    append(". ")
                    append(pricePerLiter)
                }

                if (selectedPosition == position) {
                    card.setBackgroundResource(R.drawable.selected_card_backgound)
                    icon.imageTintList =
                        ColorStateList.valueOf(resources.getColor(R.color.mcash_orange))
                }

                card.setOnClickListener {
                    fuelTypeId = fuelTypes[position].fuelTypeId.toString()
                    buildFuelTpeList(fuelTypes, position)
                    binding.purchaseDetailsView.isVisible = true
                }
            }
        }
    }


    private fun validatePurchase() {

        val location = passiveProvider(requireContext())
        location?.let {

            runBlocking {
                val deviceId = getDeviceID()
                val model = getPhoneModel()
                val imei = getDeviceID()
                val longitude = it.longitude
                val latitude = it.latitude
                val branchCode = branchCode
                val amount = binding.amountInput.text.toString()
                val fuelTypeId = fuelTypeId

                fuelSaveViewModel.validateFuelSavePurchase(
                    deviceId,
                    model,
                    imei,
                    longitude.toString(),
                    latitude.toString(),
                    branchCode,
                    amount,
                    fuelTypeId
                )
            }
        }
    }

    companion object {
        private const val SCREEN_TITLE = "Outlet Details"
        private var branchCode = ""
        private var fuelTypeId = ""
    }
}