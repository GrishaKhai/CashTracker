package com.proger.cashtracker.models

open class AppException : RuntimeException()
open class AppExceptionArg(msg: String) : RuntimeException(msg)

class EmptyFieldException(
    val field: Field
) : AppException()
//
//class PasswordMismatchException : AppException()
//
//class AccountAlreadyExistsException : AppException()
//
//class AuthException : AppException()

//некорректная форма даты или значение не находится в допустимых пределах
class IncorrectDateFormatException: AppException()
//todo баланс недостаточный для выполнения операции //class NotEnoughBalanceToTransactionException: AppException()
//некорректный формат значения (например для указания расхода значение должно быть только положительное)
class IncorrectValueFormatException: AppException()

//создаваемая запись уже присутствует в БД
class NoteExistInDbException(msg: String): AppExceptionArg(msg)
//записи нет в БД
class NoteNotExistInDbException(msg: String): AppExceptionArg(msg)
//редактируемой записи нет
class EditedNoteNotExistInDbException(): AppException()
//новое значение записи уже есть в БД, но при этом это не редактируемая запись
class NewNoteDataExitsInDb(): AppException()

//исключение конфликтующих бюджетов, когда добавляемый бюджет попадает в пределы одного из существующих бюджетов
class ConflictBudgetException(msg: String): AppExceptionArg(msg)

//указанные значение должника и одалживающего совпадают
class DebtorAndCreditorEqualsException(): AppException()