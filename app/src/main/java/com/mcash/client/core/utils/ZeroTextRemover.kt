package com.mcash.client.core.utils

import android.text.Editable

import android.widget.EditText

import android.text.TextWatcher

class ZeroTextRemover(private val mEditText: EditText) : TextWatcher {
    override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        if (s.toString() == "0") {
            mEditText.removeTextChangedListener(this)
            mEditText.setText("")
            mEditText.addTextChangedListener(this)
        }
    }

    override fun afterTextChanged(s: Editable) {}
}