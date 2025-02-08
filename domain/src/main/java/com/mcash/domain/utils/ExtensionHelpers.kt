package com.mcash.domain.utils

import android.os.Build
import androidx.annotation.RequiresApi
import java.text.DecimalFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatterBuilder
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

fun String.formatContact(): String? {
    val formatted = this.replace("\\s".toRegex(), "")
        .replace("+", "")
    return if (this.length > 9) formatted.takeLast(9) else null
}

@RequiresApi(Build.VERSION_CODES.O)
fun String.formatDateTime(): String {
    val localDateTime = LocalDateTime.parse(this)
    val dateFormatter = DateTimeFormatterBuilder()
        .parseCaseInsensitive()
        .appendPattern("MMM d, yyyy h:mm a")
        .toFormatter(Locale.ENGLISH)

    return localDateTime.format(dateFormatter)
}

@RequiresApi(Build.VERSION_CODES.O)
fun String.formatDate(): String {
    val localDateTime = LocalDateTime.parse(this)
    val dateFormatter = DateTimeFormatterBuilder()
        .parseCaseInsensitive()
        .appendPattern("MMM d, yyyy")
        .toFormatter(Locale.ENGLISH)

    return localDateTime.format(dateFormatter)
}

fun Double.formatDomainCurrency(): String {
    val format = DecimalFormat("#,###")
    format.isDecimalSeparatorAlwaysShown = false
    return "UGX ${format.format(this)}"
}
