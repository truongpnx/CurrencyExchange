package com.example.currencyexchange.helper

import java.math.RoundingMode
import java.text.DecimalFormat
import java.time.Instant
import java.time.LocalDate

object StringHelper {

    fun formatCurrency(amount: Double): String {
        val decimalFormat = DecimalFormat("#,##0.######")
        decimalFormat.roundingMode = RoundingMode.HALF_UP
        return decimalFormat.format(amount)
    }

    fun timestampToYYYYMMDD(timestamp: Long): LocalDate {

        return Instant.ofEpochMilli(timestamp * 1000).atZone(java.time.ZoneId.systemDefault())
            .toLocalDate()
    }

    fun currencyToDouble(currency: String): Double {
        return currency.replace(",", "").toDouble()
    }
}