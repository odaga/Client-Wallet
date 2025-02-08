package com.mcash.client.core.utils

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import java.lang.NumberFormatException
import java.text.DecimalFormat
import java.text.ParseException

class CurrencyTextWatcher(private val editText: EditText) : TextWatcher {
    private val df: DecimalFormat = DecimalFormat("#,###.##")
    private val dfnd: DecimalFormat
    private var hasFractionalPart: Boolean

    override fun afterTextChanged(s: Editable) {
        editText.removeTextChangedListener(this)
        try {
            val inilen: Int = editText.text.length
            val v: String = s.toString().replace(
                java.lang.String.valueOf(
                    df.decimalFormatSymbols.groupingSeparator
                ), ""
            )
            val n: Number? = df.parse(v)
            val cp = editText.selectionStart
            if (hasFractionalPart) {
                editText.setText(df.format(n))
            } else {
                editText.setText(dfnd.format(n))
            }

            val endlen: Int = editText.text.length
            val sel = cp + (endlen - inilen)
            if (sel > 0 && sel <= editText.text.length) {
                editText.setSelection(sel)
            } else {
                // place cursor at the end?
                editText.setSelection(editText.text.length - 1)
            }
        } catch (nfe: NumberFormatException) {
            // do nothing?
        } catch (e: ParseException) {
            // do nothing?
        }
        editText.addTextChangedListener(this)
    }

    override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        hasFractionalPart = s.toString().contains(df.decimalFormatSymbols.decimalSeparator.toString())
    }

    companion object {
        private const val TAG = "CurrencyTextWatcher"
    }

    init {
        df.isDecimalSeparatorAlwaysShown = true
        dfnd = DecimalFormat("#,###")
        hasFractionalPart = false
    }
}