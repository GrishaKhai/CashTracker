package com.proger.cashtracker.ui.screens.tabs.budget.addEdit

import androidx.fragment.app.viewModels
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.proger.cashtracker.R
import com.proger.cashtracker.utils.DateHelper
import com.proger.cashtracker.models.Budget
import com.proger.cashtracker.ui.screens.tabs.AddEditTransactionFragment
import com.proger.cashtracker.ui.screens.tabs.FragmentMode
import com.proger.cashtracker.utils.observeEvent
import dagger.hilt.android.AndroidEntryPoint
import java.util.Date

@AndroidEntryPoint
class AddEditBudgetFragment : AddEditTransactionFragment() {
    private val viewModel: AddEditBudgetViewModel by viewModels()

    //при изменении ориентации пересчитываются эти переменные
    private lateinit var mode: FragmentMode
    private var editBudgetId: Int = -1

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
        setIntervalCalendarListener()

        //установка режима
        val args: AddEditBudgetFragmentArgs by navArgs()
        editBudgetId = args.idNote
        mode = if (editBudgetId > 0) FragmentMode.Edit else FragmentMode.Add

        binding.llSelectAccount.visibility = View.INVISIBLE
        binding.tilDetailsContainer.visibility = View.INVISIBLE

        //todo установка заголовка для окна - не работает корректно
        //todo плохо работает єта попытка изменить заголовок при повороте экрана он возвращается к названию фрагмента
        val actionBar = (activity as AppCompatActivity).supportActionBar
        if(mode == FragmentMode.Edit){
            actionBar?.title = requireContext().getString(R.string.label_budget_edit)
        } else {
            actionBar?.title = requireContext().getString(R.string.label_budget_add)
        }

        val startDate = if(args.startDate == 0L) DateHelper.getStartDay(Date()).time else args.startDate
        val finishDate = if(args.finishDate == 0L) DateHelper.getFinishDay(Date()).time else args.finishDate
        if(baseViewModel.getSelectedIntervalDate() == null)
            baseViewModel.setSelectedIntervalDate(startDate, finishDate)

        //установка значений
        binding.tvSelectDate.text = DateHelper.makeRangeFormat(
            Date(baseViewModel.getSelectedIntervalDate()!!.first),
            Date(baseViewModel.getSelectedIntervalDate()!!.second)
        )
        binding.tietEnterValue.setText(args.value.toDouble().toString())

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
        val budget = Budget(
            dateStart = baseViewModel.getSelectedIntervalDate()!!.first,
            dateFinish = baseViewModel.getSelectedIntervalDate()!!.second,
            budget = if (binding.tietEnterValue.text!!.isBlank()) null else binding.tietEnterValue.text?.toString()
                ?.toDouble(),
            spendCash = 0.0,
            expenseCategory = baseViewModel.getSelectedCategory()
        )
        when (mode) {
            FragmentMode.Add -> viewModel.addBudget(budget)
            FragmentMode.Edit -> viewModel.editBudget(editBudgetId, budget)
        }
    }

    private fun observeState() = viewModel.state.observe(viewLifecycleOwner) { state ->
        val textDialog = getString(resources.getIdentifier("dialog_msg_process", "string", context?.packageName))
        binding.clProgress.isVisible = state.showProgress
        binding.tvMessageProgress.text = textDialog

        binding.tvSelectDate.isEnabled = state.enableViews
        binding.tilEnterValue.isEnabled = state.enableViews
        binding.ivOpenCalculator.isEnabled = state.enableViews
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