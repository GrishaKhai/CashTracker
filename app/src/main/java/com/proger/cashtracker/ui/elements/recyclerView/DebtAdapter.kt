package com.proger.cashtracker.ui.elements.recyclerView

import android.annotation.SuppressLint
import android.app.ActionBar
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.proger.cashtracker.R
import com.proger.cashtracker.databinding.ItemDebtDetailsBinding
import com.proger.cashtracker.utils.DateHelper
import com.proger.cashtracker.utils.DoubleHelper
import com.proger.cashtracker.models.Debt
import com.proger.cashtracker.ui.screens.tabs.debt.IPopupDebtOperationFullRealisation
import com.proger.cashtracker.ui.screens.tabs.debt.IPopupDebtOperationShortRealisation
import java.util.Date
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DebtAdapter @Inject constructor() : RecyclerView.Adapter<DebtAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemDebtDetailsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.setData(differ.currentList[position])
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    inner class ViewHolder(private val binding: ItemDebtDetailsBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private var isWrapped: Boolean = false

        @SuppressLint("SetTextI18n", "SimpleDateFormat")
        fun setData(item: Debt) {
            val i = binding.root.context.getString(R.string.i)
            val me = binding.root.context.getString(R.string.me)
            binding.apply {
                val idImage = root.context.resources.getIdentifier(
                    item.account!!.image,
                    "drawable",
                    root.context.packageName
                )
                ivCategoryIco.setImageResource(idImage)

                tvAccount.text = item.account!!.name
                tvDebtor.text = if(item.isDebtor) "$i -> ${item.creditorOrDebtor}" else "${item.creditorOrDebtor} -> $me"
                tvFullDebt.text = "${DoubleHelper.roundValue(item.debt!! * item.account!!.currency!!.rateToBase)} ${item.account!!.currency!!.currencyCode}"
                tvDebtStartFinishDate.text = DateHelper.makeRangeFormat(Date(item.startDate!!), Date(item.finishDate!!))
                tvRemainderOfDebt.text = "${DoubleHelper.roundValue(item.debt?.minus(item.returned)!! * item.account!!.currency!!.rateToBase)} (${DoubleHelper.roundValue((item.returned / item.debt!!) * 100)}%)"
                tvDetails.text = item.comment

                tvDetails.layoutParams?.height = 0
                cvDebtDetailBox.setOnClickListener { onClick(tvDetails, item.comment ?: "") }

                cvDebtDetailBox.setOnLongClickListener {
                    showPopupMenu(it, item)
                    true
                }
                buildPB(pbReturnedDebt, item.returned, item.debt!!)
            }
        }
        private fun buildPB(pb: ProgressBar, value: Double, full: Double) {
            val _value = Math.round(value).toInt()
            val _full = Math.round(full).toInt()
            pb.setMax(if(_full < _value) _value else _full)
            pb.progress = _value
        }

        private fun onClick(textView: TextView, text: String) {
            if (text.isNotEmpty()) {
                val params = textView.layoutParams
                val height = if (isWrapped) 0 else ActionBar.LayoutParams.WRAP_CONTENT
                params?.height = height
                textView.setLayoutParams(params)
                isWrapped = !isWrapped
            }
        }

        private fun showPopupMenu(v: View, note: Debt) {
            if (note.isReturned) {
                val popupMenu = PopupMenu(binding.root.context, v)
                popupMenu.inflate(R.menu.popup_closed_debt_menu)
                popupMenu.setOnMenuItemClickListener { item ->
                    when (item.itemId) {
                        R.id.delete -> {
                            operationShort?.delete(note)
                            true
                        }
                        R.id.details -> {
                            operationShort?.details(note)
                            true
                        }
                        else -> false
                    }
                }
                popupMenu.show()
            } else {
                val popupMenu = PopupMenu(binding.root.context, v)
                popupMenu.inflate(R.menu.popup_active_debt_menu)
                popupMenu.setOnMenuItemClickListener { item ->
                    when (item.itemId) {
                        R.id.edit -> {
                            operationFull?.edit(note)
                            true
                        }
                        R.id.delete -> {
                            operationFull?.delete(note)
                            true
                        }
                        R.id.details -> {
                            operationFull?.details(note)
                            true
                        }
                        R.id.pay_debt -> {
                            operationFull?.payDebt(note)
                            true
                        }
                        else -> false
                    }
                }
                popupMenu.show()
            }
        }
    }

    private var operationFull: IPopupDebtOperationFullRealisation? = null
    fun setOperation(operation: IPopupDebtOperationFullRealisation) {
        this.operationFull = operation
    }

    private var operationShort: IPopupDebtOperationShortRealisation? = null
    fun setOperation(operation: IPopupDebtOperationShortRealisation) {
        this.operationShort = operation
    }

    private val differCallback = object : DiffUtil.ItemCallback<Debt>() {
        override fun areItemsTheSame(oldItem: Debt, newItem: Debt): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Debt, newItem: Debt): Boolean {
            return oldItem == newItem
        }
    }
    val differ = AsyncListDiffer(this, differCallback)
}