package com.mcash.client.presentation.ui.utilities.tv

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnFocusChangeListener
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mcash.client.R
import com.mcash.client.core.base.BaseFragment
import com.mcash.client.core.utils.formatCurrency
import com.mcash.client.databinding.FragmentSelectTVBinding
import com.mcash.client.databinding.TvPackageItemRowBinding
import com.mcash.client.databinding.TvProviderCardItemBinding
import com.mcash.client.presentation.ui.utilities.UtilityViewModel
import com.mcash.domain.model.TvEntity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.runBlocking


@AndroidEntryPoint
class SelectTVFragment : BaseFragment<FragmentSelectTVBinding>(FragmentSelectTVBinding::inflate) {

    val viewModel: UtilityViewModel by viewModels()
    var tv = ""
    var totalAmount = ""
    var phone = ""
    var customerName = ""

    var transactionRef: String = ""
    var shortTransRef = ""
    var transToken = ""
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {
            with(layoutToolbar) {
                btnBack.setOnClickListener { navigateUp() }
                mtvTitle.text = getString(R.string.tv)
            }

            // Listener for validate button
            btnValidate.setOnClickListener {
                validateTvPayment()
            }



            viewModel.fetchTvPackages(tvProviderTypes.toList())

            runBlocking {
                with(viewModel) {
                    // Listen to changes in the  Tv Packages List
                    tvPackagesState.observe(viewLifecycleOwner) {
                        when (it) {
                            is UtilityViewModel.TvPackagesState.Initial -> {
                                hideProgressDialog()
                                loadingAnimationView.isVisible = false
                            }

                            is UtilityViewModel.TvPackagesState.Loading -> {
                                loadingAnimationView.isVisible = true
                            }

                            is UtilityViewModel.TvPackagesState.Error -> {
                                loadingAnimationView.isVisible = false
                                errorLayout.isVisible = true
                                errorText.text = it.message
                            }

                            is UtilityViewModel.TvPackagesState.Success -> {
                                loadingAnimationView.isVisible = false
                                tvProviderRv.visibility = View.VISIBLE
                                // Load TV Providers
                                loadTvProvidersList()
                            }
                        }
                    }

                    // Listen to TV Payment Validation status
                    validateTVState.observe(viewLifecycleOwner) {
                        when (it) {
                            is UtilityViewModel.ValidateTVState.Loading -> showProgressDialog()

                            is UtilityViewModel.ValidateTVState.Error -> {
                                hideProgressDialog()
                                showErrorDialog(it.message)
                            }

                            is UtilityViewModel.ValidateTVState.Success -> {
                                hideProgressDialog()


                                println("==--=-= validation data: \n ${it.data}")
                                transactionRef = it.data.transactionRef ?: "-"
                                shortTransRef = it.data.shortTransactionRef ?: "-"
                                transToken = it.data.transactionToken ?: "-"
                                customerName = it.data.customerName ?: "---"


                                val bundle = Bundle()
                                bundle.putString("PROVIDER", selectedTvProvider)
                                bundle.putString("SELECTED_PACKAGE_ID", selectedTvProvider)
                                bundle.putString("DECODER_NUMBER", smartCardNumber)
                                bundle.putString("TRANSACTION_REF", transactionRef)
                                bundle.putString("SHORT_TRANSACTION_REF", shortTransRef)
                                bundle.putString("TRANSACTION_TOKEN", transToken)
                                bundle.putString("CHARGE", it.data.surcharge.toString())
                                bundle.putString("CUSTOMER_NAME", customerName)
                                bundle.putString("AMOUNT", it.data.amount.toString())

                                navigateWithBundle(
                                    R.id.action_selectTVFragment_to_confirmTvFragment, bundle
                                )
                            }

                            else -> Unit
                        }
                    }
                }
            }
        }
    }


    private fun loadTvProvidersList() {
        val list = arrayOf("dstv", "gotv", "azamtv", "zukutv", "startimestv")
        with(binding) {
            tvProviderRv.layoutManager = LinearLayoutManager(requireContext())
            val adapter = ProviderAdapter(list.toList())
            tvProviderRv.adapter = adapter
        }
    }

    private fun loadPackages(providerName: String) {
        runBlocking {
            // set selected Tv Provider
            selectedTvProvider = providerName
            val tvPackageNames = ArrayList<String>()
            val tvPackages = viewModel.getTvPackageByProvider(providerName)
            tvPackages.forEach {
                tvPackageNames.add(it.paymentitemname.toString())
            }
            with(binding) {
                tvPackagesRv.layoutManager = LinearLayoutManager(requireContext())
                tvPackagesRv.adapter = TvPackagesAdapter(tvPackages.toList())
                smartCardNumber = decoderNumberField.text.toString()
            }
        }
    }

    private fun showSelectionView() {
        with(binding) {
            tvProviderRv.visibility = View.GONE
            selectPackageView.visibility = View.VISIBLE
            loadPackages(selectedTvProvider)
        }
    }

    private fun validateTvPayment() {
        runBlocking {
            totalAmount = amount.replace(",", "")
            amount = totalAmount

            runBlocking {
                viewModel.validateTVCustomer(
                    selectedTvProvider,
                    getDeviceID() ?: "",
                    getPhoneModel() ?: "",
                    binding.decoderNumberView.toString(),
                    selectedPackageId,
                    totalAmount,
                )
            }
        }
    }


    inner class ProviderAdapter(private val providers: List<String>) :
        RecyclerView.Adapter<ProviderAdapter.ViewHolder>() {

        inner class ViewHolder(itemView: TvProviderCardItemBinding) :
            RecyclerView.ViewHolder(itemView.root) {
            val card = itemView.cardItem
            val providerName = itemView.tvProviderName
            val providerLogo = itemView.providerLogo
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            return ViewHolder(
                TvProviderCardItemBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
            )
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            with(holder) {
                card.setOnClickListener {
                    selectedTvProvider = providers[position]
                    showSelectionView()
                }
                when (providers[position]) {
                    "dstv" -> {
                        providerName.text = "DSTV"
                        providerLogo.setImageResource(R.drawable.dstv)
                    }

                    "gotv" -> {
                        providerName.text = "GOTV"
                        providerLogo.setImageResource(R.drawable.gotv)
                    }

                    "showmax" -> {
                        providerName.text = "SHOWMAX"
                        providerLogo.setImageResource(R.drawable.showmax)
                    }

                    "azamtv" -> {
                        providerName.text = "AZAM TV"
                        providerLogo.setImageResource(R.drawable.azam)
                    }

                    "zukutv" -> {
                        providerName.text = "ZUKU TV"
                        providerLogo.setImageResource(R.drawable.zuku)
                    }

                    "startimestv" -> {
                        providerName.text = "STARTIMES TV"
                        providerLogo.setImageResource(R.drawable.startimes)
                    }

                }
            }
        }

        override fun getItemCount(): Int {
            return providers.size
        }
    }

    inner class TvPackagesAdapter(private val packages: List<TvEntity>) :
        RecyclerView.Adapter<TvPackagesAdapter.PackagesViewHolder>() {

        inner class PackagesViewHolder(itemView: TvPackageItemRowBinding) :
            RecyclerView.ViewHolder(itemView.root) {
            val card = itemView.tvPackageCard
            val name = itemView.tvPackageName
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PackagesViewHolder {
            return PackagesViewHolder(
                TvPackageItemRowBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
            )
        }

        override fun onBindViewHolder(holder: PackagesViewHolder, position: Int) {
            with(holder) {
                name.text = packages[position].paymentitemname

                selectedPackageId = packages[position].packageId

                card.setOnClickListener {
                    card.setBackgroundResource(R.drawable.highlighted_card_style)
//                    binding.tvSummaryView.isVisible = true
//                    binding.tvPackagePrice.text =
                    "UGX ${packages[position].amount?.toDouble()?.formatCurrency()}"
                    amount = packages[position].amount.toString()

                    with(binding) {
                        selectedPackageId = packages[position].packageId
                        selectedPackageField.text = packages[position].paymentitemname
                        selectedPackagePrice.text =
                            "UGX ${packages[position].amount?.toDouble()?.formatCurrency()}"
                    }
                }
            }
        }

        override fun getItemCount(): Int {
            return packages.size
        }
    }


    companion object {
        val tvProviderTypes = arrayOf("dstv", "azamtv", "zukutv", "startimestv")
        private var smartCardNumber: String = ""
        private var amount: String = ""
        private var selectedTvProvider: String = ""
        private var selectedPackageId: String = ""

    }
}

