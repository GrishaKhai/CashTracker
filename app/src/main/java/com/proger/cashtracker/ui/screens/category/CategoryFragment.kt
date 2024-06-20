package com.proger.cashtracker.ui.screens.category

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.recyclerview.widget.GridLayoutManager
import com.proger.cashtracker.R
import com.proger.cashtracker.databinding.FragmentViewAllNotesBinding
import com.proger.cashtracker.models.Category
import com.proger.cashtracker.ui.elements.recyclerView.EditCategoryAdapter
import com.proger.cashtracker.ui.screens.category.dialogs.addEdit.AddEditCategoryDialog
import com.proger.cashtracker.ui.screens.category.dialogs.addEdit.AddEditCategoryViewModel
import com.proger.cashtracker.ui.screens.category.dialogs.moveCategoryItems.MoveCategoryItemsToCategoryDialog
import com.proger.cashtracker.utils.DataStatus
import com.proger.cashtracker.utils.isVisible
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class CategoryFragment : Fragment(R.layout.fragment_view_all_notes) {
    private val viewModel: CategoryViewModel by viewModels()
    private lateinit var binding: FragmentViewAllNotesBinding

    @Inject
    lateinit var categoryAdapter: EditCategoryAdapter

    companion object {
        val INCOME_MODE = "income"
        val EXPENSE_MODE = "expense"
        val MODE_KEY = "mode"
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // TODO: Use the ViewModel
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentViewAllNotesBinding.inflate(inflater)
        observe()
        return binding.root
    }

    private fun buildList(category: DataStatus<List<Category>>) {
        binding.apply {
            when (category.status) {
                DataStatus.Status.LOADING -> {
                    pbLoading.isVisible(true, rvNotes)
                    emptyBody.isVisible(false, rvNotes)
                }

                DataStatus.Status.SUCCESS -> {
                    category.isEmpty?.let { isEmpty -> showEmpty(isEmpty) }
                    pbLoading.isVisible(false, rvNotes)
                    categoryAdapter.differ.submitList(category.data)


                    categoryAdapter.setMethodOnItem(object : IEditDeleteItem {
                        override fun edit(category: Category) {
                            invokeEditDialog(category, getMode())
                        }

                        override fun delete(category: Category) {
                            val categoryName = category.nameCategory
                            val idResource = requireContext().resources.getIdentifier(
                                category.image,
                                "drawable",
                                requireContext().packageName
                            )
                            val builder = AlertDialog.Builder(requireContext())
                            builder.setTitle("Видалення категорії")
                                .setMessage("Ви впевнені, що бажаєте видалити категорію $categoryName?")
                                .setIcon(idResource)
                                .setNegativeButton("Ні") { dialog, _ -> dialog.cancel() }
                                .setPositiveButton("Так") { dialog, _ -> invokeDeleteDialog(category, getMode()) }
                            builder.create().show()
                        }
                    })
                    rvNotes.apply {
                        layoutManager = GridLayoutManager(context, 5)
                        adapter = categoryAdapter
                    }
                }

                DataStatus.Status.ERROR -> {
                    pbLoading.isVisible(false, rvNotes)
                    Toast.makeText(context, category.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


    private fun observe() {
        if (getMode() == AddEditCategoryViewModel.Option.Income) {
            viewModel.getIncomeCategories()
            viewModel.incomeCategories.observe(viewLifecycleOwner) {
                buildList(it)
            }
        } else {
            viewModel.getExpenseCategories()
            viewModel.expenseCategories.observe(viewLifecycleOwner) {
                buildList(it)
            }
        }
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

    private fun invokeEditDialog(category: Category, option: AddEditCategoryViewModel.Option) {
        val addEditCategoryDialog = AddEditCategoryDialog()
        val args = Bundle()
        args.putString(AddEditCategoryDialog.MODE_KEY, AddEditCategoryViewModel.Mode.Edit.name)
        args.putString(AddEditCategoryDialog.OPTION_KEY, option.toString())
        args.putString(AddEditCategoryDialog.NAME_KEY, category.nameCategory)
        args.putString(AddEditCategoryDialog.IMAGE_KEY, category.image)

        addEditCategoryDialog.arguments = args
        addEditCategoryDialog.show(requireFragmentManager(), "MyDialog")
    }
    private fun invokeDeleteDialog(category: Category, option: AddEditCategoryViewModel.Option) {
        val moveCategoryItemsToCategoryDialog = MoveCategoryItemsToCategoryDialog()

        moveCategoryItemsToCategoryDialog.arguments = bundleOf(
            MoveCategoryItemsToCategoryDialog.OPTION_KEY to option.toString(),
            MoveCategoryItemsToCategoryDialog.NAME_KEY to category.nameCategory
        )
        moveCategoryItemsToCategoryDialog.show(requireFragmentManager(), "MyDialog")
    }

    private fun getMode(): AddEditCategoryViewModel.Option {
        return if (requireArguments().getString(MODE_KEY) == EXPENSE_MODE)
            AddEditCategoryViewModel.Option.Expense else AddEditCategoryViewModel.Option.Income
    }
}