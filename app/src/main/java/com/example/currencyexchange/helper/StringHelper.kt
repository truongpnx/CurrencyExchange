package com.example.currencyexchange.helper

import java.text.NumberFormat
import java.time.Instant
import java.time.LocalDate
import java.util.Locale
import kotlin.math.floor

object StringHelper {

    fun formatCurrency(amount: Double): String {
        val numberFormat = NumberFormat.getCurrencyInstance(Locale.US)
        return numberFormat.format(amount).replace("$", "")
    }

    fun timestampToYYYYMMDD(timestamp: Long): LocalDate {

        return Instant.ofEpochMilli(timestamp * 1000).atZone(java.time.ZoneId.systemDefault())
            .toLocalDate()
    }

    fun currencyToDouble(currency: String): Double {
        return currency.replace(",", "").toDouble()
    }
}