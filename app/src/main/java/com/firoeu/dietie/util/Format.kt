package com.firoeu.dietie.util

import android.icu.text.DateFormat
import java.text.NumberFormat
import java.util.Date
import java.util.Locale
import kotlin.math.roundToLong

private val faLocale = Locale("fa", "IR")

/** Format a number with Persian digits, rounded like the original app. */
fun fa(n: Double): String = NumberFormat.getIntegerInstance(faLocale).format(n.roundToLong())

fun fa(n: Int): String = NumberFormat.getIntegerInstance(faLocale).format(n.toLong())

/** Format a decimal number (e.g. activity factor 1.3 -> \u06F1\u066B\u06F3) with Persian digits. */
fun faDecimal(n: Double): String = NumberFormat.getNumberInstance(faLocale).format(n)

/** Persian (Jalali) date + time, similar to toLocaleDateString("fa-IR"). */
fun formatFaDateTime(ts: Long): String {
    val df = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT, faLocale)
    return df.format(Date(ts))
}

/**
 * Parse user numeric input that may contain Persian or Arabic digits
 * and Persian decimal separators.
 */
fun String.toEnglishDouble(): Double? {
    if (isBlank()) return null
    val sb = StringBuilder()
    for (c in trim()) {
        val mapped: Char = when (c) {
            in '\u06F0'..'\u06F9' -> '0' + (c - '\u06F0') // Persian digits
            in '\u0660'..'\u0669' -> '0' + (c - '\u0660') // Arabic digits
            '\u066B', ',', '\u060C' -> '.' // decimal separators
            else -> c
        }
        sb.append(mapped)
    }
    return sb.toString().toDoubleOrNull()
}
