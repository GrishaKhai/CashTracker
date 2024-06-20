package com.proger.cashtracker.ui.elements.recyclerView

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.proger.cashtracker.R
import com.proger.cashtracker.databinding.ItemShortDebtDetailsBinding
import com.proger.cashtracker.db.query.ShortDebtDetails
import com.proger.cashtracker.utils.DoubleHelper
import com.proger.cashtracker.models.DebtRole
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ShortDebtAdapter @Inject constructor() : RecyclerView.Adapter<ShortDebtAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemShortDebtDetailsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.setData(differ.currentList[position])
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    inner class ViewHolder(private val binding: ItemShortDebtDetailsBinding) :
        RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n", "SimpleDateFormat")
        fun setData(item: ShortDebtDetails) {
            val i = binding.root.context.getString(R.string.i)
            val me = binding.root.context.getString(R.string.me)
            binding.apply {
                tvDebtor.text = if(item.is_debtor) "$i -> ${item.creditor_or_debtor}" else "${item.creditor_or_debtor} -> $me"
                tvTotalValue.text = "${DoubleHelper.roundValue(item.returned_sum)}/${DoubleHelper.roundValue(item.debt_sum)}(${DoubleHelper.roundValue(((item.returned_sum / item.debt_sum)) * 100)}%)"
                buildPB(pbReturnedDebt, item.returned_sum, item.debt_sum)
                val roleOwner: DebtRole = if(item.is_debtor) DebtRole.Debtor else DebtRole.Creditor
                cvBudgetDetailBox.setOnClickListener{ openAllNotes?.open(item.creditor_or_debtor, roleOwner) }
            }
        }
        private fun buildPB(pb: ProgressBar, value: Double, full: Double) {
            val _value = Math.round(value).toInt()
            val _full = Math.round(full).toInt()
            pb.setMax(if(_full < _value) _value else _full)
            pb.progress = _value
        }
    }

    private var openAllNotes: IOpenAllNotesFragment? = null
    fun setOperation(openAllNotes: IOpenAllNotesFragment) {
        this.openAllNotes = openAllNotes
    }

    private val differCallback = object : DiffUtil.ItemCallback<ShortDebtDetails>() {
        override fun areItemsTheSame(oldItem: ShortDebtDetails, newItem: ShortDebtDetails): Boolean {
            return oldItem.is_debtor == newItem.is_debtor && oldItem.creditor_or_debtor == newItem.creditor_or_debtor
        }

        override fun areContentsTheSame(oldItem: ShortDebtDetails, newItem: ShortDebtDetails): Boolean {
            return oldItem == newItem
        }
    }
    val differ = AsyncListDiffer(this, differCallback)

    interface IOpenAllNotesFragment{
        fun open(nameDebtorOrCreditor: String, ownerRole: DebtRole)
    }
}