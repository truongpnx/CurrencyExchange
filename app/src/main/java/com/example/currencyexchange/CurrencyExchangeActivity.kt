package com.example.currencyexchange

import android.app.DatePickerDialog
import android.icu.util.Calendar
import android.icu.util.TimeZone
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.SearchView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.FragmentContainerView
import androidx.lifecycle.MutableLiveData
import com.example.currencyexchange.dialog.ChooseCurrencyListDialog
import com.example.currencyexchange.fragments.CurrencyResultListFragment
import com.example.currencyexchange.helper.NetworkHelper
import com.example.currencyexchange.helper.SaveHelper
import com.example.currencyexchange.helper.StringHelper
import com.example.currencyexchange.models.ExchangeRatesResponse
import com.example.currencyexchange.models.SymbolsResponse
import com.example.currencyexchange.view_models.CurrencyConverterViewModel
import com.example.currencyexchange.view_models.ExchangeRateResponseViewModel
import com.example.currencyexchange.view_models.SymbolsViewModel
import com.google.gson.Gson
import java.io.IOException
import java.math.BigDecimal

const val PREFERENCE_NAME = "currency_preferences"
const val SOURCE_CURRENCY = "source_currency"
const val TARGET_CURRENCY = "target_currency"

class CurrencyExchangeActivity : AppCompatActivity() {

    private val viewModel: CurrencyConverterViewModel by viewModels()
    private val exchangeRatesViewModel: ExchangeRateResponseViewModel by viewModels()
    private val symbolsViewModel: SymbolsViewModel by viewModels()

    private lateinit var currencyResultListFragment: CurrencyResultListFragment
    private lateinit var searchView: SearchView
    private lateinit var btnDate: Button
    private lateinit var btnSourceCurrency: Button
    private lateinit var btnTargetCurrency: Button

    private var sourceCurrency = MutableLiveData<String>()
    private var targetCurrency = MutableLiveData<String>()
    private var amount = MutableLiveData<BigDecimal>().apply { value = BigDecimal.ZERO }

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

        //region data binding
        exchangeRatesViewModel.exchangeRatesResponse.observe(this) {
            it?.let {
                onRatesResponseUpdate(it)
            }
        }

        symbolsViewModel.symbolsResponse.observe(this) {
            it?.let {
                onSymbolsResponseUpdate(it)
            }
        }

        amount.observe(this) {
            onUpdateAmount(it)
        }

        sourceCurrency.observe(this) {
            ("(" + it + ") " + symbolsViewModel.symbolsResponse.value?.symbols?.get(it)).also { res ->
                btnSourceCurrency.text = res
            }
            saveSourceCurrency()
            onUpdateAmount(amount.value ?: BigDecimal.ZERO)
        }

        targetCurrency.observe(this) {
            ("(" + it + ") " + symbolsViewModel.symbolsResponse.value?.symbols?.get(it)).also { res ->
                btnTargetCurrency.text = res
            }
            saveTargetCurrency()
            onUpdateAmount(amount.value ?: BigDecimal.ZERO)
        }


        //endregion

        //region search view event

        searchView = findViewById(R.id.currencySearch)
        searchView.setOnQueryTextFocusChangeListener { _, hasFocus ->
            val params = searchView.layoutParams as FrameLayout.LayoutParams
            if (hasFocus) {
                params.width = FrameLayout.LayoutParams.MATCH_PARENT
            } else {
                params.width = FrameLayout.LayoutParams.WRAP_CONTENT
            }
            searchView.layoutParams = params
        }

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                val results = ratesToResults(
                    exchangeRatesViewModel.exchangeRatesResponse.value?.rates,
                    amount.value ?: BigDecimal.ZERO,
                    sourceCurrency.value!!
                )
                updateExchangedResults(results)
                return true
            }
        })
        //endregion

        //region button event
        val btnShowCurrency = findViewById<Button>(R.id.btnShowCurrency)
        btnShowCurrency.setOnClickListener {
            val cardView = findViewById<CardView>(R.id.cv_currenciesContainer)
            if (cardView.visibility == View.VISIBLE) {
                cardView.visibility = View.GONE
                btnShowCurrency.text = resources.getString(R.string.show_currencies)
            } else {
                cardView.visibility = View.VISIBLE
                btnShowCurrency.text = resources.getString(R.string.hide_currencies)
            }
        }

        btnDate = findViewById(R.id.btnDate)
        btnDate.setOnClickListener {
            showDatePicker { year, month, day ->
                val calendar = Calendar.getInstance(TimeZone.GMT_ZONE)
                if (year == calendar.get(Calendar.YEAR) && month == calendar.get(Calendar.MONTH) && day == calendar.get(
                        Calendar.DAY_OF_MONTH
                    )
                ) {
                    getLatestRates()
                } else {
                    val date = StringHelper.convertToGMT(year, month + 1, day)
                    getDateRates(date)
                }
            }
        }

        btnSourceCurrency = findViewById(R.id.btnSourceCurrency)
        btnSourceCurrency.setOnClickListener {
            val dialog = ChooseCurrencyListDialog(
                this,
                symbolsViewModel.symbolsResponse.value?.symbols ?: emptyMap()
            )
            dialog.showDialog(object : ChooseCurrencyListDialog.OnItemSelectedListener {
                override fun onItemSelected(key: String, value: String) {
                    sourceCurrency.value = key
                }
            })
        }
        btnTargetCurrency = findViewById(R.id.btnTargetCurrency)
        btnTargetCurrency.setOnClickListener {
            val dialog = ChooseCurrencyListDialog(
                this,
                symbolsViewModel.symbolsResponse.value?.symbols ?: emptyMap()
            )
            dialog.showDialog(object : ChooseCurrencyListDialog.OnItemSelectedListener {
                override fun onItemSelected(key: String, value: String) {
                    targetCurrency.value = key
                }
            })
        }

        findViewById<ImageButton>(R.id.imBtnSwap).setOnClickListener {
            val targetString = findViewById<TextView>(R.id.resultTextView).text.toString()
            val newAmount = StringHelper.currencyToBigDecimal(targetString)
            val sourceString = StringHelper.formatCurrency(newAmount).replace(",", "")
            findViewById<EditText>(R.id.editTextAmount).setText(sourceString)

            swapCurrencies()
            amount.value = newAmount
        }
        //endregion

        //region edit text
        val editTextAmount = findViewById<EditText>(R.id.editTextAmount)
        editTextAmount.addTextChangedListener(object : TextWatcher {
            private var isFormatting: Boolean = false
            private var current = ""

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val parsed: BigDecimal = if (s.toString().isEmpty() || s.toString() == ".") {
                    BigDecimal.ZERO
                } else {
                    StringHelper.currencyToBigDecimal(s.toString())
                }
                amount.value = parsed
            }

            override fun afterTextChanged(s: Editable?) {
                if (isFormatting) return

                val input = s.toString()
                if (input == current) return // Ignore if the same

                isFormatting = true

                if (input.endsWith(".") && input.count { it == '.' } == 1) {
                    current = input
                    isFormatting = false
                    return
                }

                // Remove non-numeric characters except for '.'
                val cleanString = input.replace("[^\\d.]".toRegex(), "")
                val parsed: BigDecimal = if (cleanString.isEmpty()) {
                    BigDecimal.ZERO
                } else {
                    StringHelper.currencyToBigDecimal(cleanString)
                }
                Log.d("CurrencyExchangeActivity", "parsed: $parsed")


                val formatted = StringHelper.formatCurrency(parsed)
                Log.d("CurrencyExchangeActivity", "formatted: $formatted")


                current = formatted

                // Set the formatted text and maintain cursor position
                editTextAmount.setText(formatted)
                editTextAmount.setSelection(formatted.length) // Move cursor to the end
                isFormatting = false
            }

        })
        //endregion


    }

    override fun onStart() {
        super.onStart()
        sourceCurrency.value = getSavedSourceCurrency()
        targetCurrency.value = getSavedTargetCurrency()

        getStoredRates()
        getStoredSymbols()

        if (!NetworkHelper.isNetworkConnected(this)) {
            Toast.makeText(
                this, resources.getString(R.string.turn_on_internet_notify), Toast.LENGTH_LONG
            ).show()
            return
        }

        try {
            getSymbols()
            getLatestRates()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    private fun getStoredRates() {
        try {
            Log.d("CurrencyExchangeActivity", "get stored rates")
            var json = SaveHelper.loadResponseFromFile(this, "exchange_rates.json")

            if (json == null) {
                Log.d("CurrencyExchangeActivity", "get default rates")
                json = this.assets.open("default_rates.json").bufferedReader().use { it.readText() }
            }
            exchangeRatesViewModel.setExchangeRatesResponse(
                Gson().fromJson(json, ExchangeRatesResponse::class.java)
            )
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun getStoredSymbols() {
        try {
            Log.d("CurrencyExchangeActivity", "get stored symbols")
            var json = SaveHelper.loadResponseFromFile(this, "symbols.json")

            if (json == null) {
                Log.d("CurrencyExchangeActivity", "get default symbols")
                json =
                    this.assets.open("default_symbols.json").bufferedReader().use { it.readText() }
            }
            symbolsViewModel.setSymbolsResponse(
                Gson().fromJson(json, SymbolsResponse::class.java)
            )
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun getLatestRates() {
        try {
            Log.d("CurrencyExchangeActivity", "get latest rates")

            viewModel.fetchExchangeRates {
                if (it == null) {
                    Toast.makeText(
                        this,
                        resources.getString(R.string.fetch_exchange_rates_failed_notify),
                        Toast.LENGTH_SHORT
                    ).show()
                    getStoredRates()
                } else {
                    SaveHelper.saveResponseToFile(this, "exchange_rates.json", Gson().toJson(it))
                    exchangeRatesViewModel.setExchangeRatesResponse(it)
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
                if (it == null) {
//                    Toast.makeText(this, "Failed to fetch symbols", Toast.LENGTH_SHORT).show()
                    getStoredSymbols()
                } else {
                    SaveHelper.saveResponseToFile(this, "symbols.json", Gson().toJson(it))
                    symbolsViewModel.setSymbolsResponse(it)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun getDateRates(date: String) {
        viewModel.fetchHistoricalExchangeRates(date) {
            if (it == null) {
                Toast.makeText(
                    this,
                    resources.getString(R.string.fetch_exchange_rates_failed_notify),
                    Toast.LENGTH_SHORT
                ).show()
                getStoredRates()
            } else {
                exchangeRatesViewModel.setExchangeRatesResponse(it)
            }
        }
    }

    private fun updateExchangedResults(results: Map<String, BigDecimal>?) {
        val resultsFiltered =
            filterResults(
                results,
                sourceCurrency.value!!,
                targetCurrency.value!!,
                searchView.query.toString()
            )
        currencyResultListFragment.setResults(resultsFiltered)
    }

    private fun onRatesResponseUpdate(response: ExchangeRatesResponse) {
        btnDate.text = StringHelper.timestampToYYYYMMDD(response.timestamp * 1000)
        val results = ratesToResults(response.rates, amount.value!!, sourceCurrency.value!!)
        updateExchangedResults(results)
        findViewById<TextView>(R.id.resultTextView).text = StringHelper.formatCurrency(results?.get(targetCurrency.value) ?: BigDecimal.ZERO)
    }

    private fun onUpdateAmount(amount: BigDecimal) {
        val results = ratesToResults(
            exchangeRatesViewModel.exchangeRatesResponse.value?.rates,
            amount,
            sourceCurrency.value!!
        )
        updateExchangedResults(results)
        if (results != null) {
            findViewById<TextView>(R.id.resultTextView).text =
                StringHelper.formatCurrency(results[targetCurrency.value] ?: BigDecimal.ZERO)
        }
    }

    private fun ratesToResults(
        rates: Map<String, Double>?, amount: BigDecimal, fromCurrency: String
    ): Map<String, BigDecimal>? {
        if (rates == null) {
            return null
        }
        val results = mutableMapOf<String, BigDecimal>()
        rates.forEach { (key, value) ->
            run {
                results[key] = BigDecimal(value / rates[fromCurrency]!!) * amount
            }
        }
        return results
    }

    private fun onSymbolsResponseUpdate(response: SymbolsResponse) {
        "(${sourceCurrency.value}) ${response.symbols[sourceCurrency.value]}".also {
            findViewById<Button>(R.id.btnSourceCurrency).text = it
        }
        "(${targetCurrency.value}) ${response.symbols[targetCurrency.value]}".also {
            findViewById<Button>(R.id.btnTargetCurrency).text = it
        }
        val results = ratesToResults(
            exchangeRatesViewModel.exchangeRatesResponse.value?.rates,
            amount.value ?: BigDecimal.ZERO,
            sourceCurrency.value!!
        )
        updateExchangedResults(results)
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
        results: Map<String, BigDecimal>?,
        fromCurrency: String,
        targetCurrency: String,
        keyword: String = ""
    ): Map<String, BigDecimal>? {
        return results?.filter { (key, _) ->
            run {
                key.contains(keyword.uppercase()) && key != fromCurrency && key != targetCurrency
            }
        }
    }

    private fun showDatePicker(onDateSelected: (year: Int, month: Int, day: Int) -> Unit) {
        val calendar = Calendar.getInstance(TimeZone.GMT_ZONE)
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog =
            DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
                onDateSelected(selectedYear, selectedMonth, selectedDay)
            }, year, month, day)

        datePickerDialog.datePicker.maxDate = calendar.timeInMillis

        datePickerDialog.show()
    }


    private fun swapCurrencies() {
        val temp = sourceCurrency.value
        sourceCurrency.value = targetCurrency.value
        targetCurrency.value = temp!!
    }

    private fun saveSourceCurrency() {
        val sharedPreferences = getSharedPreferences(PREFERENCE_NAME, MODE_PRIVATE)
        sharedPreferences.edit().putString(SOURCE_CURRENCY, sourceCurrency.value).apply()
    }

    private fun saveTargetCurrency() {
        val sharedPreferences = getSharedPreferences(PREFERENCE_NAME, MODE_PRIVATE)
        sharedPreferences.edit().putString(TARGET_CURRENCY, targetCurrency.value).apply()
    }
}