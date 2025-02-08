package com.mcash.client.presentation.ui.utilities.bundles

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.mcash.client.R
import com.mcash.client.databinding.LayoutBundlesBottomSheetBinding
import com.mcash.client.databinding.LayoutTransferBottomSheetBinding
import com.mcash.client.presentation.ui.home.TransferBottomSheet
import timber.log.Timber

class BundlesBottomSheet : BottomSheetDialogFragment(
) {

    private lateinit var binding: LayoutBundlesBottomSheetBinding
    private var itemClickListener: BundlesItemClickListener? = null


    override fun getTheme(): Int = R.style.BottomSheetDialogTheme

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
        BottomSheetDialog(requireContext(), theme)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = LayoutBundlesBottomSheetBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (dialog != null && dialog?.window != null) {
            dialog!!.window?.attributes?.windowAnimations = R.style.DialogAnimation
        }

        with(binding) {
            flClose.setOnClickListener {
                dismiss()
            }

            llVoice.setOnClickListener {
                println("========== Voice bundles =========")
                itemClickListener?.onVoiceClicked()
                dismiss()
            }

            llData.setOnClickListener {
                println("========== data bundles =========")
                itemClickListener?.onDataBundlesClicked()
                dismiss()
            }
        }
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is BundlesItemClickListener) {
            itemClickListener = context
        } else {
            Timber.e("You must implement BundlesItemClickListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        itemClickListener = null
    }


    interface BundlesItemClickListener {
        fun onVoiceClicked()

        fun onDataBundlesClicked()
    }
}