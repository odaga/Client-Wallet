package com.mcash.client.presentation.ui.home

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.mcash.client.R
import com.mcash.client.databinding.LayoutTransferBottomSheetBinding
import timber.log.Timber

class TransferBottomSheet : BottomSheetDialogFragment() {

    private lateinit var binding: LayoutTransferBottomSheetBinding
    private var itemClickListener: TransferItemClickListener? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = LayoutTransferBottomSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if (dialog != null && dialog?.window != null) {
            dialog!!.window?.attributes?.windowAnimations = R.style.DialogAnimation
        }

        with(binding) {
            flClose.setOnClickListener {
                dismiss()
            }

            llWallet.setOnClickListener {
                itemClickListener?.onWalletClicked()
                dismiss()
            }

            llMobileMoney.setOnClickListener {
                itemClickListener?.onMobileMoneyCLicked()
                dismiss()
            }

            llBank.setOnClickListener {
                itemClickListener?.onBankClicked()
                dismiss()
            }
        }
    }

    override fun getTheme(): Int = R.style.BottomSheetDialogTheme

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
        BottomSheetDialog(requireContext(), theme)

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is TransferItemClickListener) {
            itemClickListener = context
        } else {
            Timber.e("You must implement TransferItemClickListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        itemClickListener = null
    }

    interface TransferItemClickListener {
        fun onWalletClicked()
        fun onMobileMoneyCLicked()
        fun onBankClicked()
    }
}