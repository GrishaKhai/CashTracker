package com.proger.cashtracker.ui.elements.recyclerView

import android.annotation.SuppressLint
import android.app.ActionBar
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.proger.cashtracker.R
import com.proger.cashtracker.databinding.ItemTransactionDetailsBinding
import com.proger.cashtracker.models.Transaction
import com.proger.cashtracker.ui.screens.tabs.IDuplicateNote
import java.text.SimpleDateFormat
import java.util.Date
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class TransactionAdapter @Inject constructor() : RecyclerView.Adapter<TransactionAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemTransactionDetailsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.setData(differ.currentList[position])
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    inner class ViewHolder(private val binding: ItemTransactionDetailsBinding) : RecyclerView.ViewHolder(binding.root) {
        private var isWrapped: Boolean = false
        @SuppressLint("SetTextI18n", "SimpleDateFormat")
        fun setData(item: Transaction) {
            binding.apply {
                val idImage = root.context.resources.getIdentifier(
                    item.category!!.image,
                    "drawable",
                    root.context.packageName
                )
                ivCategoryIco.setImageResource(idImage)

                tvAccount.text = item.account!!.name
                tvCategory.text = item.category!!.nameCategory
                tvDateCashDetailsBox.text = SimpleDateFormat("dd MMMM yyyy").format(Date(item.date!!))
                tvCost.text = item.value.toString() + " " + item.account!!.currency!!.currencyCode
                tvDetails.text = item.comment

                tvDetails.layoutParams?.height = 0
                cvTransactionDetailBox.setOnClickListener { onClick(tvDetails) }

                cvTransactionDetailBox.setOnLongClickListener {
                    showPopupMenu(it, item)
                    true}
            }
        }

        private fun onClick(textView: TextView) {
            if (textView.text.isNotEmpty()) {
                val params = textView.layoutParams
                val height = if (isWrapped) 0 else ActionBar.LayoutParams.WRAP_CONTENT
                params?.height = height
                textView.setLayoutParams(params)
                isWrapped = !isWrapped
            }
        }

        private fun showPopupMenu(v: View, note: Transaction) {
            val popupMenu = PopupMenu(binding.root.context, v)
            popupMenu.inflate(R.menu.popup_duplicate_menu)
            popupMenu
                .setOnMenuItemClickListener { item ->
                    when (item.itemId) {
                        R.id.duplicate -> {
                            duplicator?.duplicate(note)
                            true
                        }
                        else -> false
                    }
                }
            popupMenu.show()
        }
    }

    private var duplicator: IDuplicateNote<Transaction>? = null
    fun setDuplicate(duplicate: IDuplicateNote<Transaction>) {
        this.duplicator = duplicate
    }

    private val differCallback = object : DiffUtil.ItemCallback<Transaction>() {
        override fun areItemsTheSame(oldItem: Transaction, newItem: Transaction): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Transaction, newItem: Transaction): Boolean {
            return oldItem == newItem
        }
    }
    val differ = AsyncListDiffer(this, differCallback)
}