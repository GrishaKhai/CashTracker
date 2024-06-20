package com.proger.cashtracker.ui.fragment.shortDetailsBoxes

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import com.proger.cashtracker.databinding.ItemShortWithProgressbarDetailsBinding
import com.proger.cashtracker.ui.screens.tabs.IInvokeAllNotesFragment

class ShortDetailsBoxWithProgressBarFragment (private val invokeAllNotesFragment: IInvokeAllNotesFragment?) : Fragment() {
    private lateinit var binding: ItemShortWithProgressbarDetailsBinding

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
        binding = ItemShortWithProgressbarDetailsBinding.inflate(inflater)

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

        buildPB(binding.pbExpensesOfBudget, current!!, full!!)

        return binding.root
    }

    private fun buildPB(pb: ProgressBar, value: Double, full: Double) {
        val _expenses = Math.round(value).toInt()
        val _fullBudget = Math.round(full).toInt()
        pb.setMax(if(_fullBudget < _expenses) _expenses else _fullBudget)
        pb.progress = _expenses
    }
}