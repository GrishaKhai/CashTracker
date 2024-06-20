package com.proger.cashtracker.ui.screens.category.dialogs.addEdit

import android.os.Bundle
import android.text.Editable
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputLayout
import com.proger.cashtracker.R
import com.proger.cashtracker.databinding.DialogAddEditCategoryBinding
import com.proger.cashtracker.models.Category
import com.proger.cashtracker.ui.elements.recyclerView.ImageAdapter
import com.proger.cashtracker.utils.NO_ERROR_MESSAGE
import com.proger.cashtracker.utils.observeEvent
import dagger.hilt.android.AndroidEntryPoint

/*
 *  Для инициализации окна нужно установить значения:
 * MODE_KEY - режим окна редактировать или добавлять
 * OPTION_KEY - с какой категорией работаем, доходы или расходы
 * В случае редактирования категории также нужно указать
 * NAME_KEY - название редактируемой категории
 * IMAGE_KEY - картинка редактируемой категории
 */
@AndroidEntryPoint
class AddEditCategoryDialog() : DialogFragment() {

    private val viewModel: AddEditCategoryViewModel by viewModels()
    private lateinit var binding: DialogAddEditCategoryBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogAddEditCategoryBinding.inflate(inflater)

        initPositionDialog()
        addCategoriesItems(
            viewModel.getSelectedImage() ?: getImage()
        )//если категория новая добавляется - то по итогу функция примет null и не выберет ни какой рисунок
        setListeners()

        val option = getOption()
        when (getMode()) {
            AddEditCategoryViewModel.Mode.Add -> {
                binding.tvHeader.text = if (option == AddEditCategoryViewModel.Option.Expense)
                    requireContext().getString(R.string.label_add_expense_category) else
                    requireContext().getString(R.string.label_add_income_category)
            }
            AddEditCategoryViewModel.Mode.Edit -> {
                binding.tietNameCategory.setText(getOldNameCategory())
                binding.tvHeader.text = if (option == AddEditCategoryViewModel.Option.Expense)
                    requireContext().getString(R.string.label_edit_expense_category) else
                    requireContext().getString(R.string.label_edit_income_category)
            }
        }

        //установка наблюдателей
        observeState()
        observeShowMessageEvent()
        observeGoBackEvent()
        return binding.root
    }

    //todo
    @Deprecated("возможно оно и не каким образом не влияет на расположение окна")
    private fun initPositionDialog() {
        val window = dialog!!.window
        val params = window!!.attributes
        params.gravity = Gravity.CENTER
        params.width = WindowManager.LayoutParams.MATCH_PARENT
        params.horizontalMargin = 0f
        window.setAttributes(params)
    }

    private fun setListeners() {
        binding.btnCancel.setOnClickListener {
            dismiss()
        }

        binding.btnSave.setOnClickListener {
            val category = Category(
                nameCategory = binding.tietNameCategory.text.toString(),
                image = viewModel.getSelectedImage() ?: ""
            )
            val option = getOption()
            when (getMode()) {
                AddEditCategoryViewModel.Mode.Add -> viewModel.addCategory(option, category)
                AddEditCategoryViewModel.Mode.Edit -> viewModel.editCategory(
                    option,
                    getOldNameCategory(),
                    category
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
        binding.rvCategoriesList.adapter = ImageAdapter(iconList, impl)
        if (selectedCategory != null) selectCategory(iconList, selectedCategory)
    }

    private fun selectCategory(categories: List<String>, imageName: String) {
        val position = categories.indexOfFirst { it == imageName }
        if (position != -1) {
            binding.rvCategoriesList.scrollToPosition(position)// Прокрутить RecyclerView к нужной позиции
            //Выделение элемента
            val adapter: RecyclerView.Adapter<*>? = binding.rvCategoriesList.adapter
            if (adapter is ImageAdapter) {
                (adapter as ImageAdapter).selectItem(position)
                viewModel.setSelectedImage(categories[position])
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


    private fun observeState() = viewModel.state.observe(viewLifecycleOwner) { state ->
        fillError(binding.tilNameCategory, state.nameCategoryMessageRes)
    }

    private fun observeShowMessageEvent() =
        viewModel.showToastEvent.observeEvent(viewLifecycleOwner) {
            Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
        }

    private fun observeGoBackEvent() = viewModel.goBackEvent.observeEvent(viewLifecycleOwner) {
        dismiss()
    }

    private fun getOption() =
        AddEditCategoryViewModel.Option.valueOf(requireArguments().getString(OPTION_KEY)!!)

    private fun getMode() =
        AddEditCategoryViewModel.Mode.valueOf(requireArguments().getString(MODE_KEY)!!)

    private fun getOldNameCategory(): String = arguments?.getString(NAME_KEY) ?: ""
    private fun getImage(): String? = arguments?.getString(IMAGE_KEY)

    companion object {
        val MODE_KEY = "mode"
        val OPTION_KEY = "state"
        val NAME_KEY = "name"//old name category
        val IMAGE_KEY = "image"//old image
    }
}