package com.proger.cashtracker.ui.screens.tabs.debt.allContributions

import android.annotation.SuppressLint
import android.graphics.Canvas
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.proger.cashtracker.R
import com.proger.cashtracker.databinding.FragmentViewTransactionBinding
import com.proger.cashtracker.utils.DoubleHelper
import com.proger.cashtracker.models.DebtRole
import com.proger.cashtracker.ui.elements.recyclerView.PayAdapter
import com.proger.cashtracker.ui.screens.category.dialogs.addEdit.AddEditCategoryViewModel
import com.proger.cashtracker.ui.screens.tabs.debt.dialog.payDebt.PayDebtDialog
import com.proger.cashtracker.utils.DataStatus
import com.proger.cashtracker.utils.isVisible
import dagger.hilt.android.AndroidEntryPoint
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator
import javax.inject.Inject

@AndroidEntryPoint
class ViewPayForDebtFragment : Fragment(R.layout.fragment_view_transaction) {
    private lateinit var binding: FragmentViewTransactionBinding
    private val viewModel: ViewPayForDebtViewModel by viewModels()
    private val args: ViewPayForDebtFragmentArgs by navArgs()
    @Inject
    lateinit var payAdapter: PayAdapter

    @SuppressLint("ResourceType")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentViewTransactionBinding.inflate(inflater)
        binding.apply {
            viewModel.getContributions(args.debtId.toInt())
            viewModel.foundDebt(args.debtId.toInt())

            fabAddNote.visibility = View.VISIBLE
            fabAddNote.setOnClickListener{
                val dialogFragment = PayDebtDialog()
                dialogFragment.arguments = bundleOf(
                    PayDebtDialog.MODE_KEY to AddEditCategoryViewModel.Mode.Add.toString(),
                    PayDebtDialog.DEBT_ID_KEY to args.debtId.toInt()
                )
                dialogFragment.show(requireFragmentManager(), "MyDialog")
            }

            viewModel.pays.observe(viewLifecycleOwner) { pays ->
                when (pays.status) {
                    DataStatus.Status.LOADING -> {
                        pbLoading.isVisible(true, rvTransaction)
                        emptyBody.isVisible(false, rvTransaction)
                    }

                    DataStatus.Status.SUCCESS -> {
                        pays.isEmpty?.let { isEmpty -> showEmpty(isEmpty) }
                        pbLoading.isVisible(false, rvTransaction)
                        if(pays.isEmpty == false) {
                            payAdapter.setRoleOwner(DebtRole.valueOf(args.roleOwner))
                            payAdapter.differ.submitList(pays.data)
                            rvTransaction.apply {
                                layoutManager = LinearLayoutManager(context)
                                adapter = payAdapter
                            }
                            viewModel.debt.observe(viewLifecycleOwner) {
                                var total = 0.0
                                val fullDebt = DoubleHelper.roundValue(args.debt.toDouble())
                                pays.data!!.forEach { items -> total += items.contribution!! * it.account!!.currency!!.rateToBase / items.account!!.currency!!.rateToBase }
                                val currency = it.account!!.currency!!.currencyCode
                                binding.tvTotal.text = "${DoubleHelper.roundValue(total)}/${fullDebt} $currency"
                            }
                        }
                    }

                    DataStatus.Status.ERROR -> {
                        pbLoading.isVisible(false, rvTransaction)
                        Toast.makeText(context, pays.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }

            val swipeCallback = object :
                ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
                override fun onMove( recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
                    return false
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    val position = viewHolder.adapterPosition
                    val pay = payAdapter.differ.currentList[position]
                    when (direction) {
                        ItemTouchHelper.LEFT -> {
                            viewModel.deleteExpense(pay, getRoleOwner())
                            Snackbar.make(binding.root, activity!!.getString(R.string.delete_pay_debt), Snackbar.LENGTH_LONG)
                                .apply {
                                    setAction(activity!!.getString(R.string.undo)) {
                                        viewModel.recoveryExpense(pay, getRoleOwner(), args.debtId.toInt())
                                    }
                                }.show()
                        }

                        ItemTouchHelper.RIGHT -> {
                            val dialogFragment = PayDebtDialog()
                            dialogFragment.arguments = bundleOf(
                                PayDebtDialog.MODE_KEY to AddEditCategoryViewModel.Mode.Edit.toString(),
                                PayDebtDialog.DEBT_ID_KEY to args.debtId.toInt(),
                                PayDebtDialog.DATE_KEY to pay.date,
                                PayDebtDialog.CONTRIBUTION_ID_KEY to pay.contributionId!!.toInt(),
                                PayDebtDialog.ACCOUNT_NAME_KEY to pay.account!!.name,
                                PayDebtDialog.ENTERED_VALUE_KEY to pay.contribution,
                            )
                            dialogFragment.show(requireFragmentManager(), "MyDialog")
                        }
                    }
                }

                override fun onChildDraw(c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean) {
                    RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
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
                    super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                }
            }
            val itemTouchHelper = ItemTouchHelper(swipeCallback)
            itemTouchHelper.attachToRecyclerView(binding.rvTransaction)
        }
        return binding.root
    }

    private fun getRoleOwner() = DebtRole.valueOf(args.roleOwner)
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