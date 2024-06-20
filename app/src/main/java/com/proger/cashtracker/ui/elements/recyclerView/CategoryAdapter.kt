package com.proger.cashtracker.ui.elements.recyclerView

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.proger.cashtracker.R
import com.proger.cashtracker.databinding.ItemAccountBinding
import com.proger.cashtracker.databinding.ItemRecyclerViewCategoryBinding
import com.proger.cashtracker.models.Account
import com.proger.cashtracker.models.Category
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CategoryAdapter @Inject constructor() : RecyclerView.Adapter<CategoryAdapter.ViewHolder>() {
    private var selectedItem = RecyclerView.NO_POSITION

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemRecyclerViewCategoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.setData(differ.currentList[position])
        holder.bind(position == selectedItem)
        holder.itemView.setOnClickListener {
            selectItem(position)
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    fun selectItem(position: Int) {
        val previousItem = selectedItem
        selectedItem = position
        notifyItemChanged(previousItem)
        notifyItemChanged(selectedItem)
    }

    fun getSelectedItem() : Category? = if(selectedItem == RecyclerView.NO_POSITION) null else differ.currentList[selectedItem]

    inner class ViewHolder(private val binding: ItemRecyclerViewCategoryBinding) : RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n", "SimpleDateFormat")
        fun setData(item: Category) {
            binding.apply {
                val idImage = root.context.resources.getIdentifier(
                    item.image,
                    "drawable",
                    root.context.packageName
                )
                ivCategoryIco.setImageResource(idImage)
                tvNameCategory.text = item.nameCategory
            }
        }

        fun bind(isSelected: Boolean) {
            if (isSelected) {
                itemView.setBackgroundResource(R.drawable.icon_border)
            } else {
                itemView.setBackgroundResource(0)
            }
        }
    }

    private val differCallback = object : DiffUtil.ItemCallback<Category>() {
        override fun areItemsTheSame(oldItem: Category, newItem: Category): Boolean {
            return oldItem.nameCategory == newItem.nameCategory
        }

        override fun areContentsTheSame(oldItem: Category, newItem: Category): Boolean {
            return oldItem == newItem
        }
    }
    val differ = AsyncListDiffer(this, differCallback)
}