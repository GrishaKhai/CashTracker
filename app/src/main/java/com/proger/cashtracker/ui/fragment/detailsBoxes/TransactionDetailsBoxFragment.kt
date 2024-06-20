package com.proger.cashtracker.ui.fragment.detailsBoxes

import android.app.ActionBar.LayoutParams
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.proger.cashtracker.databinding.ItemTransactionDetailsBinding
import java.text.SimpleDateFormat
import java.util.Date

class TransactionDetailsBoxFragment : Fragment() {
    private lateinit var binding: ItemTransactionDetailsBinding
    private var isWrapped: Boolean = false

    companion object {
        fun newInstance(
            idImage: Int,
            account: String,
            category: String,
            date: Date,
            cost: Double,
            details: String
//            tDetails: TransactionDetails
        ): TransactionDetailsBoxFragment {
            val fragment = TransactionDetailsBoxFragment()
            val args = Bundle()
            args.putString("account", account)
            args.putString("category", category)
            args.putLong("date", date.time)
            args.putDouble("cost", cost)
            args.putString("details", details)
            args.putInt("idImage", idImage)

//            args.putString("account", tDetails.account)
//            args.putString("category", tDetails.category)
//            args.putLong("date", tDetails.date.time)
//            args.putDouble("cost", tDetails.cost)
//            args.putString("details", tDetails.details)
//            args.putInt("idImage", tDetails.idImage)
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
        binding = ItemTransactionDetailsBinding.inflate(inflater)

        arguments?.getInt("idImage")?.let { binding.ivCategoryIco.setImageResource(it) }

        binding.tvAccount.text = arguments?.getString("account")
        binding.tvCategory.text = arguments?.getString("category")
        binding.tvDateCashDetailsBox.text =
            arguments?.getLong("date")?.let { (SimpleDateFormat("dd MMMM yyyy")).format(Date(it)) }
                .toString()
        binding.tvCost.text = arguments?.getDouble("cost").toString()
        binding.tvDetails.text = arguments?.getString("details")

        binding.tvDetails.layoutParams?.height = 0
        binding.cvTransactionDetailBox.setOnClickListener { onClick() }
        return binding.root
    }


    private fun onClick() {
        val tvDetails = binding.tvDetails
        if (tvDetails.text.isNotEmpty()) {
            val params = tvDetails.layoutParams
            val height = if (isWrapped) 0 else LayoutParams.WRAP_CONTENT
            params?.height = height
            tvDetails.setLayoutParams(params)
            isWrapped = !isWrapped
        }
    }

//    class TransactionDetails(
//        var idImage: Int,
//        var account: String,
//        var category: String,
//        var date: Date,
//        var cost: Double,
//        var details: String
//    )
}