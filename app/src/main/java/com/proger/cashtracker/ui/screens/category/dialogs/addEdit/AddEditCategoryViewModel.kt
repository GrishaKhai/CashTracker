package com.proger.cashtracker.ui.screens.category.dialogs.addEdit

import androidx.annotation.StringRes
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.proger.cashtracker.R
import com.proger.cashtracker.db.ExpenseRepository
import com.proger.cashtracker.db.IncomeRepository
import com.proger.cashtracker.models.Category
import com.proger.cashtracker.models.NoteExistInDbException
import com.proger.cashtracker.models.EditedNoteNotExistInDbException
import com.proger.cashtracker.models.EmptyFieldException
import com.proger.cashtracker.models.Field
import com.proger.cashtracker.models.NewNoteDataExitsInDb
import com.proger.cashtracker.utils.MutableLiveEvent
import com.proger.cashtracker.utils.MutableUnitLiveEvent
import com.proger.cashtracker.utils.NO_ERROR_MESSAGE
import com.proger.cashtracker.utils.publishEvent
import com.proger.cashtracker.utils.requireValue
import com.proger.cashtracker.utils.share
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddEditCategoryViewModel @Inject constructor(
    private val incomeRepository: IncomeRepository,
    private val expenseRepository: ExpenseRepository
): ViewModel() {

    //сообщение об ошибках
    private val _showToastEvent = MutableLiveEvent<Int>()
    val showToastEvent = _showToastEvent.share()
    //состояние заполнения данных
    private val _state = MutableLiveData(State())
    val state = _state.share()
    //событие возврата к окну добавления/редактирования дохода или расхода
    private val _goBackEvent = MutableUnitLiveEvent()
    val goBackEvent = _goBackEvent.share()

    //состояния выбранных полей пользователем
    private val selectedImage: MutableLiveData<String> = MutableLiveData()//название картинки
    fun setSelectedImage(image: String){
        selectedImage.value = image
    }
    fun getSelectedImage() = selectedImage.value

    fun addCategory(option: Option, category: Category){
        viewModelScope.launch{
            try{
                when(option){
                    Option.Income -> incomeRepository.insertIncomeCategory(category)
                    Option.Expense -> expenseRepository.insertExpenseCategory(category)
                }
                goBack()
            } catch (e: EmptyFieldException){
                processEmptyFieldException(e)
            } catch (e: NoteExistInDbException){
                processCategoryExistInDbException()
            }
        }
    }
    fun editCategory(option: Option, oldNameCategory: String, category: Category){
        viewModelScope.launch{
            try{
                when(option){
                    Option.Income -> incomeRepository.updateIncomeCategory(oldNameCategory, category)
                    Option.Expense -> expenseRepository.updateExpenseCategory(oldNameCategory, category)
                }
                goBack()
            } catch (e: EmptyFieldException){
                processEmptyFieldException(e)
            } catch(e: NewNoteDataExitsInDb) {
                processCategoryExistInDbException()
            } catch (e: EditedNoteNotExistInDbException){
                goBack()
            }
        }
    }

    private fun processEmptyFieldException(e: EmptyFieldException) {
        when (e.field) {
            Field.Category -> _state.value = _state.requireValue().copy(nameCategoryMessageRes = R.string.category_is_empty)
            Field.Image -> showErrorMessage(R.string.image_is_empty)
            else -> throw IllegalStateException("Unknown field")
        }
    }

    private fun processCategoryExistInDbException() {
        _state.value = _state.requireValue()
            .copy(nameCategoryMessageRes = R.string.category_exist_in_db)
    }

    private fun showErrorMessage(idMessage: Int) = _showToastEvent.publishEvent(idMessage)

    private fun goBack() = _goBackEvent.publishEvent()

    data class State(
        @StringRes val nameCategoryMessageRes: Int = NO_ERROR_MESSAGE
    )


    enum class Option{
        Income, Expense
    }
    enum class Mode{
        Add, Edit
    }
}