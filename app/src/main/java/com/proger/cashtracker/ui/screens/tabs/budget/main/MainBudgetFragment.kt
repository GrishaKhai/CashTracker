package com.proger.cashtracker.ui.screens.tabs.budget.main

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
import com.proger.cashtracker.ui.fragment.shortDetailsBoxes.ShortDetailsBoxWithProgressBarFragment
import com.proger.cashtracker.ui.fragment.shortDetailsBoxes.ShortTransactionDetailsBoxFragment
import com.proger.cashtracker.ui.screens.home.MainFragmentDirections
import com.proger.cashtracker.ui.screens.tabs.IInvokeAllNotesFragment
import com.proger.cashtracker.ui.screens.tabs.IUpdatePieChart
import com.proger.cashtracker.ui.screens.tabs.TabsViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainBudgetFragment (private val updateChart: IUpdatePieChart?) ://(private val updateChart: IUpdateRadarChart?) :
    Fragment(R.layout.fragment_view_details_for_main) {

    val viewModel: MainBudgetViewModel by viewModels()
    private val tabsViewModel: TabsViewModel by activityViewModels()
    lateinit var binding: FragmentViewDetailsForMainBinding

    constructor() : this(null)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentViewDetailsForMainBinding.inflate(inflater)

        tabsViewModel.getDateIntervalLiveData().observe(viewLifecycleOwner) {
            invokeUpdate()
        }
        viewModel.budgets.observe(viewLifecycleOwner) { list ->
            updateItems()
//            val items = list.map { RadarItemData(
//                name = it.expenseCategory!!.nameCategory,
//                value = it.spendCash!!.toFloat(),
//                full = it.budget!!.toFloat()
//            )}
            val items = list.map { Pair(it.budget!!.toFloat(), it.expenseCategory!!.nameCategory) }

            var totalSpend = 0.0
            var totalBudget = 0.0

            val currency = LocalData(requireContext()).getBaseCurrency().toString()
            list.forEach { totalSpend += it.spendCash!! }
            list.forEach { totalBudget += it.budget!! }

            val _totalSpend = String.format("%.2f", totalSpend).replace(',', '.')
            val _totalBudget = String.format("%.2f", totalBudget).replace(',', '.')

            val content = if (items.isNotEmpty()) "$_totalSpend/$_totalBudget $currency" else requireActivity().getString(R.string.budget_empty)
            updateChart?.update(items, content)
        }

        return binding.root
    }

    private fun invokeUpdate() {
        if (tabsViewModel.getDateInterval() != null) {
            viewModel.updateBudgets(
                tabsViewModel.getDateInterval()!!.first.time,
                tabsViewModel.getDateInterval()!!.second.time
            )
        }
    }

    private fun updateItems() {
        if (viewModel.budgets.value != null) {
            binding.llTransactionsList.removeAllViews()
            childFragmentManager.beginTransaction().apply {
                for (budget in viewModel.budgets.value!!) {
                    val invoker = object : IInvokeAllNotesFragment {
                        override fun invoke() {
                            val navController = findNavController()
                            val action =
                                MainFragmentDirections.actionNavHomeToViewAllBudgetsFragment(
                                    startDate = tabsViewModel.getDateInterval()!!.first.time,
                                    finishDate = tabsViewModel.getDateInterval()!!.second.time,
                                    categoryName = budget.expenseCategory!!.nameCategory,
                                )
                            navController.navigate(action)
                        }
                    }

                    val fragment = ShortDetailsBoxWithProgressBarFragment(invoker)
                    fragment.arguments = bundleOf(
                        ShortTransactionDetailsBoxFragment.ID_IMAGE_KEY to
                                requireContext().resources.getIdentifier(
                                    budget.expenseCategory!!.image,
                                    "drawable",
                                    requireContext().packageName
                                ),
                        ShortTransactionDetailsBoxFragment.CATEGORY_KEY to budget.expenseCategory!!.nameCategory,
                        ShortTransactionDetailsBoxFragment.FULL_VALUE_KEY to budget.budget,
                        ShortTransactionDetailsBoxFragment.CURRENT_VALUE_KEY to budget.spendCash
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