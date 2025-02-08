package com.mcash.client.presentation.ui.beneficiaries

import android.content.Intent
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.Gson
import com.mcash.client.R
import com.mcash.client.core.sealed.BeneficiaryState
import com.mcash.client.databinding.FragmentBeneficiaryListBinding
import com.mcash.client.databinding.LayoutAddNewBeneficiaryDialogBinding
import com.mcash.client.presentation.adapters.BeneficiaryAdapter
import com.mcash.client.core.base.BaseFragment
import com.mcash.client.core.utils.AlertType
import com.mcash.client.core.utils.Constants.SELECTED_BENEFICIARY
import com.mcash.client.core.utils.ToastUtils.showAlert
import com.mcash.client.core.utils.ZeroTextRemover
import com.mcash.client.core.utils.getRandomText
import com.mcash.domain.model.Beneficiary
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class BeneficiaryListFragment : BaseFragment<FragmentBeneficiaryListBinding>(FragmentBeneficiaryListBinding::inflate) {

    private val viewModel: BeneficiaryViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            mtvTitle.text = getString(R.string.favorites)
            btnBack.setOnClickListener {
                if (etSearch.isVisible) {
                    mtvTitle.visibility = View.VISIBLE
                    etSearch.visibility = View.GONE
                } else {
                    requireActivity().finish()
                }
            }
            btnAdd.setOnClickListener { showAddDialog() }
            btnSearch.setOnClickListener {
                etSearch.visibility = View.VISIBLE
                mtvTitle.visibility = View.GONE
                etSearch.requestFocus()
            }

            etSearch.doOnTextChanged { text, _, _, _ ->
                if (text.toString().isNotEmpty()) {

                    if (text.toString().length > 1) {
                        Timber.d("TExt now: ${text.toString()}")
                        viewModel.searchBeneficiary("%${text.toString().trim()}%")
                    } else {
                        Timber.d("Use default: ${text.toString()}")
                        viewModel.getSavedBeneficiaries()
                    }
                }
            }
        }


        lifecycleScope.launchWhenStarted {
            viewModel.beneficiaryState.collect {
                when (it) {
                    is BeneficiaryState.Loading -> {}
                    is BeneficiaryState.Success -> {

                        populateBeneficiary(it.data)

                        if (it.data.isEmpty()) {
                            binding.llEmpty.visibility = View.VISIBLE
                            binding.beneficiaryRecycler.visibility = View.GONE
                        } else {
                            binding.llEmpty.visibility = View.GONE
                            binding.beneficiaryRecycler.visibility = View.VISIBLE
                        }
                    }
                    is BeneficiaryState.Error -> {}
                }
            }
        }
    }

    private fun showAddDialog() {
        val metrics = DisplayMetrics()
        requireActivity().windowManager?.defaultDisplay?.getMetrics(metrics)

        val dialog = BottomSheetDialog(requireContext(), R.style.AppBottomSheetDialogTheme)
        dialog.setCancelable(false)
        dialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED
        dialog.behavior.peekHeight = metrics.heightPixels

        val dialogBinding = LayoutAddNewBeneficiaryDialogBinding.inflate(layoutInflater)
        dialog.setContentView(dialogBinding.root)

        with(dialogBinding) {
            etPhoneNumber.addTextChangedListener(ZeroTextRemover(etPhoneNumber))
            ccp.registerCarrierNumberEditText(etPhoneNumber)

            flClose.setOnClickListener { dialog.dismiss() }
            btnAddBeneficiary.setOnClickListener {

                when {
                    etName.text.toString().isEmpty() -> {
                        showAlert(
                            getString(R.string.warning),
                            "Name required",
                            AlertType.FAILURE
                        )
                    }

                    etPhoneNumber.text.toString().isEmpty() -> {
                        showAlert(
                            getString(R.string.warning),
                            "Phone number required",
                            AlertType.FAILURE
                        )
                    }

                    etPhoneNumber.text.toString().length < 9 -> {
                        showAlert(
                            getString(R.string.warning),
                            "Invalid phone number",
                            AlertType.FAILURE
                        )
                    }
                    else -> {

                        val nums = ArrayList<String>()
                        nums.add(ccp.fullNumberWithPlus)

                        val beneficiary = Beneficiary().apply {
                            id = getRandomText()
                            name = etName.text.toString()
                            numbers = nums
                        }

                        viewModel.saveBeneficiary(beneficiary)
                        dialog.dismiss()
                        showAlert(
                            getString(R.string.success),
                            "Favorite saved successfully",
                            AlertType.SUCCESS
                        )
                    }
                }
            }
        }
        dialog.show()
    }

    private fun populateBeneficiary(beneficiaries: List<Beneficiary>) {
        val beneficiaryAdapter = BeneficiaryAdapter(
            requireContext(),
            object : BeneficiaryAdapter.BeneficiaryClickListener {
                override fun onBeneficiaryClicked(beneficiary: Beneficiary) {
                    val intent = Intent()
                    val obj = Gson().toJson(beneficiary)
                    intent.putExtra(SELECTED_BENEFICIARY, obj)
                    requireActivity().setResult(AppCompatActivity.RESULT_OK, intent)
                    requireActivity().finish()
                }

                override fun onBeneficiaryDelete(beneficiary: Beneficiary) {
                    showDeleteDialog(beneficiary)
                }
            }
        )

        val list = ArrayList(beneficiaries)
        beneficiaryAdapter.contacts = list
        binding.beneficiaryRecycler.apply {
            adapter = beneficiaryAdapter
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        }
    }

    private fun showDeleteDialog(beneficiary: Beneficiary) {
        val dialogBuilder =
            MaterialAlertDialogBuilder(requireContext(), R.style.AlertDialog).apply {
                setMessage("Are you sure you want to delete this beneficiary?")
                setCancelable(false)
                setNegativeButton("No") { dialog, _ ->
                    dialog.cancel()
                }
                setPositiveButton("Yes") { dialog, _ ->
                    viewModel.deleteBeneficiary(beneficiary)
                    dialog.dismiss()
                }
            }

        val alert = dialogBuilder.create()
        alert.setTitle("Warning")
        alert.show()
    }
}