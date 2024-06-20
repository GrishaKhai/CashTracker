package com.proger.cashtracker.ui.screens.tabs

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.annotation.StringRes
import androidx.core.util.Pair
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.textfield.TextInputLayout
import com.proger.cashtracker.R
import com.proger.cashtracker.databinding.FragmentAddEditTransactionBinding
import com.proger.cashtracker.utils.DateHelper
import com.proger.cashtracker.utils.DateHelper.Companion.getFinishDay
import com.proger.cashtracker.utils.DateHelper.Companion.getStartDay
import com.proger.cashtracker.utils.DateHelper.Companion.makeRangeFormat
import com.proger.cashtracker.models.Account
import com.proger.cashtracker.models.Category
import com.proger.cashtracker.ui.dialog.calculator.CalculatorContract
import com.proger.cashtracker.ui.dialog.calculator.CalculatorDialog
import com.proger.cashtracker.ui.elements.recyclerView.CategoryWithAddOptionAdapter
import com.proger.cashtracker.ui.elements.spinner.SpinnerAdapter
import com.proger.cashtracker.ui.screens.category.dialogs.addEdit.AddEditCategoryDialog
import com.proger.cashtracker.ui.screens.category.dialogs.addEdit.AddEditCategoryViewModel
import com.proger.cashtracker.utils.NO_ERROR_MESSAGE
import java.util.Date


abstract class AddEditTransactionFragment : Fragment(R.layout.fragment_add_edit_transaction) {
    private lateinit var calculator: CalculatorContract
    val baseViewModel: AddEditTransactionViewModel by viewModels()
    lateinit var binding: FragmentAddEditTransactionBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddEditTransactionBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setCalculatorListener()
    }

    override fun onDestroy() {
        super.onDestroy()
        if(binding.tietEnterValue.text.toString().isNotBlank()) {
            baseViewModel.setEnteredValue(binding.tietEnterValue.text.toString().toDouble())
        }
        calculator.setCalculatorValue(null)
        calculator.data.removeObservers(requireActivity())
    }

    fun initArguments(date: Long, value: Double, details: String?){
        binding.tvSelectDate.text = DateHelper.makeDayMonthYearFormat(Date(date))
        binding.tietEnterValue.setText(value.toString())
        binding.tietDetails.setText(details?: "")
    }

    fun setCalculatorListener() {
        binding.ivOpenCalculator.setOnClickListener {
            val _value = binding.tietEnterValue.text.toString()
            val value = if(_value.isBlank()) 0.0 else _value.toDouble()
            calculator.setCalculatorValue(value)
            val dialogFragment = CalculatorDialog()
            dialogFragment.show(requireFragmentManager(), "MyDialog")
        }
    }
    fun setCalendarListener() {
        binding.tvSelectDate.setOnClickListener {
            val materialDatePicker: MaterialDatePicker<*> =
                MaterialDatePicker.Builder.datePicker().build()
            materialDatePicker.addOnPositiveButtonClickListener { selection: Any? ->
                if (selection != null) {
                    binding.tvSelectDate.text =
                        arguments?.let {
                            DateHelper.makeDayMonthYearFormat(Date(selection as Long))
                        }
                    baseViewModel.setSelectedDate(selection as Long)
                }
            }
            materialDatePicker.show(requireFragmentManager(), materialDatePicker.toString())
        }
    }
    fun setIntervalCalendarListener() {
        binding.tvSelectDate.setOnClickListener {
            val datePicker = MaterialDatePicker.Builder.dateRangePicker().build()
            datePicker.addOnPositiveButtonClickListener { selection: Pair<Long?, Long?> ->
                if (selection.first != null && selection.second != null) {
                    baseViewModel.setSelectedIntervalDate(getStartDay(Date(selection.first!!)).time, getFinishDay(Date(selection.second!!)).time)
                    binding.tvSelectDate.text = makeRangeFormat(
                        Date(baseViewModel.getSelectedIntervalDate()!!.first),
                        Date(baseViewModel.getSelectedIntervalDate()!!.second)
                    )
                }
            }
            datePicker.show(requireFragmentManager(), datePicker.toString())
        }
    }


    @SuppressLint("SetTextI18n")
    fun renderAccounts(account: List<Account>, selectedAccount: String?) {
        val spinner = binding.spinnerAccount
        val adapter = SpinnerAdapter(context, account.toTypedArray(), layoutInflater)
        spinner.adapter = null
        spinner.adapter = adapter
        spinner.setPromptId(R.string.choose_account)
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?, view: View?, position: Int, id: Long
            ) {
                val selectedItem = spinner.selectedItem as Account
                baseViewModel.setSelectedAccount(selectedItem.name)
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
        if(selectedAccount != null) selectAccount(account, selectedAccount)
        else spinner.setSelection(0)
    }
    fun renderIncomeCategories(categories: List<Category>, selectedCategory: String?){
        renderCategories(AddEditCategoryViewModel.Option.Income, categories, selectedCategory)
    }
    fun renderExpenseCategories(categories: List<Category>, selectedCategory: String?){
        renderCategories(AddEditCategoryViewModel.Option.Expense, categories, selectedCategory)
    }
    private fun renderCategories(option: AddEditCategoryViewModel.Option, categories: List<Category>, selectedCategory: String?) {
        val impl = object : CategoryWithAddOptionAdapter.OnCategoryClickListener {
            override fun onCategoryClick(category: Category) {
                baseViewModel.setSelectedCategory(category)
            }
            override fun onShowAddEditCategoryDialog() {
                val addEditCategoryDialog = AddEditCategoryDialog()
                val args = Bundle()
                args.putString(AddEditCategoryDialog.MODE_KEY, AddEditCategoryViewModel.Mode.Add.name)
                args.putString(AddEditCategoryDialog.OPTION_KEY, option.name)

                addEditCategoryDialog.arguments = args
                addEditCategoryDialog.show(requireFragmentManager(), "MyDialog")
            }
        }
        binding.rvCategoriesList.adapter = CategoryWithAddOptionAdapter(categories, impl)
        if(selectedCategory != null) selectCategory(categories, selectedCategory)
    }

    private fun selectAccount(accounts: List<Account>, account: String) {
        val position = accounts.indexOfFirst { it.name == account }
        if (position != -1) binding.spinnerAccount.setSelection(position)
    }
    private fun selectCategory(categories: List<Category>, category: String) {
        val position = categories.indexOfFirst { it.nameCategory == category }
        if(position != -1){
            binding.rvCategoriesList.scrollToPosition(position)// Прокрутить RecyclerView к нужной позиции
            //Выделение элемента
            val adapter: RecyclerView.Adapter<*>? = binding.rvCategoriesList.adapter
            if (adapter is CategoryWithAddOptionAdapter) {
                (adapter as CategoryWithAddOptionAdapter).selectItem(position)
                baseViewModel.setSelectedCategory(categories[position])
            }
        }
    }

    fun fillError(input: TextInputLayout, @StringRes stringRes: Int) {
        if (stringRes == NO_ERROR_MESSAGE) {
            input.error = null
            input.isErrorEnabled = false
        } else {
            input.error = getString(stringRes)
            input.isErrorEnabled = true
        }
    }

    fun observeCalculator(value: Double){
        calculator = ViewModelProvider(requireActivity()).get(CalculatorContract::class.java)

        calculator.setCalculatorValue(baseViewModel.getEnteredValue() ?: value)
        calculator.data.observe(requireActivity()) {
            val showValue = it != null
            if(showValue) {
                binding.tietEnterValue.setText(String.format("%.2f", it).replace(',', '.'))
            }
        }
    }
}