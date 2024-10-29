package com.example.currencyexchange.view_models

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.currencyexchange.models.SymbolsResponse

class SymbolsViewModel: ViewModel() {
    private val _symbolsResponse = MutableLiveData<SymbolsResponse?>()
    val symbolsResponse: LiveData<SymbolsResponse?> get() = _symbolsResponse

    fun setSymbolsResponse(value: SymbolsResponse?) {
        _symbolsResponse.value = value
    }
}