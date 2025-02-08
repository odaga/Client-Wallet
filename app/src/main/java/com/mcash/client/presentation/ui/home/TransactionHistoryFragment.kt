package com.mcash.client.presentation.ui.home

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.mcash.client.R
import com.mcash.client.core.base.BaseFragment
import com.mcash.client.databinding.FragmentTransactionHistoryBinding
import com.mcash.client.presentation.adapters.TransactionAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.runBlocking

@AndroidEntryPoint
class TransactionHistoryFragment :
    BaseFragment<FragmentTransactionHistoryBinding>(FragmentTransactionHistoryBinding::inflate) {

    private val viewModel: HomeViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        runBlocking {
            viewModel.getTransactionHistory()
        }

        with(binding) {
            with(layoutToolbar) {
                btnBack.setOnClickListener { navigateUp() }
                mtvTitle.text = getString(R.string.transaction_history)
            }

            val historyAdapter = TransactionAdapter()
            historyRecycler.apply {
                adapter = historyAdapter
                layoutManager = LinearLayoutManager(requireContext())
            }

            viewModel.historyState.observe(viewLifecycleOwner) {
                when (it) {
                    is TransactionHistoryState.Loading -> {
                        loadingAnimationView.visibility = View.VISIBLE
                        historyRecycler.isVisible = false
                        llEmpty.isVisible = false

                    }

                    is TransactionHistoryState.Error -> {
                        loadingAnimationView.visibility = View.GONE
                        historyRecycler.isVisible = false
                        llEmpty.isVisible = false
                    }

                    is TransactionHistoryState.Success -> {
                        loadingAnimationView.visibility = View.GONE
                        if (it.data.isEmpty()) {
                            historyRecycler.isVisible = false
                            llEmpty.isVisible = true
                        } else {
                            historyRecycler.isVisible = true
                            llEmpty.isVisible = false
                        }
                        historyAdapter.submitList(it.data)
                    }
                    else -> Unit
                }
            }
        }
    }
}