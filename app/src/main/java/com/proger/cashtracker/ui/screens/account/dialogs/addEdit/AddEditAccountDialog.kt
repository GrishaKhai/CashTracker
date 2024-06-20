package com.proger.cashtracker.ui.screens.account.dialogs.addEdit

import android.annotation.SuppressLint
import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputLayout
import com.proger.cashtracker.R
import com.proger.cashtracker.databinding.DialogAddEditAccountBinding
import com.proger.cashtracker.databinding.DialogAddEditCategoryBinding
import com.proger.cashtracker.models.Account
import com.proger.cashtracker.models.Category
import com.proger.cashtracker.models.Currency
import com.proger.cashtracker.ui.elements.recyclerView.ImageAdapter
import com.proger.cashtracker.ui.elements.spinner.SpinnerAdapter
import com.proger.cashtracker.ui.elements.spinner.SpinnerAdapterCurrency
import com.proger.cashtracker.ui.screens.category.dialogs.addEdit.AddEditCategoryViewModel
import com.proger.cashtracker.utils.NO_ERROR_MESSAGE
import com.proger.cashtracker.utils.observeEvent
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddEditAccountDialog : DialogFragment() {
    private val viewModel: AddEditAccountViewModel by viewModels()
    private lateinit var binding: DialogAddEditAccountBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogAddEditAccountBinding.inflate(inflater)

        //создание items для выпадающего списка
        viewModel.currency.observe(viewLifecycleOwner) {
            renderCurrencies(
                it,
                viewModel.getSelectedCurrency() ?: getOldCurrency()
            )
        }

        addCategoriesItems(
            viewModel.getSelectedImage() ?: getOldImage()
        )//если категория новая добавляется - то по итогу функция примет null и не выберет ни какой рисунок
        setListeners()

        when (getMode()) {
            AddEditCategoryViewModel.Mode.Add -> {
                binding.tvHeader.text = requireContext().getString(R.string.label_add_account)
            }
            AddEditCategoryViewModel.Mode.Edit -> {
                binding.tietValueBalance.isEnabled = false
                binding.tietValueBalance.setText(getOldBalance().toString())
                binding.tietNameAccount.setText(getOldNameAccount())
                binding.tvHeader.text = requireContext().getString(R.string.label_edit_account)
            }
        }

        //установка наблюдателей
        observeState()
        observeShowMessageEvent()
        observeGoBackEvent()
        return binding.root
    }

    private fun setListeners() {
        binding.btnCancel.setOnClickListener {
            dismiss()
        }
        binding.btnSave.setOnClickListener {
            val account = Account(
                name = binding.tietNameAccount.text.toString(),
                balance = binding.tietValueBalance.text.toString().toDouble(),
                image = viewModel.getSelectedImage() ?: "",
                currency = Currency(currencyCode = viewModel.getSelectedCurrency()!!, currencyName = "", 0.0)
            )
            when (getMode()) {
                AddEditCategoryViewModel.Mode.Add -> viewModel.addAccount(account)
                AddEditCategoryViewModel.Mode.Edit -> viewModel.editAccount(
                    getOldNameAccount(),
                    account
                )
            }
        }
    }

    private fun addCategoriesItems(selectedCategory: String?) {
        val iconList = mutableListOf<String>()
        val fields = R.drawable::class.java.fields
        for (field in fields) {
            val name = field.name
            if (name.startsWith("asset_")) { // Фильтрация иконок
                iconList.add(name)
            }
        }

        val impl = object : ImageAdapter.OnImageClickListener {
            override fun onImageClick(image: String) {
                viewModel.setSelectedImage(image)
            }
        }
        binding.rvIconsList.adapter = ImageAdapter(iconList, impl)
        if (selectedCategory != null) selectCategory(iconList, selectedCategory)
    }

    private fun selectCategory(categories: List<String>, imageName: String) {
        val position = categories.indexOfFirst { it == imageName }
        if (position != -1) {
            binding.rvIconsList.scrollToPosition(position)// Прокрутить RecyclerView к нужной позиции
            //Выделение элемента
            val adapter: RecyclerView.Adapter<*>? = binding.rvIconsList.adapter
            if (adapter is ImageAdapter) {
                (adapter as ImageAdapter).selectItem(position)
                viewModel.setSelectedImage(categories[position])
            }
        }
    }

    @SuppressLint("SetTextI18n")
    fun renderCurrencies(currencies: List<Currency>, selectedCurrency: String?) {
        val spinner = binding.spinnerCurrency
        val adapter = SpinnerAdapterCurrency(context, currencies.toTypedArray(), layoutInflater)
        spinner.adapter = null
        spinner.adapter = adapter
        spinner.setPromptId(R.string.choose_currency)
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?, view: View?, position: Int, id: Long
            ) {
                val selectedItem = spinner.selectedItem as Currency
                viewModel.setSelectedCurrency(selectedItem.currencyCode)
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
        if(selectedCurrency != null) selectCurrency(currencies, selectedCurrency)
        else spinner.setSelection(0)
    }
    private fun selectCurrency(currencies: List<Currency>, currency: String) {
        val position = currencies.indexOfFirst { it.currencyCode == currency }
        if (position != -1) binding.spinnerCurrency.setSelection(position)
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

    @SuppressLint("ResourceType")
    private fun observeState() = viewModel.state.observe(viewLifecycleOwner) { state ->
        binding.tilNameAccount.isEnabled = state.enableViews
        if(getMode() == AddEditCategoryViewModel.Mode.Add) binding.tilValueBalance.isEnabled = state.enableViews
        binding.llEnterValue.isEnabled = state.enableViews
        binding.spinnerCurrency.isEnabled = state.enableViews
        binding.rvIconsList.isEnabled = state.enableViews

        binding.btnSave.isEnabled = state.enableViews
        binding.btnCancel.isEnabled = state.enableViews

        fillError(binding.tilNameAccount, state.nameAccountMessageRes)
        fillError(binding.tilValueBalance, state.balanceAccountMessageRes)
    }

    private fun observeShowMessageEvent() =
        viewModel.showToastEvent.observeEvent(viewLifecycleOwner) {
            Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
        }
    private fun observeGoBackEvent() = viewModel.goBackEvent.observeEvent(viewLifecycleOwner) {
        dismiss()
    }

    private fun getMode(): AddEditCategoryViewModel.Mode = AddEditCategoryViewModel.Mode.valueOf(arguments?.getString(MODE_KEY)!!) //arguments?.getString(MODE_KEY) ?: ""
    private fun getOldNameAccount(): String = arguments?.getString(NAME_KEY) ?: ""
    private fun getOldBalance(): Double = arguments?.getDouble(BALANCE_KEY) ?: 0.0
    private fun getOldCurrency(): String = arguments?.getString(CURRENCY_KEY) ?: ""
    private fun getOldImage(): String? = arguments?.getString(IMAGE_KEY)

    companion object {
        val MODE_KEY = "mode"
        val NAME_KEY = "name"//old name category
        val BALANCE_KEY = "balance"//old balance
        val CURRENCY_KEY = "currency"//old currency
        val IMAGE_KEY = "image"//old image
    }
}