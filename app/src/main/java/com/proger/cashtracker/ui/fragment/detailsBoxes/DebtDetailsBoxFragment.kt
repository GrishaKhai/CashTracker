package com.proger.cashtracker.ui.fragment.detailsBoxes

import android.app.ActionBar
import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import com.proger.cashtracker.databinding.ItemDebtDetailsBinding
import java.text.SimpleDateFormat
import java.util.Date

class DebtDetailsBoxFragment : Fragment() {
    private lateinit var binding: ItemDebtDetailsBinding
    private val viewModel: DebtDetailsBoxViewModel by viewModels()
    private var isWrapped: Boolean = false

    companion object {
        fun newInstance(
            idImage: Int,
            account: String,
            creditor: String,
            debtor: String,
            startDate: Date,
            finishDate: Date,
            debtValue: Double,
            returnedDebtValue: Double,
            details: String
        ): DebtDetailsBoxFragment {
            val fragment = DebtDetailsBoxFragment()
            val args = Bundle()
            args.putString("account", account)
            args.putString("creditor", creditor)
            args.putString("debtor", debtor)
            args.putLong("startDate", startDate.time)
            args.putLong("finishDate", finishDate.time)
            args.putDouble("debtValue", debtValue)
            args.putDouble("returnedDebtValue", returnedDebtValue)
            args.putString("details", details)
            args.putInt("idImage", idImage)

            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // TODO: Use the ViewModel
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = ItemDebtDetailsBinding.inflate(inflater)

        var full: Double? = arguments?.getDouble("debtValue")
        var value: Double? = arguments?.getDouble("returnedDebtValue")

        arguments?.getInt("idImage")?.let { binding.ivCategoryIco.setImageResource(it) }
        binding.tvAccount.text = arguments?.getString("account")
        binding.tvDebtor.text = buildString {
            append(arguments?.getString("debtor"))
            append("→")
            append(arguments?.getString("creditor"))
        }
        binding.tvDebtStartFinishDate.text = buildString {
            append(arguments?.getLong("startDate")?.let { formatDate(it) })
            append(" - ")
            append(arguments?.getLong("finishDate")?.let { formatDate(it) })
        }
        binding.tvFullDebt.text = full.toString()
        binding.tvRemainderOfDebt.text = (full!! - value!!).toString()
        binding.tvDetails.text = arguments?.getString("details")

        buildPB(binding.pbReturnedDebt, value, full)
        binding.tvDetails.layoutParams?.height = 0
        binding.llDebtDetailBox.setOnClickListener { onClick() }
        return binding.root
    }

    private fun onClick() {
        val tvDetails = binding.tvDetails
        if (tvDetails.text.isNotEmpty()) {
            val params = tvDetails.layoutParams
            val height = if (isWrapped) 0 else ActionBar.LayoutParams.WRAP_CONTENT
            params?.height = height
            tvDetails.setLayoutParams(params)
            isWrapped = !isWrapped
        }
    }

    private fun formatDate(date: Long) : String{
        return (SimpleDateFormat("dd MMMM yyyy")).format(Date(date))
    }
    private fun buildPB(pb: ProgressBar, value: Double, full: Double) {
        val _value = Math.round(value).toInt()
        var _full = Math.round(full).toInt()
        pb.setMax(if(_full < _value) _value else _full)
        pb.progress = _value
    }
}