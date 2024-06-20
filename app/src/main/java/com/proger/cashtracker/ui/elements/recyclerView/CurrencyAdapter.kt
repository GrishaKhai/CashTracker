package com.proger.cashtracker.ui.elements.recyclerView

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.proger.cashtracker.databinding.ItemCurrencyBinding
import com.proger.cashtracker.utils.DoubleHelper
import com.proger.cashtracker.models.Currency
import com.proger.cashtracker.ui.screens.currency.IUpdateBaseCurrency
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CurrencyAdapter @Inject constructor() : RecyclerView.Adapter<CurrencyAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemCurrencyBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.setData(differ.currentList[position])
    }
    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    inner class ViewHolder(private val binding: ItemCurrencyBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun setData(item: Currency) {
            binding.apply {
                val rate = DoubleHelper.roundValue(item.rateToBase)
                tvTitleCurrency.text = "${item.currencyCode} - ${item.currencyName} ($rate)"
                rbSelectedCurrency.isChecked = item.currencyCode == baseCurrency
                rbSelectedCurrency.setOnCheckedChangeListener { group, checkedId ->
                    if(checkedId) {
                        updateBaseCurrency.update(item)
                    }
                }
            }
        }
    }

    private lateinit var baseCurrency: String
    fun setBaseCurrency(baseCurrency: String) { this.baseCurrency = baseCurrency }

    private lateinit var updateBaseCurrency: IUpdateBaseCurrency
    fun setUpdateBaseCurrency(updateBaseCurrency: IUpdateBaseCurrency) { this.updateBaseCurrency = updateBaseCurrency}

    private val differCallback = object : DiffUtil.ItemCallback<Currency>() {
        override fun areItemsTheSame(oldItem: Currency, newItem: Currency): Boolean {
            return oldItem.currencyCode == newItem.currencyCode
        }

        override fun areContentsTheSame(oldItem: Currency, newItem: Currency): Boolean {
            return oldItem == newItem
        }
    }
    val differ = AsyncListDiffer(this, differCallback)
}

/*@Singleton
class CurrencyAdapter @Inject constructor() : PagingDataAdapter<Currency, CurrencyAdapter.ViewHolder>(DiffCallback) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemCurrencyBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.setData(getItem(position)!!)//differ.currentList[position])
    }
//    override fun getItemCount(): Int {
//        return differ.currentList.size
//    }

    inner class ViewHolder(private val binding: ItemCurrencyBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun setData(item: Currency) {
            binding.apply {
                val rate = DoubleHelper.roundValue(item.rateToBase)
                tvTitleCurrency.text = "${item.currencyCode} - ${item.currencyName} ($rate)"
                rbSelectedCurrency.isChecked = item.currencyCode == baseCurrency
                rbSelectedCurrency.setOnCheckedChangeListener { group, checkedId ->
                    if(checkedId) {

                        updateBaseCurrency.update(item)
                    }
                }
            }
        }
    }

    private lateinit var baseCurrency: String
    fun setBaseCurrency(baseCurrency: String) { this.baseCurrency = baseCurrency }

    private lateinit var updateBaseCurrency: IUpdateBaseCurrency
    fun setUpdateBaseCurrency(updateBaseCurrency: IUpdateBaseCurrency) { this.updateBaseCurrency = updateBaseCurrency}

//    val differ = AsyncListDiffer(this, DiffCallback)
    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<Currency>() {
            override fun areItemsTheSame(oldItem: Currency, newItem: Currency): Boolean = oldItem.currencyCode == newItem.currencyCode
            override fun areContentsTheSame(oldItem: Currency, newItem: Currency): Boolean = oldItem == newItem
        }
    }
}*/
