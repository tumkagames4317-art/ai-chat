package com.ai.frankenstein.core.utils

import android.content.Context
import android.content.res.Resources
import android.util.TypedValue
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.sp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// Context extensions
fun Context.showToast(message: String, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, message, duration).show()
}

fun Context.dpToPx(dp: Dp): Int {
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        dp.value,
        resources.displayMetrics
    ).toInt()
}

fun Context.pxToDp(px: Int): Dp {
    return (px / resources.displayMetrics.density).sp
}

// Date formatting
fun Date.formatDateTime(): String {
    val format = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
    return format.format(this)
}

fun Date.formatDate(): String {
    val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    return format.format(this)
}

fun Date.formatTime(): String {
    val format = SimpleDateFormat("HH:mm", Locale.getDefault())
    return format.format(this)
}

fun Date.isToday(): Boolean {
    val today = Date()
    val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    return format.format(this) == format.format(today)
}

fun Date.isYesterday(): Boolean {
    val yesterday = Date(System.currentTimeMillis() - 86400000)
    val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    return format.format(this) == format.format(yesterday)
}

// String extensions
fun String.capitalizeFirstLetter(): String {
    return this.replaceFirstChar {
        if (it.isLowerCase()) it.titlecase(Locale.getDefault())
        else it.toString()
    }
}

fun String.limit(length: Int): String {
    return if (this.length <= length) this else this.take(length) + "..."
}

fun String.isValidEmail(): Boolean {
    val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\$"
    return this.matches(emailRegex.toRegex())
}

fun String.isValidUrl(): Boolean {
    val urlRegex = "^(https?|ftp)://[^\\s/$.?#].[^\\s]*\\$"
    return this.matches(urlRegex.toRegex())
}

// Number formatting
fun Double.formatCurrency(symbol: String = "$"): String {
    return String.format(Locale.getDefault(), "%s%.2f", symbol, this)
}

fun Long.formatNumber(): String {
    return String.format(Locale.getDefault(), "%,d", this)
}

fun Int.formatNumber(): String {
    return String.format(Locale.getDefault(), "%,d", this)
}

// Compose extensions
@Composable
fun toast(message: String) {
    val context = LocalContext.current
    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
}

@Composable
fun longToast(message: String) {
    val context = LocalContext.current
    Toast.makeText(context, message, Toast.LENGTH_LONG).show()
}

// Resource extensions
fun Resources.getColorFromAttr(attr: Int): Int {
    val typedValue = TypedValue()
    this.getValue(attr, typedValue, true)
    return typedValue.data
}

// List extensions
fun <T> List<T>.second(): T? {
    return if (this.size >= 2) this[1] else null
}

fun <T> List<T>.third(): T? {
    return if (this.size >= 3) this[2] else null
}

fun <T> List<T>.lastOrNull(): T? {
    return if (this.isNotEmpty()) this.last() else null
}

// Map extensions
fun <K, V> Map<K, V>.getOrDefault(key: K, defaultValue: V): V {
    return this[key] ?: defaultValue
}

// Collection extensions
fun <T> Collection<T>.joinToStringWithComma(): String {
    return this.joinToString(", ")
}

fun <T> Collection<T>.joinToStringWithNewline(): String {
    return this.joinToString("\n")
}

// Boolean extensions
fun Boolean.toInt(): Int {
    return if (this) 1 else 0
}

// Int extensions
fun Int.toBoolean(): Boolean {
    return this != 0
}

// Token cost calculation
fun Int.calculateCost(inputCost: Double, outputCost: Double): Double {
    return this * inputCost
}

fun Pair<Int, Int>.calculateCost(inputCost: Double, outputCost: Double): Double {
    return this.first * inputCost + this.second * outputCost
}
