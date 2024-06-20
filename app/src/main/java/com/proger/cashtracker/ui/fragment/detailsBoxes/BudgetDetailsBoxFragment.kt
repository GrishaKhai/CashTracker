package com.proger.cashtracker.ui.fragment.detailsBoxes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.proger.cashtracker.databinding.ItemBudgetDetailsBinding
import java.text.SimpleDateFormat
import java.util.Date

class BudgetDetailsBoxFragment : Fragment() {
    private lateinit var binding: ItemBudgetDetailsBinding
    private val viewModel: BudgetDetailsBoxViewModel by viewModels()

    companion object {
        fun newInstance(idImage: Int, category: String, expenses: Double, fullBudget: Double, startDate: Date, finishDate: Date): BudgetDetailsBoxFragment {
            val fragment = BudgetDetailsBoxFragment()
            val args = Bundle()
            args.putString("category", category)
            args.putDouble("expenses", expenses)
            args.putDouble("fullBudget", fullBudget)
            args.putInt("idImage", idImage)
            args.putLong("startDate", startDate.time)
            args.putLong("finishDate", finishDate.time)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = ItemBudgetDetailsBinding.inflate(inflater)

        val expenses: Double? = arguments?.getDouble("expenses")
        val fullBudget: Double? = arguments?.getDouble("fullBudget")

        binding.tvBudgetCategory.text = arguments?.getString("category")
        binding.tvExpensesOfBudget.text = arguments?.getDouble("expenses").toString()
        binding.tvFullBudget.text = arguments?.getDouble("fullBudget").toString()
        arguments?.getInt("idImage")?.let { binding.ivCategoryIco.setImageResource(it) }

        binding.tvBudgetStartFinishDate.text =
            arguments?.getLong("startDate")?.let { (SimpleDateFormat("dd MMMM yyyy")).format(Date(it))}.toString() +
            " - " +
            arguments?.getLong("finishDate")?.let { (SimpleDateFormat("dd MMMM yyyy")).format(Date(it)) }.toString()

        binding.tvRemainderOfBudget.text = (fullBudget!! - expenses!!).toString()
        buildPB(binding.pbExpensesOfBudget, expenses, fullBudget)

        return binding.root
    }

    private fun buildPB(pb: ProgressBar, expenses: Double, fullBudget: Double) {
        val _expenses = Math.round(expenses).toInt()
        val _fullBudget = Math.round(fullBudget).toInt()
        pb.setMax(if(_fullBudget < _expenses) _expenses else _fullBudget)
        pb.progress = _expenses
    }
}