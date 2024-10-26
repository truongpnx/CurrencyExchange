package com.example.currencyexchange.view_models

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.currencyexchange.BuildConfig
import com.example.currencyexchange.instances.RetrofitInstance
import kotlinx.coroutines.launch

class CurrencyConverterViewModel : ViewModel() {
    private val apiKey = BuildConfig.API_KEY

    fun fetchExchangeRates(baseCurrency: String, onResult: (Map<String, Double>?) -> Unit) {
        viewModelScope.launch {
            val response = RetrofitInstance.api.getExchangeRates(apiKey, baseCurrency)
            if (response.isSuccessful) {
                onResult(response.body()?.rates);
            } else {
                onResult(null);
            }
        }
    }
}