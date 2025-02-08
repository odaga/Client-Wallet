package com.mcash.client.presentation.ui.home

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import coil.load
import com.mcash.client.R
import com.mcash.client.core.models.home.HomeIcon
import com.mcash.client.databinding.FragmentPaymentsListBinding
import com.mcash.client.presentation.adapters.HomeIconAdapter
import com.mcash.client.core.base.BaseFragment
import com.mcash.client.core.utils.Constants
import com.mcash.client.core.utils.Constants.HomeIds.CRYPTO
import com.mcash.client.core.utils.Constants.HomeIds.E_VOUCHER
import com.mcash.client.core.utils.Constants.HomeIds.INSURANCE
import com.mcash.client.core.utils.Constants.HomeIds.LOANS
import com.mcash.client.core.utils.Constants.HomeIds.PENALTIES
import com.mcash.client.core.utils.Constants.HomeIds.VIRTUAL_CARDS

class PaymentsListFragment: BaseFragment<FragmentPaymentsListBinding>(FragmentPaymentsListBinding::inflate) {

    private val payments = mutableListOf<HomeIcon>().apply {
        add(HomeIcon("Airtime", R.drawable.ic_phone, Constants.HomeIds.AIRTIME))
        add(HomeIcon("Internet", R.drawable.ic_globe, Constants.HomeIds.INTERNET_BUNDLES))
        add(HomeIcon("Umeme", R.drawable.ic_bulb, Constants.HomeIds.PAY_UMEME))
        add(HomeIcon("TV", R.drawable.ic_tv, Constants.HomeIds.PAY_TV))
        add(HomeIcon("NWSC", R.drawable.ic_tap, Constants.HomeIds.PAY_NWSC))
        add(HomeIcon("School Fees", R.drawable.ic_school, Constants.HomeIds.SCHOOL_FEES))
        add(HomeIcon("Merchant", R.drawable.ic_store, Constants.HomeIds.PAY_MERCHANT))
        add(HomeIcon("Statement", R.drawable.ic_receipt, Constants.HomeIds.STATEMENT))
        add(HomeIcon("Loans", R.drawable.ic_money_circle, LOANS))
        add(HomeIcon("eVoucher", R.drawable.ic_credit_card, E_VOUCHER))
        add(HomeIcon("Cypto", R.drawable.ic_money_circle, CRYPTO))
        add(HomeIcon("Insurance", R.drawable.ic_truck, INSURANCE))
        add(HomeIcon("Virtual Cards", R.drawable.ic_credit_card, VIRTUAL_CARDS))
        add(HomeIcon("Penalties", R.drawable.ic_block, PENALTIES))
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            civBanner.load("https://mcash.ug/site/images/oldman.jpg") {
                crossfade(true)
                placeholder(R.drawable.default_placeholder)
                error(R.drawable.default_placeholder)
            }

            val iconAdapter = HomeIconAdapter(payments, requireActivity(), object: HomeIconAdapter.HomeIconClickListener{
                override fun onAirtimeClicked() {
                    TODO("Not yet implemented")
                }

                override fun onInternetClicked() {
                    TODO("Not yet implemented")
                }

                override fun onTVClicked() {
                    TODO("Not yet implemented")
                }

                override fun onVoiceClicked() {
                    TODO("Not yet implemented")
                }

                override fun onUraClicked() {
                    TODO("Not yet implemented")
                }

                override fun onMerchantClicked() {
                    TODO("Not yet implemented")
                }

                override fun onLoansClicked() {
                    TODO("Not yet implemented")
                }

                override fun onSaccoClicked() {
                    TODO("Not yet implemented")
                }


                override fun onMoreClicked() {
                    TODO("Not yet implemented")
                }

                override fun onNwscClicked() {
                    TODO("Not yet implemented")
                }

                override fun onUmemeClicked() {
                    TODO("Not yet implemented")
                }

                override fun onBundlesClicked() {
                    TODO("Not yet implemented")
                }
            })

            paymentsRecyclerview.apply {
                adapter = iconAdapter
                layoutManager = GridLayoutManager(requireContext(), 4)
            }
        }
    }
}