package com.mcash.client.core.utils

import android.app.Activity
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.mcash.client.R
import com.mcash.client.databinding.LayoutFailureDialogBinding
import com.mcash.client.databinding.LayoutSuccessDialogBinding

fun <A : Activity> A.showSuccessDialog(
    title: String,
    message: String,
    status:Int,
    successDialogClickLister: SuccessDialogClickLister?
) {
//    val metrics = DisplayMetrics()
//    this.windowManager?.defaultDisplay?.getMetrics(metrics)

    val dialog = BottomSheetDialog(this, R.style.AppBottomSheetDialogTheme)
    dialog.setCancelable(false)
//    dialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED
//    dialog.behavior.peekHeight = metrics.heightPixels

    val dialogBinding = LayoutSuccessDialogBinding.inflate(layoutInflater)
    val failureDialog = LayoutFailureDialogBinding.inflate(layoutInflater)

    dialog.setContentView(dialogBinding.root)

    if (status==1){
        dialog.setContentView(dialogBinding.root)
        with(dialogBinding) {

            mtvTitle.text = title
            mtvSuccesMessage.text = message

            flClose.setOnClickListener {
                if (successDialogClickLister !== null) {
                    dialog.dismiss()
                    if (!dialog.isShowing) {
                        successDialogClickLister.onDoneClicked()
                    }
                } else {
                    dialog.dismiss()
                }
            }

            btnDone.setOnClickListener {

                if (successDialogClickLister !== null) {
                    dialog.dismiss()
                    if (!dialog.isShowing) {
                        successDialogClickLister.onDoneClicked()
                    }
                } else {
                    dialog.dismiss()
                }

            }
        }
    }
    else if (status==0){
        dialog.setContentView(failureDialog.root)
        with(failureDialog) {

            mtvTitle.text = title
            mtvSuccesMessage.text = message

            flClose.setOnClickListener {
                if (successDialogClickLister !== null) {
                    dialog.dismiss()
                    if (!dialog.isShowing) {
                        successDialogClickLister.onDoneClicked()
                    }
                } else {
                    dialog.dismiss()
                }
            }

            btnDone.setOnClickListener {

                if (successDialogClickLister !== null) {
                    dialog.dismiss()
                    if (!dialog.isShowing) {
                        successDialogClickLister.onDoneClicked()
                    }
                } else {
                    dialog.dismiss()
                }

            }
        }
    }

    dialog.show()
}

interface SuccessDialogClickLister {
    fun onDoneClicked()
}

