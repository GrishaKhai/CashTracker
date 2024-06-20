package com.proger.cashtracker.ui.screens.tabs.budget.allNotes

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
import com.proger.cashtracker.utils.LocalData
import com.proger.cashtracker.ui.elements.recyclerView.BudgetAdapter
import com.proger.cashtracker.utils.DataStatus
import com.proger.cashtracker.utils.isVisible
import dagger.hilt.android.AndroidEntryPoint
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator
import javax.inject.Inject

@AndroidEntryPoint
class ViewAllBudgetsFragment : Fragment(){
    private lateinit var binding: FragmentViewTransactionBinding
    private val viewModel: ViewAllBudgetsViewModel by viewModels()
    private val args: ViewAllBudgetsFragmentArgs by navArgs()

    @Inject
    lateinit var budgetAdapter: BudgetAdapter

    @SuppressLint("ResourceType")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentViewTransactionBinding.inflate(inflater)
        binding.apply {
            if(viewModel.getSortMode() == null) {
                viewModel.getFullBudgetDetailsDateAsc(
                    args.startDate,
                    args.finishDate,
                    args.categoryName
                )
                viewModel.setSortMode(0)
            }

            viewModel.budgets.observe(viewLifecycleOwner) { it ->
                when (it.status) {
                    DataStatus.Status.LOADING -> {
                        pbLoading.isVisible(true, rvTransaction)
                        emptyBody.isVisible(false, rvTransaction)
                    }

                    DataStatus.Status.SUCCESS -> {
                        it.isEmpty?.let { isEmpty -> showEmpty(isEmpty) }
                        pbLoading.isVisible(false, rvTransaction)
                        budgetAdapter.differ.submitList(it.data)
                        rvTransaction.apply {
                            layoutManager = LinearLayoutManager(context)
                            adapter = budgetAdapter
                        }


                        var totalSpend = 0.0
                        var totalBudget = 0.0

                        val currency = LocalData(requireContext()).getBaseCurrency().toString()
                        it.data?.forEach {
                            totalSpend += it.spendCash!!
                            totalBudget += it.budget!!
                        }

                        val _totalSpend = String.format("%.2f", totalSpend).replace(',', '.')
                        val _totalBudget = String.format("%.2f", totalBudget).replace(',', '.')

                        val content = if (it.data != null) "$_totalSpend/$_totalBudget $currency" else requireActivity().getString(R.string.budget_empty)

                        binding.tvTotal.text = content
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
                    val budget = budgetAdapter.differ.currentList[position]
                    when (direction) {
                        ItemTouchHelper.LEFT -> {
                            viewModel.deleteBudget(budget)
                            Snackbar.make(binding.root, activity!!.getString(R.string.delete_budget), Snackbar.LENGTH_LONG)
                                .apply {
                                    setAction(activity!!.getString(R.string.undo)) {
                                        viewModel.recoveryBudget(budget)
                                    }
                                }.show()
                        }

                        ItemTouchHelper.RIGHT -> {
                            val action =
                                ViewAllBudgetsFragmentDirections.actionViewAllBudgetsFragmentToAddEditBudgetTransactionFragment(
                                    idNote = budget.id!!.toInt(),
                                    startDate = budget.dateStart!!,
                                    finishDate = budget.dateFinish!!,
                                    value = budget.budget!!.toFloat(),
                                    category = budget.expenseCategory!!.nameCategory
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
        }
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)//для того что бы дополнить меню
    }
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.sort_toolbar, menu)
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_sort -> {
                filter()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun filter() {
        val builder = AlertDialog.Builder(requireContext())
        val sortItems = arrayOf("Дата : за зростанням", "Дата : за спаданням", "Бюджет : за зростанням", "Бюджет : за спаданням")
        builder.setSingleChoiceItems(sortItems, viewModel.getSortMode()!!) { dialog, item ->
            when (item) {
                0 -> {
                    viewModel.getFullBudgetDetailsDateAsc(args.startDate, args.finishDate, args.categoryName)
                }
                1 -> {
                    viewModel.getFullBudgetDetailsDateDesc(args.startDate, args.finishDate, args.categoryName)
                }
                2 -> {
                    viewModel.getFullBudgetDetailsValueAsc(args.startDate, args.finishDate, args.categoryName)
                }
                3 -> {
                    viewModel.getFullBudgetDetailsValueDesc(args.startDate, args.finishDate, args.categoryName)
                }
            }
            viewModel.setSortMode(item)
            dialog.dismiss()
        }
        val dialog: AlertDialog = builder.create()
        dialog.show()
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