package com.proger.cashtracker.models;

import com.proger.cashtracker.R;

public enum DebtRole {
    Debtor(R.string.debtor), Creditor(R.string.creditor);

    private final int idString;
    public int getRole() { return idString; }
    DebtRole(int idString){
        this.idString = idString;
    }

    public static DebtRole getDebtRole(int id){
        DebtRole debtRole = null;
        if(id == R.string.debtor) debtRole = Debtor;
        else if(id == R.string.creditor) debtRole = Creditor;
        return debtRole;
    }
}
