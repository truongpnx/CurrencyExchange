package com.example.currencyexchange.view_models

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.currencyexchange.BuildConfig
import com.example.currencyexchange.helper.SaveHelper
import com.example.currencyexchange.instances.RetrofitInstance
import kotlinx.coroutines.launch

class CurrencyConverterViewModel : ViewModel() {
    private val apiKey = BuildConfig.API_KEY

    fun fetchExchangeRates(
        baseCurrency: String? = null,
        symbols: String? = null,
        onResult: (Map<String, Double>?) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.api.getExchangeRates(apiKey, baseCurrency, symbols)
                if (response.isSuccessful) {
                    onResult(response.body()?.rates)
                } else {
                    onResult(null)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                onResult(null)
            }
        }
    }

    fun fetchAllSymbols(onResult: (Map<String, String>?) -> Unit) {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.api.getAllSymbols(apiKey)
                if (response.isSuccessful) {
                    onResult(response.body()?.symbols)
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
        onResult: (Map<String, Double>?) -> Unit
    ) {
        viewModelScope.launch {

            try {
                val response =
                    RetrofitInstance.api.getHistoricalRates(date, apiKey, baseCurrency, symbols)
                if (response.isSuccessful) {
                    onResult(response.body()?.rates)
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