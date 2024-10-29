package com.example.currencyexchange.view_models

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.currencyexchange.models.ExchangeRatesResponse

class ExchangeRateResponseViewModel: ViewModel() {
    private val _exchangeRatesResponse = MutableLiveData<ExchangeRatesResponse?>()
    val exchangeRatesResponse: LiveData<ExchangeRatesResponse?> get() = _exchangeRatesResponse

    fun setExchangeRatesResponse(value: ExchangeRatesResponse?) {
        _exchangeRatesResponse.value = value
    }
}