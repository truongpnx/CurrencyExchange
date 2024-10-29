package com.example.currencyexchange

import com.example.currencyexchange.helper.StringHelper.currencyToDouble
import com.example.currencyexchange.helper.StringHelper.formatCurrency
import com.example.currencyexchange.helper.StringHelper.timestampToYYYYMMDD
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDate

class HelperTest {
    @Test
    fun testTimestampToYYYYMMDD_CorrectFormat() {
        val timestamp: Long = 1730021825
        val expectedDate = LocalDate.of(2024, 10, 27)

        val resultDate = timestampToYYYYMMDD(timestamp)

        assertEquals(expectedDate, resultDate)
    }

    @Test
    fun testTimestampToYYYYMMDD_CorrectLocalDate() {
        val timestamp: Long = 1730070783
        val expectedDate = LocalDate.of(2024, 10, 28)

        val resultDate = timestampToYYYYMMDD(timestamp)

        assertEquals(expectedDate, resultDate)
    }

    @Test
    fun testFormatCurrency_CorrectFormat() {

        val amount = 1234567.89
        val expectedFormattedAmount = "1,234,567.89"
        val formattedAmount = formatCurrency(amount)
        assertEquals(expectedFormattedAmount, formattedAmount)
    }

    @Test
    fun testCurrencyToDouble_CorrectConversion() {
        val currency = "1,234,567.89"
        val expectedAmount = 1234567.89
        val amount = currencyToDouble(currency)
        assertEquals(expectedAmount, amount, 0.0)
    }
}