package com.proger.cashtracker.ui.screens.tabs.debt.dialog.payDebt

import android.app.ActionBar
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.textfield.TextInputLayout
import com.proger.cashtracker.R
import com.proger.cashtracker.databinding.DialogPayDebtBinding
import com.proger.cashtracker.utils.DateHelper
import com.proger.cashtracker.utils.DoubleHelper
import com.proger.cashtracker.models.Account
import com.proger.cashtracker.models.Contribution
import com.proger.cashtracker.models.DebtRole
import com.proger.cashtracker.ui.elements.spinner.SpinnerAdapter
import com.proger.cashtracker.ui.screens.category.dialogs.addEdit.AddEditCategoryViewModel
import com.proger.cashtracker.utils.NO_ERROR_MESSAGE
import com.proger.cashtracker.utils.observeEvent
import dagger.hilt.android.AndroidEntryPoint
import java.util.Date

/*
 *  Для инициализации окна нужно установить значения:
 *      РЕДАКТИРОВАНИЕ
 * MODE_KEY - режим окна редактировать или добавлять
 * DATE_KEY - интервал дат долга
 * DEBT_ID_KEY - идентификатор долга, который оплачивается
 * CONTRIBUTION_ID_KEY - идентификатор оплаты
 * ACCOUNT_NAME_KEY - имя счета с которого будет выполнена оплата долга
 * ENTERED_VALUE_KEY - внесенная сумма для оплаты долга, если сумма будет превышать остаток по долгу, то остаток будет переведен на счет, обратно
 *      СОЗДАНИЕ
 * MODE_KEY - режим окна редактировать или добавлять
 * DEBT_ID_KEY - идентификатор долга, который оплачивается
 */
@AndroidEntryPoint
class PayDebtDialog : DialogFragment() {

    private val viewModel: PayDebtViewModel by viewModels()
    private lateinit var binding: DialogPayDebtBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogPayDebtBinding.inflate(inflater)

        setListeners()

        if(viewModel.getSelectedDate() == null) viewModel.setSelectedDate((if(getDate() == null || getDate()!!.toInt() == 0) Date().time else getDate())!!)
        if(getDebtID() != null) viewModel.foundDebt(getDebtID()!!)

        viewModel.accounts.observe(viewLifecycleOwner) { renderAccounts(it, viewModel.getSelectedAccount()?.name ?: getAccountName()) }

        when (getMode()) {
            AddEditCategoryViewModel.Mode.Add -> {
                binding.tvHeader.text = requireContext().getString(R.string.label_add_contribution)
            }
            AddEditCategoryViewModel.Mode.Edit -> {
                binding.tvHeader.text = requireContext().getString(R.string.label_edit_contribution)
                binding.tietEnterDebt.setText(getEnteredValue().toString())
            }
        }
        binding.tvSelectDate.text = DateHelper.makeDayMonthYearFormat(viewModel.getSelectedDate()!!)

        //установка наблюдателей
        observeState()
        observeShowMessageEvent()
        observeGoBackEvent()
        return binding.root
    }


    private fun setListeners() {
        binding.apply {
            btnCancel.setOnClickListener {
                dismiss()
            }
            btnSave.setOnClickListener {
                val contribution = Contribution(
                    date = viewModel.getSelectedDate()!!.time,
                    contribution = tietEnterDebt.text.toString().toDouble(),
                    account = spinnerAccount.selectedItem as Account,
                )
                val roleOwner = if(viewModel.debt.value!!.isDebtor) DebtRole.Debtor else DebtRole.Creditor //getRoleOwner()
                when (getMode()) {
                    AddEditCategoryViewModel.Mode.Add -> viewModel.addPay(contribution, getDebtID()!!, roleOwner)
                    AddEditCategoryViewModel.Mode.Edit -> viewModel.editPay(contribution, getDebtID()!!, getContributionID()!!, roleOwner)
                }
            }
            tvSelectDate.setOnClickListener {
                val materialDatePicker: MaterialDatePicker<*> = MaterialDatePicker.Builder.datePicker().build()
                materialDatePicker.addOnPositiveButtonClickListener { selection: Any? ->
                    if (selection != null) {
                        binding.tvSelectDate.text = DateHelper.makeDayMonthYearFormat(Date(selection as Long))
                        viewModel.setSelectedDate(selection as Long)
                    }
                }
                materialDatePicker.show(requireFragmentManager(), materialDatePicker.toString())
            }
        }
    }

    private fun renderAccounts(account: List<Account>, selectedAccount: String?) {
        val spinner = binding.spinnerAccount
        val adapter = SpinnerAdapter(context, account.toTypedArray(), layoutInflater)
        spinner.adapter = adapter
        spinner.setPromptId(R.string.choose_account)
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?, view: View?, position: Int, id: Long
            ) {
                val selectedItem = spinner.selectedItem as Account
                viewModel.setSelectedAccount(selectedItem)

                viewModel.debt.observe(viewLifecycleOwner){
                    if(it != null) {
                        val vision = it.isDebtor
                        vision(binding.llHeaderAccount, vision)
                        binding.llHeaderAccount.visibility = if(vision) View.VISIBLE else View.INVISIBLE

                        var value = ""
                        if(getMode() == AddEditCategoryViewModel.Mode.Edit){
                            value = DoubleHelper.roundValue((it.debt?.minus(it.returned)!! * selectedItem.currency!!.rateToBase) - getEnteredValue()!!)
                        } else if(getMode() == AddEditCategoryViewModel.Mode.Add) {
                            value = DoubleHelper.roundValue(it.debt?.minus(it.returned)!! * selectedItem.currency!!.rateToBase)
                        }
                        binding.tietRemainderDebt.setText("$value ${selectedItem!!.currency!!.currencyCode}")
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
        if (selectedAccount != null) selectAccount(account, selectedAccount)
        else spinner.setSelection(0)
    }
    private fun selectAccount(accounts: List<Account>, account: String) {
        val position = accounts.indexOfFirst { it.name == account }
        if (position != -1) binding.spinnerAccount.setSelection(position)
        else binding.spinnerAccount.setSelection(accounts.size)
    }

    private fun observeState() = viewModel.state.observe(viewLifecycleOwner) { state ->
        fillError(binding.tilEnterDebt, state.payDebtValueMessageRes)
    }
    private fun observeShowMessageEvent() =
        viewModel.showToastEvent.observeEvent(viewLifecycleOwner) {
            Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
        }
    private fun observeGoBackEvent() = viewModel.goBackEvent.observeEvent(viewLifecycleOwner) {
        dismiss()
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

    private fun vision(view: View, isVisible: Boolean){
        val params = view.layoutParams
        val height = if (!isVisible) 0 else ActionBar.LayoutParams.WRAP_CONTENT
        params?.height = height
        view.setLayoutParams(params)
    }

    private fun getMode() = AddEditCategoryViewModel.Mode.valueOf(arguments?.getString(MODE_KEY)!!)
    private fun getDate() = arguments?.getLong(DATE_KEY)
    private fun getDebtID() = arguments?.getInt(DEBT_ID_KEY)
    private fun getContributionID() = arguments?.getInt(CONTRIBUTION_ID_KEY)
    private fun getAccountName() = arguments?.getString(ACCOUNT_NAME_KEY)
    private fun getEnteredValue() = arguments?.getDouble(ENTERED_VALUE_KEY)

    companion object {
        const val MODE_KEY = "mode"
        const val DATE_KEY = "date"
        const val DEBT_ID_KEY = "debtID"
        const val CONTRIBUTION_ID_KEY = "contributionID"
        const val ACCOUNT_NAME_KEY = "accountName"
        const val ENTERED_VALUE_KEY = "enteredValue"
    }
}