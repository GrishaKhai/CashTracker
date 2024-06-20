package com.proger.cashtracker.ui.dialog.calculator;

import static com.proger.cashtracker.ui.dialog.calculator.CalculatorHelper.*;

import com.proger.cashtracker.ui.dialog.calculator.CalculatorHelper.Operations;

import java.text.DecimalFormat;

public class Calculator {
    private String arg1 = "";
    private String arg2 = "";
    private Operations operations = Operations.None;

    public String getArg1() {
        return arg1;
    }


    public Calculator(String value){
        arg1 = value;
    }
    public Calculator(){
        arg1 = "0.0";
    }


    protected String enterNumberClick(int number) {
        if (operations == Operations.None) {//операция с первым числом
            arg1 = correctEnteredNumber(arg1, number);
        } else {//операция со вторым числом
            arg2 = correctEnteredNumber(arg2, number);
        }
        return makeCalcText();
    }


    protected String eraseClick() {
        if(!isNull(arg2)){
            arg2 = (arg2.length() >= 1) ? arg2.substring(0, arg2.length() - 1) : ""; //что бы была возможность стереть значение полностью
        } else if(operations != Operations.None){
            operations = Operations.None;
        } else{
            arg1 = (arg1.length() >= 2) ? arg1.substring(0, arg1.length() - 1) : "0"; //что бы была возможность стереть значение полностью, а программа его заменит на 0
        }
        return makeCalcText();
    }

    protected String doInvert() {
        if(!isNull(arg2)){
            arg2 = invert(arg2);
        } else if(!isNull(arg1)){
            arg1 = invert(arg1);
        }
        return makeCalcText();
    }
    protected String enterDotClick() {
        if(!isNull(arg2)){
            arg2 = enterDot(arg2);
        } else if(!isNull(arg1)){
            arg1 = enterDot(arg1);
        }
        return makeCalcText();
    }

    protected String doOperation(Operations operations) {
        //операция выбирается в процессе ввода второго числа - выполнить расчет, вписать результат и поставить новый знак
        if(!isNull(arg2)){
            doCalculate();
        }
        else if(arg1.indexOf(".") == (arg1.length() - 1)) arg1 = arg1 + "00";

        this.operations = operations;

        return makeCalcText();

//TODO
//        String newSpend, spend = totalSpent.getText().toString();
//        int indexE = spend.toUpperCase().indexOf('E');
//        if(indexE > 0) {
//            newSpend = (spend.substring(indexE + 1).length() == 1) ?
//                    spend.substring(0, indexE) :
//                    spend.substring(0, spend.length() - 1);
//        }
//        else {
//            newSpend = spend.substring(0, spend.length() - 1);
//        }
    }

    protected String doCalculate() {
        if ((operations != Operations.None) && !isNull(arg2)) {
            Double value1 = Double.parseDouble(arg1);
            Double value2 = Double.parseDouble(arg2);
            Double result = null;
            switch (operations) {
                case Sum:
                    result = value1 + value2;
                    break;
                case Division:
                    if (value2 == 0) result = 0.0;
                    else result = value1 / value2;
                    break;
                case Difference:
                    result = value1 - value2;
                    break;
                case Multiplication:
                    result = value1 * value2;
                    break;
            }

            arg1 = (result != null) ? new DecimalFormat( "#.##" ).format(result).replace(",", ".") : "0.00";
            arg2 = setNull();
            operations = Operations.None;
        }
        return makeCalcText();
    }

    private String makeCalcText() {
        String firstOperand = makeValueFormat(arg1);
        String secondOperand = makeValueFormat(arg2);
        return firstOperand + " " + operations.getOperation() + " " + secondOperand;
    }
}