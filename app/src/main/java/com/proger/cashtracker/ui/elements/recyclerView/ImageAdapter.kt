package com.proger.cashtracker.ui.elements.recyclerView

import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.proger.cashtracker.R

class ImageAdapter internal constructor(
    images: List<String>,
    private val onClickListener: OnImageClickListener
) : RecyclerView.Adapter<ImageAdapter.ImageViewHolder>() {

    private val images: List<String>
    private var selectedItem = RecyclerView.NO_POSITION

    init {
        this.images = images
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val view = ImageView(parent.context)
        val layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        view.layoutParams = layoutParams
        return ImageViewHolder(view)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val context = holder.itemView.context
        val id = context.resources.getIdentifier(images[position], "drawable", context.packageName)
        holder.imageView.setImageResource(id)
        holder.bind(position == selectedItem)
        holder.imageView.setOnClickListener {
            selectItem(position)
            onClickListener.onImageClick(images[position])
        }
    }

    fun selectItem(position: Int) {
        val previousItem = selectedItem
        selectedItem = position
        notifyItemChanged(previousItem)
        notifyItemChanged(selectedItem)
    }

    override fun getItemCount(): Int {
        return images.size
    }


    inner class ImageViewHolder(imageView: ImageView) : RecyclerView.ViewHolder(imageView) {
        val imageView : ImageView

        init{
            this.imageView = imageView
            val layoutParams = ViewGroup.LayoutParams(100, 100)
            this.imageView.layoutParams = layoutParams
        }

        fun bind(isSelected: Boolean) {
            if (isSelected) {
                itemView.setBackgroundResource(R.drawable.icon_border)
            } else {
                itemView.setBackgroundResource(0)
            }
        }
    }
    internal interface OnImageClickListener {
        fun onImageClick(image: String)
    }
}