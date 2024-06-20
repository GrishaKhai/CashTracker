package com.proger.cashtracker.ui.dialog.calculator;

public class CalculatorHelper {
    protected static String correctEnteredNumber(String oldValue, int addedNumber) {
        String newSpend = oldValue + addedNumber;

        //запрет ввода больше 2 чисел после запятой
        int indexDot = oldValue.indexOf('.');
        if (indexDot >= 0 && oldValue.substring(indexDot + 1).length() == 2) return oldValue;

        //запрет ввода числа больше чем 16 символов (1 000 000 000 000 000)
        if(indexDot >= 0){
            if (newSpend.substring(0, indexDot).length() > 16) return oldValue;
        } else if (newSpend.length() > 16) return oldValue;

        //если было введено 0, а после чего пользователь ввел число больше 0
        if(!oldValue.isEmpty()) {
            double old = Double.parseDouble(oldValue);

            if(old == 0 && oldValue.contains(".")) return newSpend;//если было введено "0." то можно добавить цифру
            else if (old == 0 && addedNumber > 0) newSpend = addedNumber + "";
            else if (old == 0 && addedNumber == 0) return oldValue;
        }

        //TODO check E with stepen

        return newSpend;
    }

    protected static String invert(String value){
        return (value.indexOf("-") == 0) ? value.substring(1) : "-" + value;
    }
    protected static String enterDot(String value){
        if (value.isEmpty()) return "0."; // deprecated
        if (value.indexOf('.') >= 0) return value;
        if (value.toUpperCase().indexOf('E') >= 0) return value;
        return value + ".";
    }

    protected static String makeValueFormat(String value){
        if(isNull(value)){
            return "";
        } else{
            if(value.indexOf(".") != (value.length() - 1) && !value.isEmpty()){
                return (Double.parseDouble(value) < 0) ? ("(" + formatValue(value) + ")") : formatValue(value);
            } else{
                return value;
            }
        }
    }
    public static String formatValue(String value) {
        String result = "";
        String minus = (value.indexOf("-") == 0) ? "-" : "";
        String tmp = (value.indexOf("-") == 0) ? value.substring(1) : value;

        int indexDot = tmp.indexOf(".");
        if (indexDot >= 3) {//точка есть и чисел перед ней достаточно для отделения розряда
            result = tmp.substring(indexDot - 3);
            tmp = tmp.substring(0, indexDot - 3);
        } else if (indexDot > 0) {
            return minus + value;
        }
        else{
            if(tmp.length() >= 3) {
                result = tmp.substring(tmp.length() - 3);
                tmp = tmp.substring(0, tmp.length() - 3);
            } else {
                return minus + tmp;
            }
        }

        while (tmp.length() > 0) {
            if(tmp.length() >= 3){
                result = tmp.substring(tmp.length() - 3) + " " + result;
                tmp = tmp.substring(0, tmp.length() - 3);
            } else {
                result = tmp + " " + result;
                tmp = "";
            }
        }
        return minus + result;
    }

    protected static boolean isNull(String value){
        return value.isEmpty();
    }
    protected static String setNull(){
        return "";
    }

    public enum Operations {
        None(""), Sum("+"), Difference("-"), Division("/"), Multiplication("*");
        private final String operation;

        Operations(String s) {
            operation = s;
        }

        public String getOperation() {
            return operation;
        }
    }
}