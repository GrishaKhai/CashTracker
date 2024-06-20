package com.proger.cashtracker.ui.elements.recyclerView

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.proger.cashtracker.R
import com.proger.cashtracker.databinding.ItemRecyclerViewCategoryBinding
import com.proger.cashtracker.models.Category
import com.proger.cashtracker.ui.screens.category.IEditDeleteItem
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EditCategoryAdapter @Inject constructor() : RecyclerView.Adapter<EditCategoryAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemRecyclerViewCategoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.setData(differ.currentList[position])

//        holder.itemView.setOnLongClickListener {
//            val anim = AnimationUtils.loadAnimation(it.context, R.anim.item_pressed_anim)
//            holder.itemView.startAnimation(anim)
//            true
//        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    @SuppressLint("ResourceType", "ClickableViewAccessibility")
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
                clCategoryBlock.setOnLongClickListener{
                    showPopupMenu(it, item)
                    true
                }
            }
        }

        private fun showPopupMenu(v: View, note: Category) {
            val popupMenu = PopupMenu(binding.root.context, v)
            popupMenu.inflate(R.menu.popup_edit_delete_menu)
            popupMenu
                .setOnMenuItemClickListener { item ->
                    when (item.itemId) {
                        R.id.edit -> {
                            methodOnItem?.edit(note)
                            true
                        }
                        R.id.delete -> {
                            methodOnItem?.delete(note)
                            true
                        }
                        else -> false
                    }
                }
            popupMenu.show()
        }
    }

    private var methodOnItem: IEditDeleteItem? = null
    fun setMethodOnItem(methodOnItem: IEditDeleteItem) {
        this.methodOnItem = methodOnItem
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