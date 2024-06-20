package com.proger.cashtracker.ui.screens.tabs.debt.main

import android.annotation.SuppressLint
import android.app.ActionBar
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.proger.cashtracker.R
import com.proger.cashtracker.databinding.FragmentViewDebtDetailsForMainBinding
import com.proger.cashtracker.db.query.ShortDebtDetails
import com.proger.cashtracker.utils.DoubleHelper
import com.proger.cashtracker.utils.LocalData
import com.proger.cashtracker.models.Debt
import com.proger.cashtracker.models.DebtRole
import com.proger.cashtracker.ui.dialog.calculator.CalculatorHelper
import com.proger.cashtracker.ui.elements.recyclerView.DebtAdapter
import com.proger.cashtracker.ui.elements.recyclerView.ShortDebtAdapter
import com.proger.cashtracker.ui.screens.GroupAccount
import com.proger.cashtracker.ui.screens.category.dialogs.addEdit.AddEditCategoryViewModel
import com.proger.cashtracker.ui.screens.home.MainFragmentDirections
import com.proger.cashtracker.ui.screens.tabs.TabsViewModel
import com.proger.cashtracker.ui.screens.tabs.debt.IPopupDebtOperationFullRealisation
import com.proger.cashtracker.ui.screens.tabs.debt.dialog.payDebt.PayDebtDialog
import com.proger.cashtracker.utils.DataStatus
import com.proger.cashtracker.utils.isVisible
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainDebtFragment : Fragment(R.layout.fragment_view_debt_details_for_main) {

    private val viewModel: MainDebtViewModel by viewModels()
    private val tabsViewModel: TabsViewModel by activityViewModels()
    lateinit var binding: FragmentViewDebtDetailsForMainBinding


    @Inject
    lateinit var debtAdapter: DebtAdapter

    @Inject
    lateinit var shortDebtAdapter: ShortDebtAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentViewDebtDetailsForMainBinding.inflate(inflater)

        viewModel.updateActiveDebts()
        tabsViewModel.getAccountLiveData().observe(viewLifecycleOwner) { invokeUpdate() }
        tabsViewModel.getDateIntervalLiveData().observe(viewLifecycleOwner) { invokeUpdate() }
        viewModel.activeDebts.observe(viewLifecycleOwner) { updateFullItems(it) }
        viewModel.shortByAllDebts.observe(viewLifecycleOwner) { updateShortItems(it) }

        return binding.root
    }


    private fun invokeUpdate() {
        if (tabsViewModel.getDateInterval() != null && tabsViewModel.getAccount() != null) {
            if (GroupAccount.isGroupAccount(tabsViewModel.getAccount()!!.name)) {
                viewModel.updateShortDebtsGrope(
                    tabsViewModel.getDateInterval()!!.first.time,
                    tabsViewModel.getDateInterval()!!.second.time
                )
            } else {
                viewModel.updateShortDebtsGrope(
                    tabsViewModel.getDateInterval()!!.first.time,
                    tabsViewModel.getDateInterval()!!.second.time,
                    tabsViewModel.getAccount()!!.name
                )
            }
        }
    }

    private fun updateFullItems(debts: List<Debt>) {
        if (debts.isNotEmpty()) {
            var _creditValue = 0.0
            var _debtValue = 0.0
            debts.forEach { _debtValue += if (it.isDebtor) it.debt!! - it.returned else 0.0 }
            debts.forEach { _creditValue += if (!it.isDebtor) it.debt!! - it.returned else 0.0 }

            binding.tvValueCreditor.text = "${binding.tvValueCreditor.tag}: ${
                CalculatorHelper.formatValue(
                    DoubleHelper.roundValue(_creditValue)
                )
            } ${LocalData(requireContext()).getBaseCurrency()}"
            binding.tvValueDebtor.text = "${binding.tvValueDebtor.tag}: ${
                CalculatorHelper.formatValue(
                    DoubleHelper.roundValue(_debtValue)
                )
            } ${LocalData(requireContext()).getBaseCurrency()}"

            binding.rvActiveDebts.removeAllViews()
            debtAdapter.differ.submitList(debts)
            debtAdapter.setOperation(object : IPopupDebtOperationFullRealisation {
                override fun edit(debt: Debt) {
                    val action = MainFragmentDirections.actionNavHomeToAddEditDebtFragment(
                        idNote = debt.id!!.toInt(),
                        startDate = debt.startDate!!,
                        finishDate = debt.finishDate!!,
                        account = debt.account!!.name,
                        ownerRole = if (debt.isDebtor) DebtRole.Debtor.toString() else DebtRole.Creditor.toString(),
                        nameDebtorOrCreditor = debt.creditorOrDebtor!!.nameDebtor,
                        value = (debt.debt!! * debt.account!!.currency!!.rateToBase).toFloat(),
                        details = debt.comment,
                    )
                    findNavController().navigate(action)
                }

                override fun delete(debt: Debt) {
                    val roleOwner: DebtRole =
                        if (debt.isDebtor) DebtRole.Debtor else DebtRole.Creditor

                    viewModel.deleteDebt(debt, roleOwner)
                    Snackbar.make(
                        binding.root,
                        activity!!.getString(R.string.delete_debts),
                        Snackbar.LENGTH_LONG
                    ).show()
                }

                @SuppressLint("ResourceType")
                override fun details(debt: Debt) {
                    val action = MainFragmentDirections.actionNavHomeToViewPayForDebtFragment(
                        debtId = debt.id!!,
                        roleOwner = if (debt.isDebtor) DebtRole.Debtor.toString() else DebtRole.Creditor.toString(),
                        debt = (debt.debt!! * debt.account!!.currency!!.rateToBase).toFloat(),
                    )
                    findNavController().navigate(action)
                }

                override fun payDebt(debt: Debt) {
                    val dialogFragment = PayDebtDialog()
                    dialogFragment.arguments = bundleOf(
                        PayDebtDialog.MODE_KEY to AddEditCategoryViewModel.Mode.Add.toString(),
                        PayDebtDialog.DEBT_ID_KEY to debt.id!!.toInt()
//                        PayDebtDialog.REMAINED_DEBT_KEY to (debt.debt?.minus(debt.returned)),
//                        PayDebtDialog.ROLE_OWNER_KEY to (if(debt.isDebtor) DebtRole.Debtor else DebtRole.Creditor)
                    )
                    dialogFragment.show(requireFragmentManager(), "MyDialog")
                }
            })
            binding.rvActiveDebts.apply {
                layoutManager = LinearLayoutManager(context)
                adapter = debtAdapter
            }
        }
    }

    private fun visibleEmptyList(tv: TextView, isVisible: Boolean) {
        val params = tv.layoutParams
        val height = if (isVisible) ActionBar.LayoutParams.WRAP_CONTENT else 0
        params?.height = height
        tv.setLayoutParams(params)
    }

    private fun updateShortItems(debts: DataStatus<List<ShortDebtDetails>>) {
        binding.rvAllDebts.removeAllViews()
        when (debts.status) {
            DataStatus.Status.LOADING -> {
                binding.pbLoading.isVisible(true, binding.rvAllDebts)
            }

            DataStatus.Status.SUCCESS -> {
                binding.pbLoading.isVisible(false, binding.rvAllDebts)
                visibleEmptyList(binding.tvEmptyAllDebt, debts.data!!.isEmpty())
                shortDebtAdapter.differ.submitList(debts.data)
                shortDebtAdapter.setOperation(object : ShortDebtAdapter.IOpenAllNotesFragment {
                    override fun open(nameDebtorOrCreditor: String, ownerRole: DebtRole) {
                        val action = MainFragmentDirections.actionNavHomeToViewAllDebtsFragment(
                            accountName = tabsViewModel.getAccount()!!.name,
                            startDate = tabsViewModel.getDateInterval()!!.first.time,
                            finishDate = tabsViewModel.getDateInterval()!!.second.time,
                            nameDebtorOrCreditor = nameDebtorOrCreditor,
                            ownerRole = ownerRole.toString(),
                        )
                        findNavController().navigate(action)
                    }
                })
                binding.rvAllDebts.apply {
                    layoutManager = LinearLayoutManager(context)
                    adapter = shortDebtAdapter
                }
            }

            DataStatus.Status.ERROR -> {
                binding.pbLoading.isVisible(false, binding.rvAllDebts)
                Toast.makeText(context, debts.message, Toast.LENGTH_SHORT).show()
            }
        }
    }
}