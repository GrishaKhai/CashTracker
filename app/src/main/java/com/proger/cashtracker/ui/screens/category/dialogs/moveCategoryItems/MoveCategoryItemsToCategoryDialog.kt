package com.proger.cashtracker.ui.screens.category.dialogs.moveCategoryItems

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.proger.cashtracker.R
import com.proger.cashtracker.databinding.DialogMoveCategoryItemsToCategoryBinding
import com.proger.cashtracker.models.Category
import com.proger.cashtracker.models.NoteNotExistInDbException
import com.proger.cashtracker.ui.elements.recyclerView.CategoryAdapter
import com.proger.cashtracker.ui.screens.category.CategoryFragment
import com.proger.cashtracker.ui.screens.category.dialogs.addEdit.AddEditCategoryViewModel
import com.proger.cashtracker.utils.DataStatus
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MoveCategoryItemsToCategoryDialog @Inject constructor() : DialogFragment() {
    private lateinit var binding: DialogMoveCategoryItemsToCategoryBinding
    private val viewModel: MoveCategoryItemsToCategoryViewModel by viewModels()
    @Inject
    lateinit var categoryAdapter: CategoryAdapter

    companion object {
        val NAME_KEY = "nameCategory" //категория от которой будут делаться переводы
        val OPTION_KEY = "option"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogMoveCategoryItemsToCategoryBinding.inflate(inflater)

        binding.apply {
            when(getOption()){
                AddEditCategoryViewModel.Option.Income -> {
                    viewModel.getIncomeCategories()
                    viewModel.incomeCategories.observe(viewLifecycleOwner){
                        buildItems(it, getNameCategory())
                    }
                }
                AddEditCategoryViewModel.Option.Expense -> {
                    viewModel.getExpenseCategories()
                    viewModel.expenseCategories.observe(viewLifecycleOwner){
                        buildItems(it, getNameCategory())
                    }
                }
            }

            cbMoveTransaction.setOnCheckedChangeListener { _, isChecked ->  changeHeightRV(isChecked)}
            btnCancel.setOnClickListener { dismiss() }
            btnSave.setOnClickListener {
                val category = categoryAdapter.getSelectedItem()
                try{
                    if(cbMoveTransaction.isChecked && category != null){
                        when(getOption()){
                            AddEditCategoryViewModel.Option.Income -> viewModel.moveIncomesToNewCategory(getNameCategory(), category.nameCategory)
                            AddEditCategoryViewModel.Option.Expense -> viewModel.moveExpensesToNewCategory(getNameCategory(), category.nameCategory)
                        }
                    } else if(cbMoveTransaction.isChecked && category == null){
                        Toast.makeText(context, "Оберіть категорію для переведення  транзакцій", Toast.LENGTH_SHORT).show()
                        return@setOnClickListener
                    } else {
                        when(getOption()){
                            AddEditCategoryViewModel.Option.Income -> viewModel.moveIncomesToNewCategory(getNameCategory())
                            AddEditCategoryViewModel.Option.Expense -> viewModel.moveExpensesToNewCategory(getNameCategory())
                        }
                    }
                    Toast.makeText(context, requireActivity().getString(R.string.success_delete), Toast.LENGTH_SHORT).show()
                    dismiss()
                }
                catch (e: NoteNotExistInDbException){
                    Toast.makeText(context, "Категорію - ${e.message}, не було знайдено у БД", Toast.LENGTH_SHORT).show()
                }
            }
        }
        return binding.root
    }

    private fun changeHeightRV(isChecked: Boolean){
        val params = binding.rvCategoriesList.layoutParams
        val height = if (!isChecked) 0 else 500
        params?.height = height
        binding.rvCategoriesList.setLayoutParams(params)
    }

    private fun buildItems(data: DataStatus<List<Category>>, excludeCategory: String){
        when (data.status) {
            DataStatus.Status.LOADING -> {}
            DataStatus.Status.SUCCESS -> {
                val _data: ArrayList<Category> = data.data as ArrayList<Category>
                _data.removeIf { it.nameCategory == excludeCategory}
                categoryAdapter.differ.submitList(_data)
                binding.rvCategoriesList.apply {
                    layoutManager = GridLayoutManager(context, 4)
                    adapter = categoryAdapter
                }
            }
            DataStatus.Status.ERROR -> {
                Toast.makeText(context, data.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun getOption() = AddEditCategoryViewModel.Option.valueOf(requireArguments().getString(OPTION_KEY)!!)
    private fun getNameCategory() = requireArguments().getString(NAME_KEY)!!
}