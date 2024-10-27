package com.example.currencyexchange

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.FragmentContainerView
import androidx.lifecycle.viewModelScope
import com.example.currencyexchange.fragments.CurrencyResultListFragment
import com.example.currencyexchange.helper.SaveHelper
import com.example.currencyexchange.models.ExchangeRatesResponse
import com.example.currencyexchange.models.SymbolsResponse
import com.example.currencyexchange.view_models.CurrencyConverterViewModel
import com.google.gson.Gson
import kotlinx.coroutines.launch
import java.io.IOException

const val PREFERENCE_NAME = "currency_preferences"
const val SOURCE_CURRENCY = "source_currency"
const val TARGET_CURRENCY = "target_currency"

class CurrencyExchangeActivity : AppCompatActivity() {


    private val viewModel: CurrencyConverterViewModel by viewModels()
    private lateinit var currencyResultListFragment: CurrencyResultListFragment
    private var rates: Map<String, Double>? = null
    private var symbols: Map<String, String>? = null
    private lateinit var sourceCurrency: String
    private lateinit var targetCurrency: String
    private val amount: Double = 1.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_currency_exchange)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        currencyResultListFragment =
            findViewById<FragmentContainerView>(R.id.fragmentContainerView).getFragment()

        sourceCurrency = getSavedSourceCurrency()
        targetCurrency = getSavedTargetCurrency()

        viewModel.viewModelScope.launch {
            try {
                getSymbols()
                getLatestRates()
                if (symbols == null) {
                    symbols = getStoredSymbols()?.symbols
                }

                if (rates == null) {
                    rates = getStoredRates()?.rates
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

//            SaveHelper.saveResponseToFile(this, "exchange_rates.json", Gson().toJson(rates))
    }

    override fun onStart() {
        super.onStart()
        Log.d("CurrencyExchangeActivity", rates.toString())
        updateResults(ratesToResults(rates, amount, sourceCurrency)!!)
    }

    private fun getStoredRates(): ExchangeRatesResponse? {
        try {
            Log.d("CurrencyExchangeActivity", "get stored rates")
            var json = SaveHelper.loadResponseFromFile(this, "exchange_rates.json")

            if (json != null) {
                return Gson().fromJson(json, ExchangeRatesResponse::class.java)
            }
            Log.d("CurrencyExchangeActivity", "get default rates")
            json = this.assets.open("default_rates.json").bufferedReader().use { it.readText() }
            return Gson().fromJson(json, ExchangeRatesResponse::class.java)
        } catch (e: IOException) {
            e.printStackTrace()
            return null
        }
    }

    private fun getStoredSymbols(): SymbolsResponse? {
        try {
            Log.d("CurrencyExchangeActivity", "get stored symbols")
            var json = SaveHelper.loadResponseFromFile(this, "symbols.json")

            if (json != null) {
                return Gson().fromJson(json, SymbolsResponse::class.java)
            }
            Log.d("CurrencyExchangeActivity", "get default symbols")

            json = this.assets.open("default_symbols.json").bufferedReader().use { it.readText() }
            return Gson().fromJson(json, SymbolsResponse::class.java)
        } catch (e: IOException) {
            e.printStackTrace()
            return null
        }
    }

    private fun getLatestRates() {
        try {
            Log.d("CurrencyExchangeActivity", "get latest rates")

            viewModel.fetchExchangeRates {
                rates = it
                if (it == null) {
                    Toast.makeText(this, "Failed to fetch rates", Toast.LENGTH_SHORT).show()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private fun getSymbols() {
        try {
            Log.d("CurrencyExchangeActivity", "get symbols")
            viewModel.fetchAllSymbols {
                symbols = it
                if (it == null) {
                    Toast.makeText(this, "Failed to fetch symbols", Toast.LENGTH_SHORT).show()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getDateRates(date: String) {
        viewModel.fetchHistoricalExchangeRates(date) {
            if (it == null) {
                Toast.makeText(this, "Failed to fetch rates", Toast.LENGTH_SHORT).show()
                return@fetchHistoricalExchangeRates
            }
            rates = it
            updateResults(ratesToResults(rates, amount, sourceCurrency)!!)
        }
    }

    private fun updateResults(results: Map<String, Double>) {
        currencyResultListFragment.setResults(results)
    }

    private fun ratesToResults(
        rates: Map<String, Double>?,
        amount: Double,
        fromCurrency: String
    ): Map<String, Double>? {
        if (rates == null) {
            return null
        }
        val results = mutableMapOf<String, Double>()
        rates.forEach { (key, value) ->
            run {
                results[key] = (value / rates[fromCurrency]!!) * amount
            }
        }
        return results
    }

    fun swapCurrencies() {
        val temp = sourceCurrency
        sourceCurrency = targetCurrency
        targetCurrency = temp
    }

    fun changeFromCurrency(currency: String) {
        sourceCurrency = currency
    }

    fun changeTargetCurrency(currency: String) {
        targetCurrency = currency
    }

    private fun getSavedSourceCurrency(): String {
        val sharedPreferences = getSharedPreferences(PREFERENCE_NAME, MODE_PRIVATE)
        return sharedPreferences.getString(SOURCE_CURRENCY, "EUR") ?: "EUR"
    }

    private fun getSavedTargetCurrency(): String {
        val sharedPreferences = getSharedPreferences(PREFERENCE_NAME, MODE_PRIVATE)
        return sharedPreferences.getString(TARGET_CURRENCY, "VND") ?: "VND"
    }

    private fun filterResults(
        results: Map<String, Double>?,
        fromCurrency: String,
        targetCurrency: String,
        keyword: String = ""
    ): Map<String, Double>? {
        return results?.filter { (key, _) ->
            run {
                key.contains(keyword) && key != fromCurrency && key != targetCurrency
            }
        }
    }

}