package com.proger.cashtracker.ui.screens.account

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
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.proger.cashtracker.R
import com.proger.cashtracker.databinding.FragmentViewAllNotesBinding
import com.proger.cashtracker.ui.elements.recyclerView.AccountAdapter
import com.proger.cashtracker.ui.screens.account.dialogs.addEdit.AddEditAccountDialog
import com.proger.cashtracker.ui.screens.category.dialogs.addEdit.AddEditCategoryViewModel
import com.proger.cashtracker.ui.screens.tabs.debt.dialog.payDebt.PayDebtDialog
import com.proger.cashtracker.utils.DataStatus
import com.proger.cashtracker.utils.isVisible
import dagger.hilt.android.AndroidEntryPoint
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator
import javax.inject.Inject

@AndroidEntryPoint
class AccountsFragment : Fragment(R.layout.fragment_view_all_notes) {
    lateinit var binding: FragmentViewAllNotesBinding
    private val viewModel: AccountsViewModel by viewModels()


    @Inject
    lateinit var accountAdapter: AccountAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentViewAllNotesBinding.inflate(inflater)

//        if(viewModel.accounts == null) {
//            viewModel.getAccounts()
//        }
        viewModel.getAccounts()

        binding.apply {
            viewModel.accounts.observe(viewLifecycleOwner){
                when (it.status) {
                    DataStatus.Status.LOADING -> {
                        pbLoading.isVisible(true, rvNotes)
                        emptyBody.isVisible(false, rvNotes)
                    }

                    DataStatus.Status.SUCCESS -> {
                        it.isEmpty?.let { isEmpty -> showEmpty(isEmpty) }
                        pbLoading.isVisible(false, rvNotes)
                        accountAdapter.differ.submitList(it.data)
                        rvNotes.apply {
                            layoutManager = LinearLayoutManager(requireContext())
                            adapter = accountAdapter
                        }
                    }

                    DataStatus.Status.ERROR -> {
                        pbLoading.isVisible(false, rvNotes)
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
                    val account = accountAdapter.differ.currentList[position]
                    when (direction) {
                        ItemTouchHelper.LEFT -> {
                            viewModel.changeStateAccount(account.name, false)
                            Snackbar.make(binding.root, activity!!.getString(R.string.delete_account), Snackbar.LENGTH_LONG)
                                .apply {
                                    setAction(activity!!.getString(R.string.undo)) {
                                        viewModel.changeStateAccount(account.name, true)
                                    }
                                }.show()
                        }

                        ItemTouchHelper.RIGHT -> {
                            val dialogFragment = AddEditAccountDialog()
                            dialogFragment.arguments = bundleOf(
                                AddEditAccountDialog.MODE_KEY to AddEditCategoryViewModel.Mode.Edit.toString(),
                                AddEditAccountDialog.NAME_KEY to account.name,
                                AddEditAccountDialog.BALANCE_KEY to account.balance,
                                AddEditAccountDialog.CURRENCY_KEY to account.currency!!.currencyCode,
                                AddEditAccountDialog.IMAGE_KEY to account.image
                            )
                            dialogFragment.show(requireFragmentManager(), "MyDialog")
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
            itemTouchHelper.attachToRecyclerView(binding.rvNotes)
        }

        binding.fabAddNote.setOnClickListener{
            val dialogFragment = AddEditAccountDialog()
            dialogFragment.arguments = bundleOf(
                AddEditAccountDialog.MODE_KEY to AddEditCategoryViewModel.Mode.Add.toString(),
            )
            dialogFragment.show(requireFragmentManager(), "MyDialog")
        }

        return binding.root
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