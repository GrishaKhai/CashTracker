package com.proger.cashtracker.ui.screens.tabs.income.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.proger.cashtracker.R
import com.proger.cashtracker.databinding.FragmentViewDetailsForMainBinding
import com.proger.cashtracker.utils.LocalData
import com.proger.cashtracker.ui.fragment.shortDetailsBoxes.ShortTransactionDetailsBoxFragment
import com.proger.cashtracker.ui.screens.GroupAccount
import com.proger.cashtracker.ui.screens.home.MainFragmentDirections
import com.proger.cashtracker.ui.screens.tabs.IInvokeAllNotesFragment
import com.proger.cashtracker.ui.screens.tabs.IUpdatePieChart
import com.proger.cashtracker.ui.screens.tabs.TabsViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainIncomeFragment(private val updateChart: IUpdatePieChart?) :
    Fragment(R.layout.fragment_view_details_for_main) {

    val viewModel: MainIncomeViewModel by viewModels()
    private val tabsViewModel: TabsViewModel by activityViewModels()
    lateinit var binding: FragmentViewDetailsForMainBinding

    constructor() : this(null)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentViewDetailsForMainBinding.inflate(inflater)

        tabsViewModel.getAccountLiveData().observe(viewLifecycleOwner) {
            invokeUpdate()
        }
        tabsViewModel.getDateIntervalLiveData().observe(viewLifecycleOwner) {
            invokeUpdate()
        }
        viewModel.incomes.observe(viewLifecycleOwner) { list ->
            updateItems()
            val items = list.map { Pair(it.sumTransaction.toFloat(), it.nameCategory) }

            var total = 0.0
            val currency = if (GroupAccount.isGroupAccount(tabsViewModel.getAccount()!!.name)) {
                LocalData(requireContext()).getBaseCurrency().toString()
            } else {
                tabsViewModel.getAccount()!!.currency?.currencyCode!!
            }
            list.forEach { total += it.sumTransaction }

            val _total = String.format("%.2f", total).replace(',', '.')
            val content =
                if (items.size > 0) "$_total $currency" else requireActivity().getString(R.string.income_empty)
            updateChart?.update(items, content)
        }
        if (binding.llTransactionsList.childCount > 0)//todo а нужна ли эта очистка
            binding.llTransactionsList.removeAllViews()

        return binding.root
    }

    private fun invokeUpdate() {
        if (tabsViewModel.getDateInterval() != null && tabsViewModel.getAccount() != null) {
            if (GroupAccount.isGroupAccount(tabsViewModel.getAccount()!!.name)) {
                viewModel.updateIncomes(
                    tabsViewModel.getDateInterval()!!.first.time,
                    tabsViewModel.getDateInterval()!!.second.time
                )
            } else {
                viewModel.updateIncomes(
                    tabsViewModel.getDateInterval()!!.first.time,
                    tabsViewModel.getDateInterval()!!.second.time,
                    tabsViewModel.getAccount()!!.name
                )
            }
        }
    }

    private fun updateItems() {
        if (viewModel.incomes.value != null) {
            binding.llTransactionsList.removeAllViews()
            childFragmentManager.beginTransaction().apply {
                var summa = 0.0
                if (viewModel.incomes.value != null) viewModel.incomes.value!!.forEach { summa += it.sumTransaction }
                for (income in viewModel.incomes.value!!) {
                    val invoker = object : IInvokeAllNotesFragment {
                        override fun invoke() {
                            val navController = findNavController()
                            val action =
                                MainFragmentDirections.actionNavHomeToViewAllIncomesFragment(
                                    startDate = tabsViewModel.getDateInterval()!!.first.time,
                                    finishDate = tabsViewModel.getDateInterval()!!.second.time,
                                    accountName = tabsViewModel.getAccount()!!.name, //nameAccount,
                                    categoryName = income.nameCategory,
                                )
                            navController.navigate(action)
                        }
                    }

                    val fragment = ShortTransactionDetailsBoxFragment(invoker)
                    fragment.arguments = bundleOf(
                        ShortTransactionDetailsBoxFragment.ID_IMAGE_KEY to
                                requireContext().resources.getIdentifier(
                                    income.image,
                                    "drawable",
                                    requireContext().packageName
                                ),
                        ShortTransactionDetailsBoxFragment.CATEGORY_KEY to income.nameCategory,
                        ShortTransactionDetailsBoxFragment.FULL_VALUE_KEY to summa,
                        ShortTransactionDetailsBoxFragment.CURRENT_VALUE_KEY to income.sumTransaction
                    )

                    add(
                        binding.llTransactionsList.id,
                        fragment
                    )
                }
                commit()
            }
        }
    }
}