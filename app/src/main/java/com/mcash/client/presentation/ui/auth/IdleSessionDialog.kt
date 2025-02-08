package com.mcash.client.presentation.ui.auth

import android.app.Dialog
import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.mcash.client.R
import com.mcash.client.presentation.MainViewModel
import java.util.concurrent.TimeUnit

class IdleSessionDialog(
    private val mainViewModel: MainViewModel,
    private val listener: DialogClickListener
) : BottomSheetDialogFragment(

) {

    private lateinit var dialogMessage: TextView
    private var countDownTimer: CountDownTimer? = null

    override fun getTheme(): Int = R.style.BottomSheetDialogTheme

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
        BottomSheetDialog(requireContext(), theme)


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.timeout_dialog, container, false)
        dialogMessage = view.findViewById(R.id.message)
        view.findViewById<Button>(R.id.button_signout).setOnClickListener {
            listener.triggerLogout()
            dismiss()
        }

        view.findViewById<TextView>(R.id.keep_me_in).setOnClickListener {
//            mainViewModel.resetTimer()
            dismiss()
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        isCancelable = false // Set the bottom sheet to be non-cancellable
//        mainViewModel.enableSessionDialog()
        startCountdownTimer()
    }


    private fun startCountdownTimer() {
        countDownTimer?.cancel()
        countDownTimer = object : CountDownTimer(initialTimeMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val minutes = TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)
                val seconds = TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) -
                        TimeUnit.MINUTES.toSeconds(
                            TimeUnit.MILLISECONDS.toMinutes(
                                millisUntilFinished
                            )
                        )
                val timeLeftFormatted = String.format("%02d:%02d", minutes, seconds)
                dialogMessage.text = buildString {
                    append(" ")
                    append(message)
                    append(" ")
                    append(timeLeftFormatted)
                    append(" seconds")
                }
            }

            override fun onFinish() {
//                mainViewModel.disableSessionDialog()
                listener.triggerLogout()
                dismiss()
            }
        }.start()
    }



    companion object {
        private var initialTimeMillis: Long = 30000
        var message = "Inactivity detected. You'll be automatically logged out in"
//            "You've been quiet. To protect you details, you will be logged out automatically in 30 seconds"
    }

    interface DialogClickListener {
        fun triggerLogout()

        fun keepSession()
    }


}