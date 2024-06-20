package com.proger.cashtracker.ui.elements.recyclerView

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.proger.cashtracker.databinding.ItemContributionBinding
import com.proger.cashtracker.utils.DateHelper
import com.proger.cashtracker.utils.DoubleHelper
import com.proger.cashtracker.models.Contribution
import com.proger.cashtracker.models.DebtRole
import java.util.Date
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PayAdapter @Inject constructor() : RecyclerView.Adapter<PayAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemContributionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.setData(differ.currentList[position])
    }
    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    inner class ViewHolder(private val binding: ItemContributionBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun setData(item: Contribution) {
            binding.apply {
                val znak = if(roleOwner == DebtRole.Debtor) "-" else "+"

                if(roleOwner == DebtRole.Debtor) tvAccountName.text = item.account!!.name
                tvPayValue.text = "$znak${DoubleHelper.roundValue(item.contribution!!)} ${item.account!!.currency!!.currencyCode}"
                tvPayDate.text = "(${DateHelper.makeShortDayMonthYearFormat(Date(item.date))})"
            }
        }
    }

    private lateinit var roleOwner: DebtRole
    fun setRoleOwner(roleOwner: DebtRole) { this.roleOwner = roleOwner }

    private val differCallback = object : DiffUtil.ItemCallback<Contribution>() {
        override fun areItemsTheSame(oldItem: Contribution, newItem: Contribution): Boolean {
            return oldItem.contributionId == newItem.contributionId
        }

        override fun areContentsTheSame(oldItem: Contribution, newItem: Contribution): Boolean {
            return oldItem == newItem
        }
    }
    val differ = AsyncListDiffer(this, differCallback)
}