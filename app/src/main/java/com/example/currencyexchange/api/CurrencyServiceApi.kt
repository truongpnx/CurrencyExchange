package com.example.currencyexchange.api

import com.example.currencyexchange.models.ExchangeRatesResponse
import com.example.currencyexchange.models.SymbolsResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface CurrencyServiceApi {
    @GET("latest")
    suspend fun getExchangeRates(
        @Query("access_key") apiKey: String,
        @Query("base") baseCurrency: String? = null,
        @Query("symbols") symbols: String? = null
    ): Response<ExchangeRatesResponse>

    @GET("symbols")
    suspend fun getAllSymbols(
        @Query("access_key") apiKey: String
    ): Response<SymbolsResponse>

    @GET("{date}")
    suspend fun getHistoricalRates(
        @Path("date") date: String,
        @Query("access_key") apiKey: String,
        @Query("base") baseCurrency: String? = null,
        @Query("symbols") symbols: String? = null
    ): Response<ExchangeRatesResponse>
}