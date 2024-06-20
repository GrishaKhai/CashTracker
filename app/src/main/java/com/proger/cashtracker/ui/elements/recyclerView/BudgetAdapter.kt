package com.proger.cashtracker.ui.elements.recyclerView

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.proger.cashtracker.databinding.ItemBudgetDetailsBinding
import com.proger.cashtracker.utils.DateHelper
import com.proger.cashtracker.models.Budget
import java.util.Date
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BudgetAdapter @Inject constructor() : RecyclerView.Adapter<BudgetAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemBudgetDetailsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.setData(differ.currentList[position])
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    inner class ViewHolder(private val binding: ItemBudgetDetailsBinding) : RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n", "SimpleDateFormat")
        fun setData(item: Budget) {
            binding.apply {
                val idImage = root.context.resources.getIdentifier(
                    item.expenseCategory!!.image,
                    "drawable",
                    root.context.packageName
                )
                val expenses: Double= item.spendCash!!
                val fullBudget: Double= item.budget!!

                tvBudgetCategory.text = item.expenseCategory!!.nameCategory
                tvExpensesOfBudget.text = String.format("%.2f", expenses).replace(',', '.')
                tvFullBudget.text = String.format("%.2f", fullBudget).replace(',', '.')
                ivCategoryIco.setImageResource(idImage)

                tvBudgetStartFinishDate.text = DateHelper.makeRangeFormat(Date(item.dateStart!!), Date(item.dateFinish!!))

                tvRemainderOfBudget.text = String.format("%.2f", (fullBudget - expenses)).replace(',', '.')
                buildPB(pbExpensesOfBudget, expenses, fullBudget)
            }
        }

        private fun buildPB(pb: ProgressBar, expenses: Double, fullBudget: Double) {
            val _expenses = Math.round(expenses).toInt()
            val _fullBudget = Math.round(fullBudget).toInt()
            pb.setMax(if(_fullBudget < _expenses) _expenses else _fullBudget)
            pb.progress = _expenses
        }
    }

    private val differCallback = object : DiffUtil.ItemCallback<Budget>() {
        override fun areItemsTheSame(oldItem: Budget, newItem: Budget): Boolean {
            return oldItem.expenseCategory!!.nameCategory == newItem.expenseCategory!!.nameCategory &&
                    oldItem.dateStart == newItem.dateStart && oldItem.dateFinish == newItem.dateFinish
        }

        override fun areContentsTheSame(oldItem: Budget, newItem: Budget): Boolean {
            return oldItem == newItem
        }
    }
    val differ = AsyncListDiffer(this, differCallback)
}