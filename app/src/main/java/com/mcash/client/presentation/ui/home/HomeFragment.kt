package com.mcash.client.presentation.ui.home

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.annotation.RequiresApi
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.mcash.client.R
import com.mcash.client.core.base.BaseFragment
import com.mcash.client.core.models.home.HomeIcon
import com.mcash.client.core.models.home.PromoEntity
import com.mcash.client.core.utils.Constants
import com.mcash.client.core.utils.formatCurrency
import com.mcash.client.databinding.FragmentHomeBinding
import com.mcash.client.presentation.MainActivity
import com.mcash.client.presentation.adapters.HomeIconAdapter
import com.mcash.client.presentation.adapters.PromosAdapter
import com.mcash.client.presentation.ui.utilities.bundles.BundlesBottomSheet
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber
import java.time.LocalTime

@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding>(FragmentHomeBinding::inflate) {

    private val viewModel: HomeViewModel by viewModels()

    private lateinit var bundlesBottomSheet: BundlesBottomSheet

    private var itemClickListener: HomeClickInterface? = null
    private var balance: String = "0"

    private val homeIcons = mutableListOf<HomeIcon>().apply {

        add(HomeIcon("Umeme", R.drawable.umeme, Constants.HomeIds.PAY_UMEME))
        add(HomeIcon("URA", R.drawable.ura, Constants.HomeIds.URA))
        add(HomeIcon("NWSC", R.drawable.nwsc, Constants.HomeIds.PAY_NWSC))
        add(HomeIcon("Pay TV", R.drawable.ic_tv, Constants.HomeIds.PAY_TV))
        add(HomeIcon("Buy Airtime", R.drawable.ic_phone, Constants.HomeIds.AIRTIME))
        add(HomeIcon("Buy Bundles", R.drawable.mobile, Constants.HomeIds.BUNDLES))
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val user = viewModel.savedUser

        bundlesBottomSheet = BundlesBottomSheet()

        lifecycleScope.launch {

//            viewModel.promoState.collect {
//                when(it) {
//                    is MainViewModel.PromoState.Loading -> {}
//                    is MainViewModel.PromoState.Error -> {Timber.e("Error: ${it.message}")}
//                    is MainViewModel.PromoState.Success -> {
//                        populatePromos(it.data)
//                    }
//                }
//            }

            val x = viewModel.getNotifications()
//            if (x>0) binding.textView.visibility = View.VISIBLE else binding.textView.visibility = View.GONE
        }

        with(binding) {
            val activity = requireActivity() as MainActivity
            mtvUsername.text = buildString {
                append(getGreeting())
                append(", ")
                append(user.name.substringBefore(" "))
                append(".")
            }

            // Observe Balance changes
            viewModel.balanceState.observe(viewLifecycleOwner) {
                when (it) {
                    is BalanceState.Success -> {
                        balance = it.data.toDouble().formatCurrency()
                    }

                    else -> Unit
                }
            }


            showBalance.setOnClickListener {
                hideBalance.visibility = View.VISIBLE
                showBalance.visibility = View.GONE
                binding.mtvWalletBalance.text = balance

            }

            hideBalance.setOnClickListener {
                hideBalance.visibility = View.GONE
                showBalance.visibility = View.VISIBLE
                binding.mtvWalletBalance.text = BALANCE_PLACE_HOLDER
            }


            val iconAdapter = HomeIconAdapter(
                homeIcons,
                requireActivity(),
                object : HomeIconAdapter.HomeIconClickListener {
                    override fun onAirtimeClicked() {
                        val directions =
                            HomeFragmentDirections.actionFragmentHomeToAirtimeFragment()
                        navigate(directions)
                    }

                    override fun onInternetClicked() {
                        navigate(HomeFragmentDirections.actionFragmentHomeToInternetFragment())
                    }

                    override fun onTVClicked() {
//                        navigate(HomeFragmentDirections.actionFragmentHomeToTVFragment())
                        navigate(HomeFragmentDirections.actionFragmentHomeToSelectTVFragment())
                    }

                    override fun onVoiceClicked() {
                        navigate(HomeFragmentDirections.actionFragmentHomeToVoiceFragment())
                    }

                    override fun onUraClicked() {
                        navigate(HomeFragmentDirections.actionFragmentHomeToUraFragment())
                    }

                    override fun onMerchantClicked() {
                        navigate(HomeFragmentDirections.actionFragmentHomeToMerchantFragment())
                    }

                    override fun onLoansClicked() {
                        navigate(HomeFragmentDirections.actionFragmentHomeToLoansFragment())
                        //showAlert("", "", AlertType.INFO)
                    }

                    override fun onSaccoClicked() {
                        navigate(HomeFragmentDirections.actionFragmentHomeToSaccoFragment())
                    }

                    override fun onMoreClicked() {
                        val directions =
                            HomeFragmentDirections.actionFragmentHomeToFragmentPaymentList()
                        navigate(directions)
                    }

                    override fun onNwscClicked() {
                        navigate(HomeFragmentDirections.actionFragmentHomeToFragmentNwsc())
                    }

                    override fun onUmemeClicked() {
                        navigate(HomeFragmentDirections.actionFragmentHomeToUmemeFragment())
                    }

                    override fun onBundlesClicked() {
//                        val directions =
//                            HomeFragmentDirections.actionFragmentHomeToBundlesFragment()
//                        navigate(directions)
                        bundlesBottomSheet = BundlesBottomSheet()
                        bundlesBottomSheet.show(
                            requireActivity().supportFragmentManager,
                            bundlesBottomSheet.tag
                        )
                    }

                })
            iconsRecyclerview.apply {
                adapter = iconAdapter
                layoutManager = GridLayoutManager(requireContext(), 3)
            }

            cardTransfer.setOnClickListener {
                activity.onTransferClicked()
            }

            cardTopup.setOnClickListener {
                val directions = HomeFragmentDirections.actionFragmentHomeToFragmentTop()
                navigate(directions)
            }

            cardHistory.setOnClickListener {
                val directions =
                    HomeFragmentDirections.actionFragmentHomeToFragmentTransactionHistory()
                navigate(directions)
            }
            llSettings.setOnClickListener {
                navigate(HomeFragmentDirections.actionFragmentHomeToSettingsFragment())
//                navigate(HomeFragmentDirections.actionFragmentHomeToTransactionSummaryFragment())
            }

            fuelSaveCardView.setOnClickListener {
//                action_fragment_home_to_fuelSaveFragment
                navigate(HomeFragmentDirections.actionFragmentHomeToFuelSaveFragment())
            }
        }

    }


    private fun populatePromos(list: List<PromoEntity>) {
        val promoAdapter = PromosAdapter(list, object : PromosAdapter.PromoClickListener {
            override fun onPromoClicked(promo: PromoEntity) {
                val directions = HomeFragmentDirections.actionFragmentHomeToFragmentPromoDetail()
                navigate(directions)
            }

        })
//        binding.promotionRecycler.apply {
//            adapter = promoAdapter
//            layoutManager =
//                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
//        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is HomeClickInterface) {
            itemClickListener = context
        } else {
            Timber.e("You must implement HomeClickInterface")
        }
    }

    private fun getGreeting(): String {
        val currentTime = LocalTime.now()
        return when {
            currentTime.isBefore(LocalTime.NOON) -> "Good Morning"
            currentTime.isBefore(LocalTime.of(18, 0)) -> "Good Afternoon"
            else -> "Good Evening"
        }
    }

    interface HomeClickInterface {
        fun onTransferClicked()
        fun onTopUpClicked()
    }


    companion object {
        private var IS_BALANCE_VISIBLE = false
        private var BALANCE_PLACE_HOLDER = "--- ---"
    }

}