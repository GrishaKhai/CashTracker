package com.proger.cashtracker.ui.screens.tabs.expense.addEdit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.proger.cashtracker.R
import com.proger.cashtracker.models.Account
import com.proger.cashtracker.models.Transaction
import com.proger.cashtracker.ui.screens.tabs.AddEditTransactionFragment
import com.proger.cashtracker.ui.screens.tabs.FragmentMode
import com.proger.cashtracker.utils.observeEvent
import dagger.hilt.android.AndroidEntryPoint
import java.util.Date

@AndroidEntryPoint
class AddEditExpenseTransactionFragment : AddEditTransactionFragment() {
    val viewModel: AddEditExpenseTransactionViewModel by viewModels()//viewModel для общей привязки окна

    //при изменении ориентации пересчитываются эти переменные
    private lateinit var mode: FragmentMode
    private var editExpenseId: Int = -1

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setCalendarListener()
        //установка значений
        val args: AddEditExpenseTransactionFragmentArgs by navArgs()
        editExpenseId = args.idNote
        mode = if (editExpenseId > 0) FragmentMode.Edit else FragmentMode.Add

        //установка заголовка для окна
        val actionBar = (activity as AppCompatActivity).supportActionBar
        if(mode == FragmentMode.Edit){
            actionBar?.title = requireContext().getString(R.string.label_expense_edit)
        } else {
            actionBar?.title = requireContext().getString(R.string.label_expense_add)
        }

        val date = if(args.date == 0L) Date().time else args.date
        baseViewModel.setSelectedDate(baseViewModel.getSelectedDate() ?: date)//todo исправь этот момент, на условие если равно налу то присвой иначе ничего

        initArguments(
            baseViewModel.getSelectedDate()!!, args.value.toDouble(), args.details
        )

        //создание items для выпадающего списка
        viewModel.accounts.observe(viewLifecycleOwner) {
            renderAccounts(
                it,
                baseViewModel.getSelectedAccount() ?: args.account
            )
        }
        //создание items для списка категорий
        viewModel.categories.observe(viewLifecycleOwner) {
            renderExpenseCategories(
                it,
                baseViewModel.getSelectedCategory()?.nameCategory ?: args.category
            )
        }

        //обработчик нажатий сохранить
        binding.btnSave.setOnClickListener {
            saveNote()
        }

        //установка наблюдателей
        observeCalculator(args.value.toDouble())
        observeState()
        observeGoBackEvent()
        observeShowMessageEvent()
    }

    private fun saveNote() {
        val expense = Transaction(
            date = baseViewModel.getSelectedDate(),
            comment = binding.tietDetails.text.toString(),
            value = if (binding.tietEnterValue.text!!.isBlank()) null else binding.tietEnterValue.text?.toString()
                ?.toDouble(),
            category = baseViewModel.getSelectedCategory(),
            account = binding.spinnerAccount.selectedItem as Account
        )
        when (mode) {
            FragmentMode.Add -> viewModel.addExpense(expense)
            FragmentMode.Edit -> viewModel.editExpense(editExpenseId, expense)
        }
    }

    private fun observeState() = viewModel.state.observe(viewLifecycleOwner) { state ->
        val textDialog = getString(resources.getIdentifier("dialog_msg_process", "string", context?.packageName))
        binding.clProgress.isVisible = state.showProgress
        binding.tvMessageProgress.text = textDialog

        binding.tvSelectDate.isEnabled = state.enableViews
        binding.spinnerAccount.isEnabled = state.enableViews
        binding.tilEnterValue.isEnabled = state.enableViews
        binding.ivOpenCalculator.isEnabled = state.enableViews
        binding.tilDetailsContainer.isEnabled = state.enableViews
        binding.rvCategoriesList.isEnabled = state.enableViews
        binding.btnSave.isEnabled = state.enableViews

        fillError(binding.tilEnterValue, state.valueTransactionMessageRes)
    }
    private fun observeShowMessageEvent() =
        viewModel.showToastEvent.observeEvent(viewLifecycleOwner) {
            Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
        }
    private fun observeGoBackEvent() = viewModel.goBackEvent.observeEvent(viewLifecycleOwner) {
        findNavController().popBackStack()
    }
}