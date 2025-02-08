package com.mcash.client.presentation.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony
import android.widget.EditText
import timber.log.Timber

class OtpReceiver : BroadcastReceiver() {

    fun setEditText(editText: EditText?) {
        Companion.editText = editText
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        val messages = Telephony.Sms.Intents.getMessagesFromIntent(intent)

        for (message in messages) {
            val msg = message.messageBody
            Timber.d("OTP message: $msg")
            //val str = msg.filter { !it.isWhitespace() }
            //val otp = msg.takeLast(4)
            val otp = msg.substringBefore('.').takeLast(4)
            Timber.d("OTP: $otp")
            editText?.setText(otp)
        }
    }

    companion object {
        private var editText: EditText? = null
    }
}
