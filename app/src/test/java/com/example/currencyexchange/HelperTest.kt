package com.example.currencyexchange

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
}