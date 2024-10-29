package com.example.currencyexchange.view_models

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.currencyexchange.BuildConfig
import com.example.currencyexchange.helper.SaveHelper
import com.example.currencyexchange.instances.RetrofitInstance
import com.example.currencyexchange.models.ExchangeRatesResponse
import com.example.currencyexchange.models.SymbolsResponse
import kotlinx.coroutines.launch

class CurrencyConverterViewModel : ViewModel() {
    private val apiKey = BuildConfig.API_KEY

    fun fetchExchangeRates(
        baseCurrency: String? = null,
        symbols: String? = null,
        onResult: (ExchangeRatesResponse?) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.api.getExchangeRates(apiKey, baseCurrency, symbols)
                if (response.isSuccessful) {
                    onResult(response.body())
                } else {
                    onResult(null)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                onResult(null)
            }
        }
    }

    fun fetchAllSymbols(onResult: (SymbolsResponse?) -> Unit) {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.api.getAllSymbols(apiKey)
                if (response.isSuccessful) {
                    onResult(response.body())
                } else {
                    onResult(null)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                onResult(null)
            }
        }
    }

    fun fetchHistoricalExchangeRates(
        date: String,
        baseCurrency: String? = null,
        symbols: String? = null,
        onResult: (ExchangeRatesResponse?) -> Unit
    ) {
        viewModelScope.launch {

            try {
                val response =
                    RetrofitInstance.api.getHistoricalRates(date, apiKey, baseCurrency, symbols)
                if (response.isSuccessful) {
                    onResult(response.body())
                } else {
                    onResult(null)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                onResult(null)
            }

        }
    }
}