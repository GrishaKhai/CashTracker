package com.proger.cashtracker.ui.elements.recyclerView

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.proger.cashtracker.databinding.ItemShortTransactionDetailsBinding
import com.proger.cashtracker.models.TransactionByCategories
import com.proger.cashtracker.ui.fragment.shortDetailsBoxes.IInvokeOpenFragmentWithNotes
import javax.inject.Inject
import javax.inject.Singleton

@Deprecated("была мысль реализовать такой способ загрузки короткой информации, но он оказался накладным со входными аргументами")
@Singleton
class ShortTransactionAdapter
@Inject constructor() : RecyclerView.Adapter<ShortTransactionAdapter.ViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemShortTransactionDetailsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.setData(shortTransaction.currentList[position])
    }

    override fun getItemCount(): Int {
        return shortTransaction.currentList.size
    }

    inner class ViewHolder(private val binding: ItemShortTransactionDetailsBinding) : RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n", "SimpleDateFormat")
        fun setData(item: TransactionByCategories) {
            binding.apply {
                val current = item.sumTransaction
                val percent = (fullSum.toFloat().let { current.toFloat().div(it) }).times(100)

                val idImage = root.context.resources.getIdentifier(
                    item.image,
                    "drawable",
                    root.context.packageName
                )
                ivCategoryIco.setImageResource(idImage)
                tvCategory.text = item.nameCategory
                tvCost.text = String.format("%.2f", current).replace(",", ".")
                tvPercent.text = String.format("%.2f", percent) + "%"

                clShortDetailBox.setOnClickListener{
                    invokeOpenFragmentWithNotes!!.invoke(item.nameCategory)
                }
            }
        }
    }

    private val differCallback = object : DiffUtil.ItemCallback<TransactionByCategories>() {
        override fun areItemsTheSame(oldItem: TransactionByCategories, newItem: TransactionByCategories): Boolean {
            return oldItem.nameCategory == newItem.nameCategory
        }

        override fun areContentsTheSame(oldItem: TransactionByCategories, newItem: TransactionByCategories): Boolean {
            return oldItem == newItem
        }
    }
    val shortTransaction = AsyncListDiffer(this, differCallback)


    fun setInvokeOpenFragmentWithNotes(invokeOpenFragmentWithNotes: IInvokeOpenFragmentWithNotes) { this.invokeOpenFragmentWithNotes = invokeOpenFragmentWithNotes }
    fun setFullSum(fullSum: Double) { this.fullSum = fullSum }

    private var invokeOpenFragmentWithNotes: IInvokeOpenFragmentWithNotes? = null
    private var fullSum: Double = 0.0
}