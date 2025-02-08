package com.mcash.client.presentation.ui.auth

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.mcash.client.R

class InvalidSessionDialog(
    private val listener: IdleSessionDialog.DialogClickListener
) : BottomSheetDialogFragment() {
    private lateinit var dialogMessage: TextView

    override fun getTheme(): Int = R.style.BottomSheetDialogTheme

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
        BottomSheetDialog(requireContext(), theme)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.invalid_session_dialog, container, false)
        dialogMessage = view.findViewById(R.id.message)
        view.findViewById<Button>(R.id.button_signout).setOnClickListener {
            listener.triggerLogout()
            dismiss()
        }

        dialogMessage.text = message
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        isCancelable = false // Set the bottom sheet to be non-cancellable
    }

    companion object {
        var message = "Your Session has timeout. Login again to continue using the app!"
    }
}