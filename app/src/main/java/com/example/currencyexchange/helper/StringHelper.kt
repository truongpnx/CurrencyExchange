package com.example.currencyexchange.helper

import java.math.BigDecimal
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.time.Instant
import java.time.LocalDate
import java.util.Locale

object StringHelper {

    fun formatCurrency(amount: BigDecimal): String {
        val symbols = DecimalFormatSymbols(Locale.US).apply {
            decimalSeparator = '.' // Set dot as the decimal separator
            groupingSeparator = ',' // Set comma as the grouping separator
        }

        val decimalFormat = DecimalFormat("#,##0.######", symbols)
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

    fun currencyToBigDecimal(currency: String): BigDecimal {
        return currency.replace(",", "").toBigDecimal()
    }
}