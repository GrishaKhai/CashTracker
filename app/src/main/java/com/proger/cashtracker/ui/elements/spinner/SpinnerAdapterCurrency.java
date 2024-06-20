package com.proger.cashtracker.ui.elements.spinner;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.proger.cashtracker.R;
import com.proger.cashtracker.models.Currency;

public class SpinnerAdapterCurrency extends ArrayAdapter<Currency> {
    private final LayoutInflater inflater;

    public SpinnerAdapterCurrency(Context context, Currency[] objects, LayoutInflater inflater) {
        super(context, R.layout.item_spinner_currency, objects);
        this.inflater = inflater;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, parent);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, parent);
    }

    @SuppressLint("MissingInflatedId")
    public View getCustomView(int position, ViewGroup parent) {
        Currency currency = getItem(position);
        View row = inflater.inflate(R.layout.item_spinner_currency, parent, false);
        TextView labelCurrencyName = (TextView) row.findViewById(R.id.tvCurrencyName);
        TextView labelCurrencyCode = (TextView) row.findViewById(R.id.tvCurrencyCode);
        labelCurrencyName.setText((currency.getCurrencyName() == null || currency.getCurrencyName().isEmpty()) ? "": currency.getCurrencyName());
        labelCurrencyCode.setText(currency.getCurrencyCode());
        return row;
    }
}
