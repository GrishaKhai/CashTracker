package com.proger.cashtracker.ui.elements.recyclerView

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.proger.cashtracker.databinding.ItemAccountBinding
import com.proger.cashtracker.models.Account
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class AccountAdapter @Inject constructor() : RecyclerView.Adapter<AccountAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemAccountBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.setData(differ.currentList[position])
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    inner class ViewHolder(private val binding: ItemAccountBinding) : RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n", "SimpleDateFormat")
        fun setData(item: Account) {
            binding.apply {
                val idImage = root.context.resources.getIdentifier(
                    item.image,
                    "drawable",
                    root.context.packageName
                )
                ivIcon.setImageResource(idImage)

                tvNameAccount.text = item.name
                tvBalance.text = tvBalance.tag.toString() + " " + String.format("%.2f", item.balance).replace(',', '.') + " " + item.currency!!.currencyCode
            }
        }
    }

    private val differCallback = object : DiffUtil.ItemCallback<Account>() {
        override fun areItemsTheSame(oldItem: Account, newItem: Account): Boolean {
            return oldItem.name == newItem.name
        }

        override fun areContentsTheSame(oldItem: Account, newItem: Account): Boolean {
            return oldItem == newItem
        }
    }
    val differ = AsyncListDiffer(this, differCallback)
}