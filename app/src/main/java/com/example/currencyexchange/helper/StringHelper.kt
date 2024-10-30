package com.example.currencyexchange.helper

import java.math.BigDecimal
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
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

    fun timestampToYYYYMMDD(timestamp: Long): String {
        val instant = Instant.ofEpochMilli(timestamp)
        val localDateTime = LocalDateTime.ofInstant(instant, ZoneOffset.UTC)
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        return localDateTime.format(formatter)
    }

    fun currencyToDouble(currency: String): Double {
        return currency.replace(",", "").toDouble()
    }

    fun currencyToBigDecimal(currency: String): BigDecimal {
        return currency.replace(",", "").toBigDecimal()
    }

    fun convertToGMT(year: Int, month: Int, dayOfMonth: Int): String {
        // Create a LocalDateTime at the start of the day in the system default time zone
        val localDateTime = LocalDateTime.of(year, month, dayOfMonth, 23, 59)


        // Convert to ZonedDateTime in the system default time zone
        val systemZoneDateTime = localDateTime.atZone(ZoneId.systemDefault())


        // Convert to GMT (UTC) time zone
        val gmtDateTime = systemZoneDateTime.withZoneSameInstant(ZoneOffset.UTC)


        // Format to YYYY-MM-DD
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        return gmtDateTime.format(formatter)
    }
}