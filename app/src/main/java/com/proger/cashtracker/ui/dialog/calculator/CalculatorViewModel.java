package com.proger.cashtracker.ui.dialog.calculator;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class CalculatorViewModel extends ViewModel {
    protected MutableLiveData<StateCalculator> stateCalculator = new MutableLiveData<>(StateCalculator.Pause);//true = "=", false = "OK"
    protected MutableLiveData<String> stateCalculation;
    protected Double getResult(){
        return Double.parseDouble(calculator.getArg1());
    }
    private final Calculator calculator;

    public CalculatorViewModel(String value){
        calculator = new Calculator(value);
        stateCalculation = new MutableLiveData<>(value);
    }

    protected void enterNumberClick(int number) {
        stateCalculation.setValue(calculator.enterNumberClick(number));
    }
    protected void eraseClick() {
        stateCalculation.setValue(calculator.eraseClick());
    }
    protected void doInvert() {
        stateCalculation.setValue(calculator.doInvert());
    }
    protected void enterDotClick() {
        stateCalculation.setValue(calculator.enterDotClick());
    }
    protected void doOperation(CalculatorHelper.Operations operations) {
        stateCalculation.setValue(calculator.doOperation(operations));
        stateCalculator.setValue(StateCalculator.Calculate);
    }
    protected void doCalculate() {
        if (stateCalculator.getValue() == StateCalculator.Calculate) {
            stateCalculation.setValue(calculator.doCalculate());
            stateCalculator.setValue(StateCalculator.Pause);
        } else if (stateCalculator.getValue() == StateCalculator.Pause) {
            stateCalculator.setValue(StateCalculator.Confirm);
        }
    }

    protected enum StateCalculator {
        Calculate, Pause, Confirm
    }
}