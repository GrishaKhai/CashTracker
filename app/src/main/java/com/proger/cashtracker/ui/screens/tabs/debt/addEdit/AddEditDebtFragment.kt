package com.proger.cashtracker.ui.screens.tabs.debt.addEdit

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.Nullable
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.textfield.TextInputLayout
import com.proger.cashtracker.R
import com.proger.cashtracker.databinding.FragmentAddEditDebtBinding
import com.proger.cashtracker.utils.DateHelper.Companion.getFinishDay
import com.proger.cashtracker.utils.DateHelper.Companion.getStartDay
import com.proger.cashtracker.utils.DateHelper.Companion.makeRangeFormat
import com.proger.cashtracker.models.Account
import com.proger.cashtracker.models.Debt
import com.proger.cashtracker.models.DebtRole
import com.proger.cashtracker.models.Debtor
import com.proger.cashtracker.ui.dialog.calculator.CalculatorContract
import com.proger.cashtracker.ui.dialog.calculator.CalculatorDialog
import com.proger.cashtracker.ui.elements.spinner.SpinnerAdapter
import com.proger.cashtracker.ui.screens.tabs.FragmentMode
import com.proger.cashtracker.utils.NO_ERROR_MESSAGE
import com.proger.cashtracker.utils.observeEvent
import dagger.hilt.android.AndroidEntryPoint
import java.util.Date


@AndroidEntryPoint
class AddEditDebtFragment : Fragment(R.layout.fragment_add_edit_debt) {
    private val viewModel: AddEditDebtViewModel by viewModels()
    private lateinit var binding: FragmentAddEditDebtBinding
    private lateinit var calculator: CalculatorContract


    //при изменении ориентации пересчитываются эти переменные
    private lateinit var mode: FragmentMode
    private var editDebtId: Int = -1

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        binding = FragmentAddEditDebtBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setIntervalCalendarListener()
        setCalculatorListener()

        //установка значений
        val args: AddEditDebtFragmentArgs by navArgs()
        editDebtId = args.idNote
        mode = if (editDebtId > 0) FragmentMode.Edit else FragmentMode.Add

        //установка заголовка для окна
        val actionBar = (activity as AppCompatActivity).supportActionBar
        if(mode == FragmentMode.Edit){
            actionBar?.title = requireContext().getString(R.string.label_debt_edit)
        } else {
            actionBar?.title = requireContext().getString(R.string.label_debt_add)
        }

        val startDate = if(args.startDate == 0L) Date().time else args.startDate
        val finishDate = if(args.finishDate == 0L) Date().time else args.finishDate
        viewModel.setSelectedIntervalDate(viewModel.getSelectedIntervalDate() ?: Pair(startDate, finishDate))

        val roleOwner: DebtRole = if(args.ownerRole == null) DebtRole.Creditor else DebtRole.valueOf(args.ownerRole!!)

        initArguments(
            date = viewModel.getSelectedIntervalDate()!!,
            args.value.toDouble(),
            args.details,
            roleOwner,
            args.nameDebtorOrCreditor ?: ""
        )

        //создание items для выпадающего списка
        viewModel.accounts.observe(viewLifecycleOwner) {
            renderAccounts(
                it,
                viewModel.getSelectedAccount()?.name ?: args.account
            )
        }
        renderSpinnerRoleOwner(roleOwner)

        //обработчик нажатий сохранить
        binding.btnSave.setOnClickListener {
            saveNote()
        }

        //установка наблюдателей
        observeAutoCompleteTextView()
        observeCalculator(args.value.toDouble())
        observeState()
        observeGoBackEvent()
        observeShowMessageEvent()
    }

    private fun initArguments(date: Pair<Long, Long>, value: Double, details: String?, debtRole: DebtRole, nameDebtorOrCreditor: String){
        binding.tvSelectDate.text = makeRangeFormat(Date(date.first), Date(date.second))
        binding.tietEnterValue.setText(value.toString())
        binding.tietDetails.setText(details?: "")

        binding.actvNameDebtorOrCreditor.hint =
            if(debtRole == DebtRole.Debtor) requireContext().getString(R.string.debtor)
            else requireContext().getString(R.string.creditor)
        binding.actvNameDebtorOrCreditor.setText(nameDebtorOrCreditor)
    }

    override fun onDestroy() {
        super.onDestroy()
        if(binding.tietEnterValue.text.toString().isNotBlank()) {
            viewModel.setEnteredValue(binding.tietEnterValue.text.toString().toDouble())
        }
        calculator.setCalculatorValue(null)
        calculator.data.removeObservers(requireActivity())
    }

    private fun saveNote() {
        val roleOwner : DebtRole = if (requireContext().getString(R.string.debtor) == binding.spinnerRoleOwner.selectedItem as String)
            DebtRole.Debtor else DebtRole.Creditor

        val debt = Debt(
            id = null,
            debt = if (binding.tietEnterValue.text!!.isBlank()) null else binding.tietEnterValue.text?.toString()
                ?.toDouble(),
            startDate = viewModel.getSelectedIntervalDate()!!.first,
            finishDate = viewModel.getSelectedIntervalDate()!!.second,
            comment = binding.tietDetails.text.toString(),
            isReturned = false,
            creditorOrDebtor = Debtor(getDebtorOrCreditor()),
            isDebtor = roleOwner == DebtRole.Debtor,
            account = viewModel.getSelectedAccount()
        )

        when (mode) {
            FragmentMode.Add -> viewModel.addDebt(debt, roleOwner)
            FragmentMode.Edit -> viewModel.editDebt(editDebtId, debt, roleOwner)
        }
    }

    private fun renderSpinnerRoleOwner(roleOwner: DebtRole){
        val spinner = binding.spinnerRoleOwner
        val items = listOf(
            requireContext().getString(DebtRole.Debtor.role),
            requireContext().getString(DebtRole.Creditor.role)
        )
        val adapter = ArrayAdapter<String>(
            requireActivity().applicationContext,
            android.R.layout.simple_spinner_item,
            items
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
//        adapter.getDropDownView().setBackgroundColor(R.color.white)



        spinner.adapter = adapter
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) { }
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if(parent?.context != null){
                    binding.actvNameDebtorOrCreditor.hint = if(position == 0) parent.context.getString(DebtRole.Creditor.role)
                    else parent.context.getString(DebtRole.Debtor.role)
                }
            }
        }
        spinner.setSelection(if(roleOwner == DebtRole.Debtor) 0 else 1)
    }
    private fun renderAccounts(account: List<Account>, selectedAccount: String?) {
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
                viewModel.setSelectedAccount(selectedItem)
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
        if(selectedAccount != null) selectAccount(account, selectedAccount)
        else spinner.setSelection(0)
    }
    private fun selectAccount(accounts: List<Account>, account: String) {
        val position = accounts.indexOfFirst { it.name == account }
        if (position != -1) binding.spinnerAccount.setSelection(position)
    }

    private fun getDebtorOrCreditor(): String = binding.actvNameDebtorOrCreditor.text.toString()

    private fun setCalculatorListener() {
        binding.ivOpenCalculator.setOnClickListener {
            val _value = binding.tietEnterValue.text.toString()
            val value = if(_value.isBlank()) 0.0 else _value.toDouble()
            calculator.setCalculatorValue(value)
            val dialogFragment = CalculatorDialog()
            dialogFragment.show(requireFragmentManager(), "MyDialog")
        }
    }
    private fun setIntervalCalendarListener() {
        binding.tvSelectDate.setOnClickListener {
            val datePicker = MaterialDatePicker.Builder.dateRangePicker().build()
            datePicker.addOnPositiveButtonClickListener { selection ->
                if (selection.first != null && selection.second != null) {
                    viewModel.setSelectedIntervalDate(getStartDay(Date(selection.first!!)).time!!, getFinishDay(Date(selection.second!!)).time!!)
                    binding.tvSelectDate.text = makeRangeFormat(
                        Date(viewModel.getSelectedIntervalDate()!!.first),
                        Date(viewModel.getSelectedIntervalDate()!!.second)
                    )
                }
            }
            datePicker.show(requireFragmentManager(), datePicker.toString())
        }
    }

    private fun observeShowMessageEvent() =
        viewModel.showToastEvent.observeEvent(viewLifecycleOwner) {
            Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
        }
    private fun observeGoBackEvent() = viewModel.goBackEvent.observeEvent(viewLifecycleOwner) {
        findNavController().popBackStack()
    }
    private fun observeState() = viewModel.state.observe(viewLifecycleOwner) { state ->
        val textDialog = getString(resources.getIdentifier("dialog_msg_process", "string", context?.packageName))
        binding.clProgress.isVisible = state.showProgress
        binding.tvMessageProgress.text = textDialog

        binding.tvSelectDate.isEnabled = state.enableViews
        binding.spinnerAccount.isEnabled = state.enableViews
        binding.spinnerRoleOwner.isEnabled = state.enableViews
        binding.actvNameDebtorOrCreditor.isEnabled = state.enableViews
        binding.tilEnterValue.isEnabled = state.enableViews
        binding.ivOpenCalculator.isEnabled = state.enableViews
        binding.tilDetailsContainer.isEnabled = state.enableViews
        binding.btnSave.isEnabled = state.enableViews

        fillError(binding.tilEnterValue, state.valueTransactionMessageRes)
    }
    private fun observeAutoCompleteTextView(){
        viewModel.debtors.observe(viewLifecycleOwner){
            val autoCompleteTextView = binding.actvNameDebtorOrCreditor

            val adapter: ArrayAdapter<Debtor> = object : ArrayAdapter<Debtor>(
                requireActivity().applicationContext,
                android.R.layout.simple_dropdown_item_1line,
                it
            ) {
                override fun getView(
                    position: Int,
                    @Nullable convertView: View?,
                    parent: ViewGroup
                ): View {
                    val view = super.getView(position, convertView, parent)
                    val textView = view.findViewById<View>(android.R.id.text1) as TextView
                    textView.setTextColor(Color.BLACK) // устанавливаем черный цвет текста
                    textView.setBackgroundColor(Color.WHITE) // устанавливаем белый цвет фона
                    return view
                }
            }

//            val adapter = ArrayAdapter<Debtor>(requireActivity().applicationContext, android.R.layout.simple_dropdown_item_1line, it)
            autoCompleteTextView.setAdapter(adapter)
        }
    }
    private fun observeCalculator(value: Double){
        calculator = ViewModelProvider(requireActivity()).get(CalculatorContract::class.java)

        calculator.setCalculatorValue(viewModel.getEnteredValue() ?: value)
        calculator.data.observe(requireActivity()) {
            val showValue = it != null
            if(showValue) {
                binding.tietEnterValue.setText(String.format("%.2f", it).replace(',', '.'))
            }
        }
    }

    private fun fillError(input: TextInputLayout, @StringRes stringRes: Int) {
        if (stringRes == NO_ERROR_MESSAGE) {
            input.error = null
            input.isErrorEnabled = false
        } else {
            input.error = getString(stringRes)
            input.isErrorEnabled = true
        }
    }
}