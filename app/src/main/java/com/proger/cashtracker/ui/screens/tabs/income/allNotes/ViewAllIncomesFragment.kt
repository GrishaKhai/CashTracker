package com.proger.cashtracker.ui.screens.tabs.income.allNotes

import android.annotation.SuppressLint
import android.graphics.Canvas
import android.graphics.Color
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
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.proger.cashtracker.R
import com.proger.cashtracker.databinding.FragmentViewTransactionBinding
import com.proger.cashtracker.utils.DoubleHelper
import com.proger.cashtracker.utils.LocalData
import com.proger.cashtracker.models.Transaction
import com.proger.cashtracker.ui.elements.recyclerView.TransactionAdapter
import com.proger.cashtracker.ui.screens.GroupAccount
import com.proger.cashtracker.ui.screens.tabs.IDuplicateNote
import com.proger.cashtracker.utils.DataStatus
import com.proger.cashtracker.utils.isVisible
import dagger.hilt.android.AndroidEntryPoint
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator
import javax.inject.Inject

@AndroidEntryPoint
class ViewAllIncomesFragment() : Fragment() {
    private lateinit var binding: FragmentViewTransactionBinding
    private val viewModel: ViewAllIncomesViewModel by viewModels()
    private val args: ViewAllIncomesFragmentArgs by navArgs()

    @Inject
    lateinit var transactionAdapter: TransactionAdapter


    @SuppressLint("ResourceType")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentViewTransactionBinding.inflate(inflater)
        binding.apply {
            val nameAccount = if(GroupAccount.isGroupAccount(args.accountName)) "%%" else args.accountName

            if(viewModel.getSortMode() == null) {
                viewModel.getFullIncomeDetailsDateAsc(
                    args.startDate,
                    args.finishDate,
                    nameAccount,
                    args.categoryName
                )
                viewModel.setSortMode(0)
            }

            viewModel.incomes.observe(viewLifecycleOwner) {
                when (it.status) {
                    DataStatus.Status.LOADING -> {
                        pbLoading.isVisible(true, rvTransaction)
                        emptyBody.isVisible(false, rvTransaction)
                    }

                    DataStatus.Status.SUCCESS -> {
                        it.isEmpty?.let { isEmpty -> showEmpty(isEmpty) }
                        pbLoading.isVisible(false, rvTransaction)
                        transactionAdapter.differ.submitList(it.data)
                        transactionAdapter.setDuplicate(object : IDuplicateNote<Transaction> {
                            override fun duplicate(note: Transaction) {
                                viewModel.duplicateIncome(note)
                            }
                        })
                        rvTransaction.apply {
                            layoutManager = LinearLayoutManager(context)
                            adapter = transactionAdapter
                        }
                        var total = 0.0
                        val isMultiCurrency = it.data?.firstOrNull{ item -> item.account!!.currency!!.currencyCode != it.data.first().account!!.currency!!.currencyCode} != null

                        if(!isMultiCurrency) it.data?.forEach { items -> total += items.value!! }
                        else it.data?.forEach { items -> total += items.value!! / items.account!!.currency!!.rateToBase }

                        val currency = if(!isMultiCurrency && it.data!!.isNotEmpty()) it.data.first().account!!.currency!!.currencyCode
                        else LocalData(requireContext()).getBaseCurrency()

                        binding.tvTotal.text = "${DoubleHelper.roundValue(total)} $currency"
                    }

                    DataStatus.Status.ERROR -> {
                        pbLoading.isVisible(false, rvTransaction)
                        Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }

            val swipeCallback = object :
                ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {
                    return false
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    val position = viewHolder.adapterPosition
                    val transaction = transactionAdapter.differ.currentList[position]
                    when (direction) {
                        ItemTouchHelper.LEFT -> {
                            viewModel.deleteIncome(transaction)
                            Snackbar.make(binding.root, activity!!.getString(R.string.delete_income), Snackbar.LENGTH_LONG)
                                .apply {
                                    setAction(activity!!.getString(R.string.undo)) {
                                        viewModel.recoveryIncome(transaction)
                                    }
                                }.show()
                        }

                        ItemTouchHelper.RIGHT -> {
                            val action =
                                ViewAllIncomesFragmentDirections.actionViewAllIncomesFragmentToAddEditIncomeTransactionFragment(
                                    idNote = transaction.id!!.toInt(),
                                    date = transaction.date!!,
                                    account = transaction.account?.name,
                                    value = transaction.value!!.toFloat(),
                                    details = transaction.comment,
                                    category = transaction.category?.nameCategory
                                )
                            findNavController().navigate(action)
                        }
                    }
                }

                override fun onChildDraw(
                    c: Canvas,
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    dX: Float,
                    dY: Float,
                    actionState: Int,
                    isCurrentlyActive: Boolean
                ) {
                    RecyclerViewSwipeDecorator.Builder(
                        c,
                        recyclerView,
                        viewHolder,
                        dX,
                        dY,
                        actionState,
                        isCurrentlyActive
                    )
                        .addSwipeLeftLabel("Delete")
                        .addSwipeLeftBackgroundColor(Color.RED)
                        .addSwipeLeftActionIcon(R.drawable.ic_delete)
                        .setSwipeLeftLabelColor(Color.WHITE)
                        .setSwipeLeftActionIconTint(Color.WHITE)
                        .addSwipeRightLabel("Edit")
                        .addSwipeRightBackgroundColor(Color.GREEN)
                        .setSwipeRightLabelColor(Color.WHITE)
                        .setSwipeRightActionIconTint(Color.WHITE)
                        .addSwipeRightActionIcon(R.drawable.ic_edit)
                        .create()
                        .decorate()
                    super.onChildDraw(
                        c,
                        recyclerView,
                        viewHolder,
                        dX,
                        dY,
                        actionState,
                        isCurrentlyActive
                    )
                }

            }

            val itemTouchHelper = ItemTouchHelper(swipeCallback)
            itemTouchHelper.attachToRecyclerView(binding.rvTransaction)

            observeDuplicateEvent()
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
        val builder = AlertDialog.Builder(requireContext())
        val nameAccount = if(GroupAccount.isGroupAccount(args.accountName)) "%%" else args.accountName
        val sortItems = arrayOf("Дата : за зростанням", "Дата : за спаданням", "Сума : за зростанням", "Сума : за спаданням")
        builder.setSingleChoiceItems(sortItems, viewModel.getSortMode()!!) { dialog, item ->
            when (item) {
                0 -> {
                    viewModel.getFullIncomeDetailsDateAsc(args.startDate, args.finishDate, nameAccount, args.categoryName)
                }
                1 -> {
                    viewModel.getFullIncomeDetailsDateDesc(args.startDate, args.finishDate, nameAccount, args.categoryName)
                }
                2 -> {
                    viewModel.getFullIncomeDetailsValueAsc(args.startDate, args.finishDate, nameAccount, args.categoryName)
                }
                3 -> {
                    viewModel.getFullIncomeDetailsValueDesc(args.startDate, args.finishDate, nameAccount, args.categoryName)
                }
            }
            viewModel.setSortMode(item)
            dialog.dismiss()
        }
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }
    private fun search(search: MenuItem) {
        val searchView = search.actionView as SearchView
        searchView.queryHint = "Search..."
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }
            override fun onQueryTextChange(newText: String?): Boolean {
                viewModel.getFullIncomeDetailsByComment(args.startDate, args.finishDate, args.accountName, args.categoryName,
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


    private fun observeDuplicateEvent(){
        viewModel.duplicateEvent.observe(viewLifecycleOwner) {
            val action =
                ViewAllIncomesFragmentDirections.actionViewAllIncomesFragmentToAddEditIncomeTransactionFragment(
                    idNote = it.id!!.toInt(),
                    date = it.date!!,
                    account = it.account?.name,
                    value = it.value!!.toFloat(),
                    details = it.comment,
                    category = it.category?.nameCategory
                )
            findNavController().navigate(action)
            viewModel.clearDuplicate()
        }
    }
}