package com.mcash.client.core.utils

import android.app.Activity
import android.os.CountDownTimer
import android.view.LayoutInflater
import androidx.core.view.isVisible
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.mcash.client.R
import com.mcash.client.databinding.LayoutAwaitConfirmationBinding
import java.text.DecimalFormat

class ConfirmationDialog(
    val activity: Activity,
) {
    private var dialog: BottomSheetDialog =
        BottomSheetDialog(activity, R.style.AppBottomSheetDialogTheme)

    fun showAwaitDialog(
        title: String,
        message: String,
        clickListener: ConfirmationClickLister
    ) {
        dialog.setCancelable(false)

        val dialogBinding = LayoutAwaitConfirmationBinding.inflate(LayoutInflater.from(activity))
        dialog.setContentView(dialogBinding.root)

        with(dialogBinding) {
            mtvTitle.text = title
            mtvMessage.text = message

            object : CountDownTimer(60000, 1000) {
                override fun onTick(millisUntilFinished: Long) {

                    val formatter = DecimalFormat("##")
                    val yourFormattedString: String = formatter.format(millisUntilFinished / 1000)
                    mtvTimer.isVisible = true
                    pbLoading.isVisible = true
                    mtvTimer.text = "$yourFormattedString s"
                }

                override fun onFinish() {
                    btnRetry.isVisible = true
                    mtvTimer.isVisible = false
                    pbLoading.isVisible = false
                }
            }.start()

            btnClose.setOnClickListener {
                dialog.dismiss()
                clickListener.onDismiss()
            }

            btnRetry.setOnClickListener {
                if (dialog.isShowing) {
                    clickListener.onRetry()
                }
            }
        }

        dialog.show()
    }

    fun hideAwaitDialog() {
        if (dialog.isShowing) dialog.dismiss()
    }
}

interface ConfirmationClickLister {
    fun onRetry()
    fun onDismiss()
}