package com.mcash.client.presentation.ui.utilities.bundles

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import com.mcash.client.core.base.BaseFragment
import com.mcash.client.databinding.FragmentBundlesBinding
import com.mcash.client.presentation.ui.utilities.internet.InternetFragment
import com.mcash.client.presentation.ui.utilities.voice.VoiceFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BundlesFragment : BaseFragment<FragmentBundlesBinding>(FragmentBundlesBinding::inflate) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            val adapter = ViewPagerAdapter(requireActivity())
            viewPager.adapter = adapter

            TabLayoutMediator(tabLayout, viewPager) { tab, position ->
                tab.text = tabList[position]
            }.attach()
            civBack.setOnClickListener {
                navigateUp()
            }
        }


    }


    private inner class ViewPagerAdapter(fa: FragmentActivity) : FragmentStateAdapter(fa) {
        override fun getItemCount(): Int = NUM_PAGES

        override fun createFragment(position: Int): Fragment {

            return when (position) {
                0 -> {
                    VoiceFragment()
                }

                1 -> {
                    InternetFragment()
                }

                else -> {
                    VoiceFragment()
                }
            }
        }
    }

    companion object {
        private const val NUM_PAGES = 2
        private var tabList = mutableListOf("Voice Bundles", "Data Bundles")
    }


}