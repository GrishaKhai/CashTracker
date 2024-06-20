package com.proger.cashtracker.ui.screens.transaction

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.proger.cashtracker.ui.fragment.detailsBoxes.BudgetDetailsBoxFragment
import com.proger.cashtracker.R
import com.proger.cashtracker.databinding.FragmentViewTransactionBinding
import com.proger.cashtracker.ui.fragment.detailsBoxes.TransactionDetailsBoxFragment
import java.util.Calendar

class ViewTransactionFragment() : Fragment() {

    private lateinit var mode: ModeTransaction
    constructor(mode: ModeTransaction, category: String) : this() { this.mode = mode }

    private lateinit var binding: FragmentViewTransactionBinding

    companion object {
        fun newInstance(mode: ModeTransaction, category: String) = ViewTransactionFragment(mode, category)
    }

    private val viewModel: ViewTransactionViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // TODO: Use the ViewModel
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentViewTransactionBinding.inflate(inflater)

        val args = arguments
        if (args != null) {
            mode = args.getString("mode")?.let { ModeTransaction.valueOf(it) }!!
        }

        /*when (mode) {
            ModeTransaction.BUDGET -> {
                val date1 = Calendar.getInstance()
                val date2 = Calendar.getInstance()
                date1.set(1992,11, 14)
                date2.set(2010, 11, 15)

                var f1: BudgetDetailsBoxFragment = BudgetDetailsBoxFragment.newInstance(
                    R.drawable.all_accounts,
                    "Lalla",
                    1200.50,
                    1600.00,
                    date1.time,
                    date2.time)

                date1.set(2024, 2, 1)
                date2.set(2024, 3, 31)
                var f2: BudgetDetailsBoxFragment = BudgetDetailsBoxFragment.newInstance(
                    R.drawable.all_accounts,
                    "Faffa",
                    1400.50,
                    1900.00,
                    date1.time,
                    date2.time)

                childFragmentManager.beginTransaction().apply {
                    add(binding.llTransactionsList.id, f1)
                    add(binding.llTransactionsList.id, f2)
                    commit()
                }
            }
            ModeTransaction.INCOME -> {
                val date = Calendar.getInstance()
                date.set(2024, 1, 12)
                var f1: TransactionDetailsBoxFragment = TransactionDetailsBoxFragment.newInstance(
                    R.drawable.all_accounts,
                    "тумбочка",
                    "стипендия",
                    date.time,
                    2800.00,
                    "новая степуха"
                )

                date.set(2024, 2, 1)
                var f2: TransactionDetailsBoxFragment = TransactionDetailsBoxFragment.newInstance(
                    R.drawable.all_accounts,
                    "кошель",
                    "зп",
                    date.time,
                    35500.00,
                    "первая зп"
                )

                childFragmentManager.beginTransaction().apply {
                    add(binding.llTransactionsList.id, f1)
                    add(binding.llTransactionsList.id, f2)
                    commit()
                }
            }
            ModeTransaction.EXPENSE -> {
                val date = Calendar.getInstance()
                date.set(2010, 11, 15)
                var f1: TransactionDetailsBoxFragment = TransactionDetailsBoxFragment.newInstance(
                    R.drawable.all_accounts,
                    "тумбочка",
                    "хавка",
                    date.time,
                    1600.00,
                    "1231 231213 312 213123"
                )

                date.set(2021, 11, 15)
                var f2: TransactionDetailsBoxFragment = TransactionDetailsBoxFragment.newInstance(
                    R.drawable.all_accounts,
                    "кошель",
                    "ювелирчик",
                    date.time,
                    15500.00,
                    ""
                )

                childFragmentManager.beginTransaction().apply {
                    add(binding.llTransactionsList.id, f1)
                    add(binding.llTransactionsList.id, f2)
                    commit()
                }
            }
            ModeTransaction.DEBT -> {

            }
        }*/

        return binding.root
    }

//    private fun createBox(data:) {
//
//    }
}