package com.proger.cashtracker.ui.screens.tabs.debt.allNotes

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SearchView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.proger.cashtracker.R
import com.proger.cashtracker.databinding.FragmentViewAllNotesBinding
import com.proger.cashtracker.models.Debt
import com.proger.cashtracker.models.DebtRole
import com.proger.cashtracker.ui.elements.recyclerView.DebtAdapter
import com.proger.cashtracker.ui.screens.GroupAccount
import com.proger.cashtracker.ui.screens.category.dialogs.addEdit.AddEditCategoryViewModel
import com.proger.cashtracker.ui.screens.tabs.debt.IPopupDebtOperationFullRealisation
import com.proger.cashtracker.ui.screens.tabs.debt.dialog.payDebt.PayDebtDialog
import com.proger.cashtracker.utils.DataStatus
import com.proger.cashtracker.utils.isVisible
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ViewAllDebtsFragment: Fragment(R.layout.fragment_view_all_notes) {
    private val viewModel: ViewAllDebtsViewModel by viewModels()
    private lateinit var binding: FragmentViewAllNotesBinding
    private val args: ViewAllDebtsFragmentArgs by navArgs()

    @Inject
    lateinit var debtAdapter: DebtAdapter

    @SuppressLint("ResourceType")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentViewAllNotesBinding.inflate(inflater)
        binding.apply {
            val nameAccount = if(GroupAccount.isGroupAccount(args.accountName)) "%%" else args.accountName

            if(viewModel.getSortMode() == null) {
                viewModel.getFullDebtDetailsDateAsc(
                    args.startDate,
                    args.finishDate,
                    DebtRole.valueOf(args.ownerRole) == DebtRole.Debtor,
                    args.nameDebtorOrCreditor,
                    nameAccount
                )
                viewModel.setSortMode(0)
            }

            viewModel.debts.observe(viewLifecycleOwner) {
                when (it.status) {
                    DataStatus.Status.LOADING -> {
                        pbLoading.isVisible(true, rvNotes)
                        emptyBody.isVisible(false, rvNotes)
                    }

                    DataStatus.Status.SUCCESS -> {
                        it.isEmpty?.let { isEmpty -> showEmpty(isEmpty) }
                        pbLoading.isVisible(false, rvNotes)
                        debtAdapter.differ.submitList(it.data)
                        debtAdapter.setOperation(object: IPopupDebtOperationFullRealisation{
                            override fun edit(debt: Debt) {
                                val action = ViewAllDebtsFragmentDirections.actionViewAllDebtsFragmentToAddEditDebtFragment(
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
                                viewModel.deleteDebt(debt)
                                Snackbar.make(
                                    binding.root,
                                    activity!!.getString(R.string.delete_debts),
                                    Snackbar.LENGTH_LONG
                                ).show()
                            }

                            @SuppressLint("ResourceType")
                            override fun details(debt: Debt) {
                                val action = ViewAllDebtsFragmentDirections.actionViewAllDebtsFragmentToViewPayForDebtFragment(
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
                                )
                                dialogFragment.show(requireFragmentManager(), "MyDialog")
                            }
                        })
                        rvNotes.apply {
                            layoutManager = LinearLayoutManager(context)
                            adapter = debtAdapter
                        }
//                        var total = 0.0
//                        val isMultiCurrency = it.data?.firstOrNull{ item -> item.account!!.currency!!.currencyCode != it.data.first().account!!.currency!!.currencyCode} != null

//                        if(!isMultiCurrency) it.data?.forEach { items -> total += items.!! }
//                        else it.data?.forEach { items -> total += items.value!! / items.account!!.currency!!.rateToBase }

//                        val currency = if(!isMultiCurrency && it.data!!.isNotEmpty()) it.data.first().account!!.currency!!.currencyCode
//                        else LocalData(requireContext()).getBaseCurrency()

//                        binding.tvTotal.text = String.format("%.2f", total).replace(',', '.') + " " + currency
                    }

                    DataStatus.Status.ERROR -> {
                        pbLoading.isVisible(false, rvNotes)
                        Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }

            fabAddNote.setOnClickListener{
                val action = ViewAllDebtsFragmentDirections.actionViewAllDebtsFragmentToAddEditDebtFragment(
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
        }

        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)//для того что бы дополнить меню
    }
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.more_items_toolbar, menu)
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_search -> {
                search(item)
                true
            }
            R.id.action_sort -> {
                filter()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }


    private fun filter() {
        val isDebtor = DebtRole.valueOf(args.ownerRole) == DebtRole.Debtor
        val builder = AlertDialog.Builder(requireContext())
        val nameAccount = if(GroupAccount.isGroupAccount(args.accountName)) "%%" else args.accountName
        val sortItems = arrayOf("Дата : за зростанням", "Дата : за спаданням", "Сума : за зростанням відсотку сплати", "Сума : за спаданням відсотку сплати")
        builder.setSingleChoiceItems(sortItems, viewModel.getSortMode()!!) { dialog, item ->
            when (item) {
                0 -> viewModel.getFullDebtDetailsDateAsc(args.startDate, args.finishDate, isDebtor, args.nameDebtorOrCreditor, nameAccount)
                1 -> viewModel.getFullDebtDetailsDateDesc(args.startDate, args.finishDate, isDebtor, args.nameDebtorOrCreditor, nameAccount)
                2 -> viewModel.getFullDebtDetailsValueAsc(args.startDate, args.finishDate, isDebtor, args.nameDebtorOrCreditor, nameAccount)
                3 -> viewModel.getFullDebtDetailsValueDesc(args.startDate, args.finishDate, isDebtor, args.nameDebtorOrCreditor, nameAccount)
            }
            viewModel.setSortMode(item)
            dialog.dismiss()
        }
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }
    private fun search(search: MenuItem) {
        val isDebtor = DebtRole.valueOf(args.ownerRole) == DebtRole.Debtor
        val searchView = search.actionView as SearchView
        searchView.queryHint = "Search..."
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }
            override fun onQueryTextChange(newText: String?): Boolean {
                viewModel.getFullDebtDetailsByComment(args.startDate, args.finishDate, isDebtor, args.nameDebtorOrCreditor, args.accountName,
                    if(newText!!.isBlank()) "%" else "%$newText%")
                return true
            }
        })
    }

    private fun showEmpty(isShown: Boolean) {
        binding.apply {
            if (isShown) {
                emptyBody.visibility = View.VISIBLE
                clListBody.visibility = View.GONE
            } else {
                emptyBody.visibility = View.GONE
                clListBody.visibility = View.VISIBLE
            }
        }
    }
}