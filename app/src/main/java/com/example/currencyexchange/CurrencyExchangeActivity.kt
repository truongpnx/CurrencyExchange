package com.example.currencyexchange

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.currencyexchange.view_models.CurrencyConverterViewModel

class CurrencyExchangeActivity : AppCompatActivity() {

    private val viewModel: CurrencyConverterViewModel by viewModels()
    private var latestRates: Map<String, Double>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_currency_exchange)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        viewModel.fetchExchangeRates("EUR") { rates ->
            latestRates = rates
            if (rates != null) {
                val rateToVND = rates["VND"] ?: 0.0
                findViewById<TextView>(R.id.resultTextView).text = "1 EUR = ${rateToVND} VND"
            } else {
                Toast.makeText(this, "Failed to fetch rates", Toast.LENGTH_SHORT).show()
            }
        }

        findViewById<Button>(R.id.convertButton).setOnClickListener {
            val amount = findViewById<EditText>(R.id.editTextAmount).text.toString().toDoubleOrNull()
            if (amount != null) {
                val rateToEur = latestRates?.get("VND") ?: 0.0
                val convertedAmount = amount * rateToEur
                findViewById<TextView>(R.id.resultTextView).text = "${amount} EUR = ${convertedAmount} VND"
            } else {
                Toast.makeText(this, "Enter a valid amount", Toast.LENGTH_SHORT).show()
            }
        }

    }
}