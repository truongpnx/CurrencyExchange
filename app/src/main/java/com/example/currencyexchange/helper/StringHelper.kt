package com.example.currencyexchange.helper

import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.Locale

object StringHelper {

    fun formatCurrency(amount: Double): String {
        val numberFormat = NumberFormat.getCurrencyInstance(Locale.US)
        return numberFormat.format(amount).replace("$", "")
    }
}