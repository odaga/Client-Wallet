package com.mcash.client.core.utils

import android.app.Activity
import android.view.LayoutInflater
import androidx.core.content.ContextCompat
import androidx.core.widget.doOnTextChanged
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.mcash.client.R
import com.mcash.client.databinding.LayoutPinConfirmationBinding
import timber.log.Timber

class PinConfirmationDialog(val activity: Activity) {

    private var dialog = BottomSheetDialog(activity, R.style.AppBottomSheetDialogTheme)
    private var dialogBinding: LayoutPinConfirmationBinding? = null

    fun showPinConfirmationDialog(clickListener: PinConfirmClickListener) {
        dialog.setCancelable(false)
        dialogBinding = LayoutPinConfirmationBinding.inflate(LayoutInflater.from(activity))

        dialogBinding?.let { binding ->
            dialog.setContentView(binding.root)
            with(binding) {
                pvPin.requestFocus()

                pvPin.doOnTextChanged { text, _, _, _ ->
                    if (text.toString().length > 1) pvPin.setLineColor(ContextCompat.getColorStateList(activity, R.color.mcash_grey_80))
                }

                pvPin.setOnClickListener {
                    Timber.d("Pin view clicked...")
                }

                btnClose.setOnClickListener {
                    dialog.dismiss()
                }

                btnConfirm.setOnClickListener {
                    if (dialog.isShowing) {
                        if (pvPin.text.toString().isEmpty()) {
                            pvPin.setLineColor(ContextCompat.getColorStateList(activity, R.color.danger))
                        } else {
                            clickListener.onConfirmed()
                        }
                    }
                }
            }

        }

        dialog.show()
    }

    fun hidePinConfirmationDialog() {
        if (dialog.isShowing) dialog.dismiss()
    }

    fun showError() {
        dialogBinding?.let {
            with(it) {
                pvPin.setLineColor(ContextCompat.getColorStateList(activity, R.color.danger))
            }
        }
    }

    val getPinText get() = dialogBinding?.pvPin?.text.toString()
}

interface PinConfirmClickListener {
    fun onConfirmed()
}