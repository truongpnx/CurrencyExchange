package com.example.currencyexchange.dialog

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.SearchView.OnQueryTextListener
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.currencyexchange.R

class ChooseCurrencyListDialog(
    private var context: Context,
    currencies: Map<String, String>,
) {
    private var originalCurrencies: Map<String, String> = currencies
    private var filteredCurrencies: MutableList<Pair<String, String>> =
        currencies.map { it.key to it.value }.toMutableList()
    private lateinit var adapter: ChooseCurrencyListAdapter

    fun showDialog(listener: OnItemSelectedListener) {
        val dialog = Dialog(context)
        val dialogView: View = LayoutInflater.from(context).inflate(R.layout.dialog_choose_currency, null)
        dialog.setContentView(dialogView)

        val searchView: SearchView = dialogView.findViewById(R.id.svCurrencySearch)
        val rcvCurrencyList: RecyclerView = dialogView.findViewById(R.id.rcvCurrencyList)

        rcvCurrencyList.layoutManager = LinearLayoutManager(context)
        rcvCurrencyList.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))

        adapter = ChooseCurrencyListAdapter(filteredCurrencies.toMap(), object : OnItemSelectedListener {
            override fun onItemSelected(key: String, value: String) {
                listener.onItemSelected(key, value)
                dialog.dismiss()
            }
        })

        rcvCurrencyList.adapter = adapter

        searchView.setOnQueryTextListener(object: OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filter(newText?: "")
                return true
            }

        })
        dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        dialog.show()

    }

    @SuppressLint("NotifyDataSetChanged")
    fun filter(keyword: String) {
        filteredCurrencies.clear()
        if (keyword.isEmpty()) {
            filteredCurrencies.addAll(originalCurrencies.map { it.key to it.value })
        } else {
            filteredCurrencies.addAll(originalCurrencies.filter {
                it.key.contains(keyword, ignoreCase = true) ||
                        it.value.contains(keyword, ignoreCase = true)
            }.map { it.key to it.value })
        }
        adapter.updateData(filteredCurrencies)
    }

    interface OnItemSelectedListener {
        fun onItemSelected(key: String, value: String)
    }

}