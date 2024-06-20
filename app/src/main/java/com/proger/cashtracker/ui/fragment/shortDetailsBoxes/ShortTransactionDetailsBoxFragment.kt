package com.proger.cashtracker.ui.fragment.shortDetailsBoxes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.proger.cashtracker.databinding.ItemShortTransactionDetailsBinding
import com.proger.cashtracker.ui.screens.tabs.IInvokeAllNotesFragment

class ShortTransactionDetailsBoxFragment(private val invokeAllNotesFragment: IInvokeAllNotesFragment?) : Fragment() {
    private lateinit var binding: ItemShortTransactionDetailsBinding

    constructor() : this(null)

    companion object {
        const val ID_IMAGE_KEY = "idImage"
        const val CATEGORY_KEY = "category"
        const val FULL_VALUE_KEY = "fullValue"
        const val CURRENT_VALUE_KEY = "currentValue"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = ItemShortTransactionDetailsBinding.inflate(inflater)

        val current = arguments?.getDouble(CURRENT_VALUE_KEY)
        val full = arguments?.getDouble(FULL_VALUE_KEY)
        val percent = (full?.toFloat()?.let { current?.toFloat()?.div(it) })?.times(100)

        arguments?.getInt(ID_IMAGE_KEY)?.let { binding.ivCategoryIco.setImageResource(it) }
        binding.tvCategory.text = arguments?.getString(CATEGORY_KEY)
        binding.tvCost.text = String.format("%.2f", current).replace(",", ".")
        binding.tvPercent.text = String.format("%.2f", percent) + "%"

        binding.clShortDetailBox.setOnClickListener{
            invokeAllNotesFragment!!.invoke()
        }

        return binding.root
    }
}