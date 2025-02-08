package com.mcash.client.core.utils

import android.app.Activity
import androidx.fragment.app.Fragment
import com.mcash.client.R
import com.tapadoo.alerter.Alerter

object ToastUtils {

    const val DURATION_SHORT = 500L
    const val DURATION_NORMAL = 1500L
    const val DURATION_LONG = 2000L

    fun <F : Fragment> F.showAlert(
        title: String,
        description: String,
        type: AlertType,
        duration: Long = DURATION_NORMAL
    ) {
        val icon = when (type) {
            AlertType.INFO -> R.drawable.ic_alert_info
            AlertType.FAILURE -> R.drawable.ic_alert_fail
            AlertType.SUCCESS -> R.drawable.ic_alert_success
        }
        val backgroundColor = when (type) {
            AlertType.INFO -> R.color.mcash_grey
            AlertType.FAILURE -> R.color.danger
            AlertType.SUCCESS -> R.color.success
        }
        Alerter.create(requireActivity())
            .setTitle(title)
            .setText(description)
            .setIcon(icon)
            .setTextAppearance(R.style.AlerterStyle)
            .setTitleAppearance(R.style.AlerterStyle)
            .setBackgroundColorRes(backgroundColor)
            .setDuration(duration)
            .enableIconPulse(true)
            .enableSwipeToDismiss()
            .show()
    }

    fun <A : Activity> A.showAlert(
        title: String,
        description: String,
        type: AlertType,
        duration: Long = DURATION_NORMAL
    ) {
        val icon = when (type) {
            AlertType.INFO -> R.drawable.ic_alert_info
            AlertType.FAILURE -> R.drawable.ic_alert_fail
            AlertType.SUCCESS -> R.drawable.ic_alert_success
        }
        val backgroundColor = when (type) {
            AlertType.INFO -> R.color.mcash_grey
            AlertType.FAILURE -> R.color.danger
            AlertType.SUCCESS -> R.color.success
        }
        Alerter.create(this)
            .setTitle(title)
            .setText(description)
            .setIcon(icon)
            .setTextAppearance(R.style.AlerterStyle)
            .setTitleAppearance(R.style.AlerterStyle)
            .setBackgroundColorRes(backgroundColor)
            .setDuration(duration)
            .enableIconPulse(true)
            .enableSwipeToDismiss()
            .show()
    }
}

enum class AlertType { INFO, FAILURE, SUCCESS }