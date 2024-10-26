package com.example.currencyexchange.instances

import com.example.currencyexchange.api.CurrencyServiceApi
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {
    private const val BASE_URL = "http://api.exchangeratesapi.io/v1/"

    val api: CurrencyServiceApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(CurrencyServiceApi::class.java)
    }
}