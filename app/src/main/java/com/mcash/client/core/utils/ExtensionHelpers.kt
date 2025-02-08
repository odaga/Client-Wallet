package com.mcash.client.core.utils

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.os.Build
import android.text.Html
import android.text.Spanned
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.text.HtmlCompat
import java.text.DecimalFormat
import java.text.NumberFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

/**
 * An extension method that extracts time in AM or PM from a provide datetime string
 * @return formatted time string
 */
fun String.toAmPmTime(): String? {
    val tokenizer = StringTokenizer(this)
    val date = tokenizer.nextToken()
    val time = tokenizer.nextToken()

    val sdf = SimpleDateFormat("KK:mm:ss", Locale.getDefault())
    val sdfs = SimpleDateFormat("hh:mm a", Locale.getDefault())

    return try {
        val m = sdf.parse(time)
        sdfs.format(m!!)
    } catch (e: ParseException) {
        e.printStackTrace()
        null
    }
}

fun String.formatCurrency(): String {
    val format: NumberFormat = NumberFormat.getCurrencyInstance()
    format.maximumFractionDigits = 0
    format.currency = Currency.getInstance("UGX")

    return format.format(this.toInt())
}

fun String.formatContact(): String? {
    val formatted = this.replace("\\s".toRegex(), "")
        .replace("+", "")
    return if (this.length > 9) formatted.takeLast(9) else null
}

fun Double.formatCurrency(): String {
    val format = DecimalFormat("#,###")
    format.isDecimalSeparatorAlwaysShown = false
    return format.format(this).toString()
}

fun Activity.removeFocus() {
    val imm = this.getSystemService(Context.INPUT_METHOD_SERVICE)
            as InputMethodManager
    imm.hideSoftInputFromWindow(this.currentFocus?.windowToken, 0)
}

fun EditText.showKeyboard() {
    requestFocus()
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as
            InputMethodManager
    imm.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
}

fun getRandomText(): String {
    val random = Random()
    val num = random.nextInt(1000000000)
    return "MCASH$num"
}

@RequiresApi(Build.VERSION_CODES.N)
fun String.formatHtml(): Spanned = Html.fromHtml(this, HtmlCompat.FROM_HTML_MODE_LEGACY)

fun Context.hasPermission(permission: String): Boolean {
    return ActivityCompat.checkSelfPermission(this, permission) ==
            PackageManager.PERMISSION_GRANTED
}

fun Activity.requestPermissionWithRationale(
    permission: String,
    requestCode: Int,
    rationaleStr: String
) {
    val provideRationale = ActivityCompat.shouldShowRequestPermissionRationale(this, permission)

    if (provideRationale) {
        AlertDialog.Builder(this).apply {
            setTitle("Permission")
            setMessage(rationaleStr)
            setPositiveButton("Ok") { _, _ ->
                ActivityCompat.requestPermissions(
                    this@requestPermissionWithRationale,
                    arrayOf(permission),
                    requestCode
                )
            }
            create()
            show()
        }
    } else {
        ActivityCompat.requestPermissions(this, arrayOf(permission), requestCode)
    }
}

fun Context.isOnline(): Boolean {
    return try {
        val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val netInfo = cm.activeNetworkInfo
        //should check null because in airplane mode it will be null
        netInfo != null && netInfo.isConnected
    } catch (e: NullPointerException) {
        e.printStackTrace()
        false
    }
}

fun String.isMtnNumber(): Boolean {
    val code = this.take(5)
    return when {
        code.contains("76") -> true
        code.contains("77") -> true
        code.contains("78") -> true
        else -> false
    }
}

fun String.isAirtelNumber(): Boolean {
    val code = this.take(5)
    return when {
        code.contains("70") -> true
        code.contains("75") -> true
        code.contains("74") -> true
        code.contains("72") -> true
        else -> false
    }
}

fun Int.getNwscFees(): Int {
    return when {
        this <= 2500 -> 190
        this <= 5000 -> 330
        this <= 15000 -> 1000
        this <= 30000 -> 1600
        this <= 45000 -> 2000
        this <= 60000 -> 2600
        this <= 125000 -> 3500
        this <= 250000 -> 4000
        this <= 500000 -> 5050
        this <= 1000000 -> 6500
        this <= 2000000 -> 6500
        this <= 4000000 -> 7000
        else -> 7000
    }
}