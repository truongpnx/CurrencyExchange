package com.example.currencyexchange.dialog

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.currencyexchange.databinding.DialogChooseCurrencyItemBinding

class ChooseCurrencyListAdapter(private var currencies: Map<String, String>?, private val listener: ChooseCurrencyListDialog.OnItemSelectedListener) :
    RecyclerView.Adapter<ChooseCurrencyListAdapter.ViewHolder>() {

    inner class ViewHolder(binding: DialogChooseCurrencyItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val tvCurrenciesName = binding.tvCurrenciesName
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        return ViewHolder(
            DialogChooseCurrencyItemBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun getItemCount(): Int {
        if (currencies == null) {
            return 0
        }
        return currencies!!.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val key = currencies?.keys?.elementAt(position)
        holder.tvCurrenciesName.text = "($key) ${currencies?.get(key)}"
        holder.itemView.setOnClickListener {
            listener.onItemSelected(key?: "", currencies?.get(key)?: "")
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateData(newData: MutableList<Pair<String, String>>) {
        currencies = newData.toMap()
        notifyDataSetChanged()
    }
}