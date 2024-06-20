package com.proger.cashtracker.ui.elements.recyclerView

import android.graphics.Color
import android.graphics.PorterDuff
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.proger.cashtracker.R
import com.proger.cashtracker.models.Category

class CategoryWithAddOptionAdapter internal constructor(
    category: List<Category>,
    private val onClickListener: OnCategoryClickListener
) :
    RecyclerView.Adapter<CategoryWithAddOptionAdapter.ViewHolder>() {
    internal interface OnCategoryClickListener {
        fun onCategoryClick(category: Category)
        fun onShowAddEditCategoryDialog()
    }

    private val categories: List<Category>

    init {
        var list = ArrayList<Category>()
        if(category.toList().size == 1){
            list.add(category.toList()[0])
        } else if(category.toList().size > 1) {
            list = (category as ArrayList<Category>)
        }
        list.add(Category("", "plus"))
        this.categories = list
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_recycler_view_category, parent, false)
        return ViewHolder(view)
    }

    private var selectedItem = RecyclerView.NO_POSITION

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val context = holder.itemView.context
        val category: Category = categories[position]

        //прорисовка картинок и разукрашивание
        if(position == (this.categories.size - 1)){
            val idResource = context.resources.getIdentifier(category.image, "drawable", context.packageName)
            val drawable = ContextCompat.getDrawable(context, idResource)
            drawable?.setColorFilter(Color.parseColor("#00ff00"), PorterDuff.Mode.SRC_ATOP)
            holder.iconView.setImageDrawable(drawable)
        }
        else{
            holder.iconView.setImageResource(
                context.resources.getIdentifier(
                    category.image,
                    "drawable",
                    context.packageName
                )
            )
        }

        holder.categoryView.text = category.nameCategory

        holder.bind(position == selectedItem)
        holder.itemView.setOnClickListener {
            if(position == (this.categories.size - 1)){//кнопка для добавления новой категории
                onClickListener.onShowAddEditCategoryDialog()
            } else {//обычная категория
                selectItem(position)
                onClickListener.onCategoryClick(category)
            }
        }
    }

    fun selectItem(position: Int) {
        val previousItem = selectedItem
        selectedItem = position
        notifyItemChanged(previousItem)
        notifyItemChanged(selectedItem)
    }

    override fun getItemCount(): Int {
        return categories.size
    }

    class ViewHolder internal constructor(view: View) : RecyclerView.ViewHolder(view) {
        val categoryView: TextView
        val iconView: ImageView

        init {
            iconView = view.findViewById(R.id.ivCategoryIco)
            categoryView = view.findViewById(R.id.tvNameCategory)
        }

        fun bind(isSelected: Boolean) {
            if (isSelected) {
                itemView.setBackgroundResource(R.drawable.icon_border)
            } else {
                itemView.setBackgroundResource(0)
            }
        }
    }
}