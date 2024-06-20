package com.proger.cashtracker

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.proger.cashtracker.databinding.FragmentViewDetailsForMainBinding
import com.proger.cashtracker.ui.fragment.detailsBoxes.BudgetDetailsBoxFragment
import com.proger.cashtracker.ui.fragment.detailsBoxes.DebtDetailsBoxFragment
import com.proger.cashtracker.ui.fragment.detailsBoxes.TransactionDetailsBoxFragment
import com.proger.cashtracker.ui.fragment.shortDetailsBoxes.ShortTransactionDetailsBoxFragment
import com.proger.cashtracker.ui.screens.transaction.ModeTransaction
import dagger.hilt.android.AndroidEntryPoint
import java.util.Calendar
import java.util.Date

@AndroidEntryPoint
class ViewDetailsForMainFragment() : Fragment() {
    private lateinit var mode: ModeTransaction
    private lateinit var dateInterval: Pair<Date, Date>

    val viewModel: ViewDetailsForMainViewModel by viewModels()
    private lateinit var binding: FragmentViewDetailsForMainBinding

    constructor(mode: ModeTransaction, dateInterval: Pair<Date, Date>) : this() {
        this.mode = mode
        this.dateInterval = dateInterval
    }

    companion object {
        fun newInstance(mode: ModeTransaction, dateInterval: Pair<Date, Date>) =
            ViewDetailsForMainFragment(mode, dateInterval)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // TODO: Use the ViewModel
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentViewDetailsForMainBinding.inflate(inflater)


        /*childFragmentManager.beginTransaction().apply {
            val incomes = viewModel.getIncomes()
            for(income in incomes!!){
                add(binding.llTransactionsList.id,
                    TransactionDetailsBoxFragment.newInstance(
                        idImage = requireContext().resources.getIdentifier(income.category!!.nameCategory, "drawable", requireContext().packageName),
                        account = income.account!!.name,
                        category = income.category!!.nameCategory,
                        date = Date(income.date!!),
                        cost = income.value!!,
                        details = income.comment!!
                    ))
            }
            var s = 3 + 2
            commit()
        }*/



        viewModel.incomes.observe(viewLifecycleOwner) {
            if (viewModel.incomes.value != null) {
                childFragmentManager.beginTransaction().apply {
                    for (income in viewModel.incomes.value!!) {
                        add(
                            binding.llTransactionsList.id,
//                            binding.rvTransaction.id,
                            TransactionDetailsBoxFragment.newInstance(
                                idImage = requireContext().resources.getIdentifier(
                                    income.category!!.image,
                                    "drawable",
                                    requireContext().packageName
                                ),
                                account = income.account!!.name,
                                category = income.category!!.nameCategory,
                                date = Date(income.date!!),
                                cost = income.value!!,
                                details = income.comment!!
                            )
                        )
                    }
                    var s = 3 + 2
                    commit()
                }
            }
        }/**/

//        val args = arguments
//        if (args != null) {
//            mode = args.getString("mode")?.let { ModeTransaction.valueOf(it) }!!
//        }

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
                if(viewModel.incomes.value != null) {
                    childFragmentManager.beginTransaction().apply {
                        for(income in viewModel.incomes.value!!){
                            add(binding.llTransactionsList.id,
                                TransactionDetailsBoxFragment.newInstance(
                                    idImage = requireContext().resources.getIdentifier(income.category!!.nameCategory, "drawable", requireContext().packageName),
                                    account = income.account!!.name,
                                    category = income.category!!.nameCategory,
                                    date = Date(income.date!!),
                                    cost = income.value!!,
                                    details = income.comment!!
                                ))
                        }
                        var s = 3 + 2
                        commit()
                    }
                }
                var s = 3 + 2


        //                val date = Calendar.getInstance()
        //                date.set(2024, 1, 12)
        //                var f1: TransactionDetailsBoxFragment = TransactionDetailsBoxFragment.newInstance(
        //                    R.drawable.all_accounts,
        //                    "тумбочка",
        //                    "стипендия",
        //                    date.time,
        //                    2800.00,
        //                    "новая степуха"
        //                )
        //
        //                date.set(2024, 2, 1)
        //                var f2: TransactionDetailsBoxFragment = TransactionDetailsBoxFragment.newInstance(
        //                    R.drawable.all_accounts,
        //                    "кошель",
        //                    "зп",
        //                    date.time,
        //                    35500.00,
        //                    "первая зп 666 8 8 8 9 0 97 6 7870"
        //                )

        //                childFragmentManager.beginTransaction().apply {
        //                    add(binding.llTransactionsList.id, f1)
        //                    add(binding.llTransactionsList.id, f2)
        //                    commit()
        //                }
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
                    " 666 8 8 8 9 0 97 6 7870"
                )

                childFragmentManager.beginTransaction().apply {
                    add(binding.llTransactionsList.id, f1)
                    add(binding.llTransactionsList.id, f2)
                    commit()
                }
            }
            ModeTransaction.DEBT -> {
                val date1 = Calendar.getInstance()
                val date2 = Calendar.getInstance()
                date1.set(1992,11, 14)
                date2.set(2010, 11, 15)

                var f1: DebtDetailsBoxFragment = DebtDetailsBoxFragment.newInstance(
                    R.drawable.all_accounts,
                    "тумбочка",
                    "Я",
                    "Вася",
                    date1.time,
                    date2.time,
                    1900.50,
                    1100.00,
                    "на шавуху")

                date1.set(2024, 2, 1)
                date2.set(2024, 3, 31)
                var f2: DebtDetailsBoxFragment = DebtDetailsBoxFragment.newInstance(
                    R.drawable.all_accounts,
                    "копилка",
                    "Вита",
                    "Я",
                    date1.time,
                    date2.time,
                    1400.50,
                    1300.00,
                    "на чесалку для спини")

                childFragmentManager.beginTransaction().apply {
                    add(binding.llTransactionsList.id, f1)
                    add(binding.llTransactionsList.id, f2)
                    commit()
                }
            }
        }*/
        return binding.root
    }

    private fun initial(dateInterval: Pair<Date, Date>) {

    }

//    private fun createBox(
//        idImage: Int,
//        category: String,
//        fullValue: Double,
//        cost: Double
//    ): Fragment {
//        val f: Fragment =
//            ShortTransactionDetailsBoxFragment.newInstance(idImage, category, fullValue, cost)
//        return f
//    }
}