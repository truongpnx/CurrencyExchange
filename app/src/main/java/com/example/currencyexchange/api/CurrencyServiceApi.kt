package com.example.currencyexchange.api

import com.example.currencyexchange.models.ExchangeRatesResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface CurrencyServiceApi {
    @GET("latest")
    suspend fun getExchangeRates(
        @Query("access_key") apiKey: String,
        @Query("base") baseCurrency: String = "EUR"
    ): Response<ExchangeRatesResponse>
}