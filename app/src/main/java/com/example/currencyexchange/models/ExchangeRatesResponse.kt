package com.example.currencyexchange.models


data class ExchangeRatesResponse(
    val rates: Map<String, Double>,
    val base: String,
    val date: String,
    val timestamp: Long
)
