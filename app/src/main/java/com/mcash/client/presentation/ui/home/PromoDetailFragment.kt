package com.mcash.client.presentation.ui.home

import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.annotation.RequiresApi
import coil.load
import com.mcash.client.R
import com.mcash.client.databinding.FragmentPromoDetailBinding
import com.mcash.client.core.base.BaseFragment
import com.mcash.client.core.models.home.PromoEntity
import com.mcash.client.core.utils.formatHtml

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class PromoDetailFragment : BaseFragment<FragmentPromoDetailBinding>(FragmentPromoDetailBinding::inflate) {
    private var param1: String? = null
    private var param2: String? = null

//    private val args: PromoDetailFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        val promo = args.promo
//        promo?.let {
//            populatePromoDetails(it)
//        }

    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun populatePromoDetails(promo: PromoEntity) {
        with(binding) {
            civBanner.load(promo.banner) {
                crossfade(true)
                placeholder(R.drawable.default_placeholder)
                error(R.drawable.default_placeholder)
            }

//            val activity = requireActivity() as MainActivity
//            activity.setToolbarTitle(promo.title)

            mtvPromoTitle.text = promo.title
            mtvPromoBody.text = promo.formattedBody.formatHtml()
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            PromoDetailFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }


}