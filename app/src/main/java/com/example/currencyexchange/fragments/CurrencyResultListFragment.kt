package com.example.currencyexchange.fragments

import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.currencyexchange.R
import com.example.currencyexchange.databinding.FragmentCurrencyResultBinding
import com.example.currencyexchange.databinding.FragmentCurrencyResultListBinding
import com.example.currencyexchange.helper.StringHelper
import java.io.Serializable

const val ARG_RESULTS = "exchange_results"

/**
 *
 * A fragment that shows a list of items as a modal bottom sheet.
 *
 * You can show this modal bottom sheet from your activity like this:
 * <pre>
 *    CurrencyResultListDialogFragment.newInstance(30).show(supportFragmentManager, "dialog")
 * </pre>
 */
class CurrencyResultListFragment : Fragment() {

    private var _binding: FragmentCurrencyResultListBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentCurrencyResultListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val recyclerView = view.findViewById<RecyclerView>(R.id.list)
        recyclerView?.layoutManager = LinearLayoutManager(context)
        val results = arguments?.getSerializable(ARG_RESULTS) as Map<String, Double>?
        recyclerView?.adapter = results?.let { CurrencyResultAdapter(it) }
    }

    private inner class ViewHolder(binding: FragmentCurrencyResultBinding) :
        RecyclerView.ViewHolder(binding.root) {

        val textViewCurrencyResult = binding.textViewCurrencyResult
        val textViewCurrency = binding.textViewCurrency
    }

    private inner class CurrencyResultAdapter(private val results: Map<String, Double>?) :
        RecyclerView.Adapter<ViewHolder>() {


        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

            return ViewHolder(
                FragmentCurrencyResultBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )

        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val key = results?.keys?.elementAt(position)
            if (key != null) {
                Log.d("CurrencyResultAdapter", StringHelper.formatCurrency(results?.getValue(key)?: 0.0))
                holder.textViewCurrencyResult.text = StringHelper.formatCurrency(results?.getValue(key)?: 0.0)
                holder.textViewCurrency.text = key

            }
        }

        override fun getItemCount(): Int {
            if (results == null){
                return 0
            }
            return results.size
        }
    }


    fun setResults(results: Map<String, Double>?) {
        val recyclerView = view?.findViewById<RecyclerView>(R.id.list)
        recyclerView?.adapter = CurrencyResultAdapter(results)
    }

    companion object {
        fun newInstance(results: Map<String, Double>?): CurrencyResultListFragment =
            CurrencyResultListFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(ARG_RESULTS, results as Serializable)
                }
            }

    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}