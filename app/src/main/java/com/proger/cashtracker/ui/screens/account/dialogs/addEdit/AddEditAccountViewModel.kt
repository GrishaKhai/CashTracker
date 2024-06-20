package com.proger.cashtracker.ui.screens.account.dialogs.addEdit

import androidx.annotation.StringRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.proger.cashtracker.R
import com.proger.cashtracker.db.AccountRepository
import com.proger.cashtracker.db.CurrencyRepository
import com.proger.cashtracker.models.Account
import com.proger.cashtracker.models.Category
import com.proger.cashtracker.models.Currency
import com.proger.cashtracker.models.EditedNoteNotExistInDbException
import com.proger.cashtracker.models.EmptyFieldException
import com.proger.cashtracker.models.Field
import com.proger.cashtracker.models.IncorrectValueFormatException
import com.proger.cashtracker.models.NewNoteDataExitsInDb
import com.proger.cashtracker.models.NoteExistInDbException
import com.proger.cashtracker.ui.screens.category.dialogs.addEdit.AddEditCategoryViewModel
import com.proger.cashtracker.ui.screens.tabs.income.addEdit.AddEditIncomeTransactionViewModel
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
class AddEditAccountViewModel @Inject constructor(
    private val accountRepository: AccountRepository,
    private val currencyRepository: CurrencyRepository
): ViewModel() {

    //сообщение об ошибках
    private val _showToastEvent = MutableLiveEvent<Int>()
    val showToastEvent = _showToastEvent.share()
    //состояние заполнения данных
    private val _state = MutableLiveData(AddEditAccountViewModel.State())
    val state = _state.share()
    //событие возврата к окну добавления/редактирования дохода или расхода
    private val _goBackEvent = MutableUnitLiveEvent()
    val goBackEvent = _goBackEvent.share()

    //состояния выбранных полей пользователем
    private val selectedImage: MutableLiveData<String> = MutableLiveData()//название картинки
    fun setSelectedImage(image: String){ selectedImage.value = image }
    fun getSelectedImage() = selectedImage.value

    private val selectedCurrency: MutableLiveData<String> = MutableLiveData()
    fun setSelectedCurrency(currency: String) { selectedCurrency.value = currency }
    fun getSelectedCurrency() = selectedCurrency.value

    val currency: LiveData<List<Currency>> = currencyRepository.currencies.asLiveData()

    fun addAccount(account: Account){
        viewModelScope.launch{
            showProgress()
            try{
                accountRepository.insertAccount(account)
                goBack()
            } catch (e: EmptyFieldException){
                processEmptyFieldException(e)
            } catch (e: NoteExistInDbException){
                processAccountExistInDbException()
            } catch (e: IncorrectValueFormatException){
                processValueIsNegativeException()
            } finally {
                hideProgress()
                onCleared()
            }
        }
    }
    fun editAccount(oldNameAccount: String, account: Account){
        viewModelScope.launch{
            showProgress()
            try{
                accountRepository.updateAccount(oldNameAccount, account)
                goBack()
            } catch (e: EmptyFieldException){
                processEmptyFieldException(e)
            } catch(e: NewNoteDataExitsInDb) {
                processAccountExistInDbException()
            } catch (e: EditedNoteNotExistInDbException){
                goBack()
            } catch (e: IncorrectValueFormatException){
                processValueIsNegativeException()
            } finally {
                hideProgress()
                onCleared()
            }
        }
    }

    private fun processEmptyFieldException(e: EmptyFieldException) {
        when (e.field) {
            Field.Image -> showErrorMessage(R.string.image_is_empty)
            Field.Account -> _state.value = _state.requireValue().copy(nameAccountMessageRes = R.string.account_name_is_empty)
            Field.Currency -> showErrorMessage(R.string.currency_not_checked)
            else -> throw IllegalStateException("Unknown field")
        }
    }
    private fun processValueIsNegativeException() {
        _state.value = _state.requireValue()
            .copy(balanceAccountMessageRes = R.string.value_is_negative)
    }
    private fun processAccountExistInDbException() {
        _state.value = _state.requireValue()
            .copy(nameAccountMessageRes = R.string.account_exist_in_db)
    }

    private fun showErrorMessage(idMessage: Int) = _showToastEvent.publishEvent(idMessage)

    private fun goBack() = _goBackEvent.publishEvent()

    private fun showProgress() {
        _state.value = State(pushDataProgress = true)
    }
    private fun hideProgress() {
        _state.value = _state.requireValue().copy(pushDataProgress = false)
    }

    data class State(
        @StringRes val nameAccountMessageRes: Int = NO_ERROR_MESSAGE,
        @StringRes val balanceAccountMessageRes: Int = NO_ERROR_MESSAGE,
        val pushDataProgress: Boolean = false,
    ) {
        val enableViews: Boolean get() = !pushDataProgress
    }
}