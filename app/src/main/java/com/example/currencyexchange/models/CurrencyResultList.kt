package com.example.currencyexchange.models

import java.io.Serializable

class CurrencyResultList: Serializable {
    private var resultList: Map<Double, String>  = mutableMapOf();

    // Getter for resultList
    fun getResultList(): Map<Double, String> {
        return resultList
    }

    // Setter for resultList
    fun setResultList(resultList: Map<Double, String>) {
        this.resultList = resultList
    }
}