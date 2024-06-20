package com.proger.cashtracker.ui.screens.home

import android.annotation.SuppressLint
import android.app.ActionBar
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.utils.ColorTemplate
import com.proger.cashtracker.R
import com.proger.cashtracker.databinding.FragmentMainBinding
import com.proger.cashtracker.models.Account
import com.proger.cashtracker.ui.activity.MainActivity
import com.proger.cashtracker.ui.dialog.calendar.CalendarContract
import com.proger.cashtracker.ui.elements.spinner.SpinnerAdapter
import com.proger.cashtracker.ui.screens.GroupAccount
import com.proger.cashtracker.ui.screens.tabs.IUpdatePieChart
import com.proger.cashtracker.ui.screens.tabs.TabsViewModel
import com.proger.cashtracker.ui.screens.tabs.budget.main.MainBudgetFragment
import com.proger.cashtracker.ui.screens.tabs.debt.main.MainDebtFragment
import com.proger.cashtracker.ui.screens.tabs.expense.main.MainExpenseFragment
import com.proger.cashtracker.ui.screens.tabs.income.main.MainIncomeFragment
import com.proger.cashtracker.utils.DateHelper
import com.proger.cashtracker.utils.LocalData
import com.proger.cashtracker.utils.NavigationUtils
import dagger.hilt.android.AndroidEntryPoint
import java.util.Date

@AndroidEntryPoint
open class MainFragment : Fragment() {

    lateinit var binding: FragmentMainBinding
    val viewModel: MainViewModel by viewModels()
    private val calendarViewModel: CalendarContract by activityViewModels()
    val tabsViewModel: TabsViewModel by activityViewModels()


    @SuppressLint("ResourceType")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMainBinding.inflate(inflater, container, false)

//        if(tabsViewModel.getAccount() == null) tabsViewModel.setAccount(baseViewModel.accounts.value?.first()!!.name)
        if (tabsViewModel.getDateInterval() == null) {
            val range = DateHelper.makeDayRange(Date())
            tabsViewModel.setDateInterval(range.first, range.second)
        }

        //создание items для выпадающего списка и списка
        viewModel.accounts.observe(viewLifecycleOwner) {
            renderAccounts(
                it,
                tabsViewModel.getAccount()
            )
        }

        calendarViewModel.range.observe(viewLifecycleOwner) {
            tabsViewModel.setDateInterval(it.first, it.second)
        }

        NavigationUtils.getOptionPageLiveData().observe(viewLifecycleOwner) {
//            lateinit var fragment: Fragment
            when (it) {
                MainActivity.Option.Income -> {
                    visibilityAccount(true)
                    val fragment = MainIncomeFragment(getPieChartCreator())
                    getChildFragmentManager().beginTransaction()
                        .replace(R.id.llDetailsBox, fragment).commit()
                }
                MainActivity.Option.Expense -> {
                    visibilityAccount(true)
                    val fragment = MainExpenseFragment(getPieChartCreator())
                    getChildFragmentManager().beginTransaction()
                        .replace(R.id.llDetailsBox, fragment).commit()
                }
                MainActivity.Option.Debt -> {
                    visibilityAccount(true)
                    setVisiblePieChart(false)
                    val fragment = MainDebtFragment()
                    getChildFragmentManager().beginTransaction()
                        .replace(R.id.llDetailsBox, fragment).commit()
                }
                MainActivity.Option.Budget -> {
                    visibilityAccount(false)
                    val fragment = MainBudgetFragment(getPieChartCreator())
                    getChildFragmentManager().beginTransaction()
                        .replace(R.id.llDetailsBox, fragment).commit()
                }
                null -> {}
            }
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.bottomNavigationView.background = null
        binding.bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.income_page -> {
                    NavigationUtils.setOptionPage(MainActivity.Option.Income)
                }
                R.id.expense_page -> {
                    NavigationUtils.setOptionPage(MainActivity.Option.Expense)
                }
                R.id.budget_page -> {
                    NavigationUtils.setOptionPage(MainActivity.Option.Budget)
                }
                R.id.debts_page -> {
                    NavigationUtils.setOptionPage(MainActivity.Option.Debt)
                }
            }
            true
        }

        binding.fabAddTransaction.setOnClickListener {
            when(NavigationUtils.getOptionPage()){
                MainActivity.Option.Income -> {
                    val action = MainFragmentDirections.actionNavHomeToAddEditIncomeTransactionFragment(
                        date = 0,
                        account = null,
                        value = 0.0f,
                        details = null,
                        category = null,
                    )
                    findNavController().navigate(action)
                }
                MainActivity.Option.Expense -> {
                    val action = MainFragmentDirections.actionNavHomeToAddEditExpenseTransactionFragment(
                        date = 0,
                        account = null,
                        value = 0.0f,
                        details = null,
                        category = null,
                    )
                    findNavController().navigate(action)
                }
                MainActivity.Option.Budget -> {
                    val action = MainFragmentDirections.actionNavHomeToAddEditBudgetFragment(
                        startDate = 0,
                        finishDate = 0,
                        value = 0.0f,
                        category = null,
                    )
                    findNavController().navigate(action)
                }
                MainActivity.Option.Debt -> {

//                    val intent: Intent = Intent(
//                        this@MainActivity,
//                        CustomPinActivity::class.java
//                    )

                    val action = MainFragmentDirections.actionNavHomeToAddEditDebtFragment(
                        startDate = 0,
                        finishDate = 0,
                        account = null,
                        ownerRole = null,
                        nameDebtorOrCreditor = null,
                        value = 0.0f,
                        details = null,
                    )
                    findNavController().navigate(action)
                }

                null -> { }
            }
        }
    }

    private fun visibilityAccount(visible: Boolean){
        var params = binding.tvHeader1.layoutParams
        var height = if (!visible) 0 else ActionBar.LayoutParams.WRAP_CONTENT
        params?.height = height
        binding.tvHeader1.setLayoutParams(params)

        params = binding.spinnerAccount.layoutParams
        height = if (!visible) 0 else ActionBar.LayoutParams.WRAP_CONTENT
        params?.height = height
        binding.spinnerAccount.setLayoutParams(params)
    }

    private fun renderAccounts(account: List<Account>, selectedAccount: Account?) {
        val spinner = binding.spinnerAccount
        val code = LocalData(requireContext()).getBaseCurrency()!!
        val adapter = SpinnerAdapter(context, GroupAccount.extensionAccountArray(account, code), layoutInflater)
        spinner.adapter = null
        spinner.adapter = adapter
        spinner.setPromptId(R.string.choose_account)
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?, view: View?, position: Int, id: Long
            ) {
                val selectedItem = spinner.selectedItem as Account
                tabsViewModel.setAccount(selectedItem)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
        if (selectedAccount != null) selectAccount(account, selectedAccount.name)
        else spinner.setSelection(0)
    }
    private fun selectAccount(accounts: List<Account>, account: String) {
        val position = accounts.indexOfFirst { it.name == account }
        if (position != -1) binding.spinnerAccount.setSelection(position)
        else binding.spinnerAccount.setSelection(accounts.size)
    }


    fun buildPieChart(dataList: List<Pair<Float, String>>, content: String) {
        val chart = binding.pieChart
        chart.clear()
        var fullValue = 0.0
        if(dataList.isNotEmpty()) dataList.forEach{ it -> fullValue += it.first }
        if (dataList.isNotEmpty() && fullValue > 0.0) {
            val entriesPie = ArrayList<PieEntry>()
            for (item in dataList) {
                entriesPie.add(PieEntry(item.first, item.second))
            }

            val pieDataSet = PieDataSet(entriesPie, "")
            pieDataSet.colors = ColorTemplate.COLORFUL_COLORS.toList()

            val pieData = PieData(pieDataSet)
            pieData.setDrawValues(false)
            chart.data = pieData

            chart.setEntryLabelColor(Color.WHITE)
            chart.setDrawEntryLabels(true)
            chart.legend.isEnabled = true
        } else {
            val entriesPie = ArrayList<PieEntry>()
            entriesPie.add(PieEntry((100F)))
            val pieDataSet = PieDataSet(entriesPie, "")
            pieDataSet.setDrawValues(false)
            pieDataSet.color = R.color.gray

            val pieData = PieData(pieDataSet)
            pieData.setDrawValues(false)
            chart.data = pieData

            chart.setEntryLabelColor(Color.BLACK)
            chart.setDrawEntryLabels(false)
            chart.legend.isEnabled = false
        }
        chart.description.isEnabled = false
        chart.centerText = content
        chart.animateY(1000)

        chart.invalidate()
    }
    fun getPieChartCreator() = object : IUpdatePieChart {
        override fun update(dataList: List<Pair<Float, String>>, content: String) {
            setVisiblePieChart(true)
            buildPieChart(dataList, content)
        }
    }
    fun setVisiblePieChart(visible: Boolean){
        val scale = resources.displayMetrics.density
        val dpValue = 300
        val pixelValue = (dpValue * scale + 0.5f).toInt()

        val params = binding.pieChart.layoutParams
        val height = if (!visible) 0 else pixelValue
        params?.height = height
        binding.pieChart.setLayoutParams(params)
    }


    /*
    fun buildRadarChart(dataList: List<RadarItemData>){
        val chart = binding.radarChart
        chart.clear()
        if (dataList.isNotEmpty()) {
            val expenseEntries = ArrayList<RadarEntry>()
//            val budgetEntries = ArrayList<RadarEntry>()
            for(item in dataList){
                expenseEntries.add(RadarEntry(item.value))
//                budgetEntries.add(RadarEntry(item.full))
            }

//            val budgetDS = RadarDataSet(budgetEntries, requireContext().getString(R.string.label_budget))
//            budgetDS.color = Color.argb(255, 47, 211, 43)
//            budgetDS.highLightColor = Color.argb(150, 128, 231, 125)

            val expenseDS = RadarDataSet(expenseEntries, requireContext().getString(R.string.label_expense))
            expenseDS.color = Color.argb(255, 243, 140, 28)

//            val radarData = RadarData(budgetDS, expenseDS)
            val radarData = RadarData(expenseDS)
            chart.data = radarData

            chart.legend.isEnabled = true
        } else {
            val entries = ArrayList<RadarEntry>()
            for(i in 1..10){
                entries.add(RadarEntry(100F))
            }

            val budgetDS = RadarDataSet(entries, requireContext().getString(R.string.label_budget))
            budgetDS.fillColor = Color.argb(150, 128, 231, 125)
            budgetDS.setDrawFilled(true)
            budgetDS.fillAlpha = 170
            budgetDS.isDrawHighlightCircleEnabled = true
            budgetDS.setDrawHighlightIndicators(false)

//            budgetDS.highLightColor = Color.argb(150, 128, 231, 125)

            val radarData = RadarData(budgetDS)
            chart.data = radarData

            chart.legend.isEnabled = false
        }
        chart.animateY(1000)
        chart.description.isEnabled = false
        chart.invalidate()
    }
    fun getRadarChartCreator() = object : IUpdateRadarChart {
        override fun update(dataList: List<RadarItemData>) {
            binding.pieChart.visibility = View.INVISIBLE
            binding.radarChart.visibility = View.VISIBLE
            buildRadarChart(dataList)
        }
    }
     */
}