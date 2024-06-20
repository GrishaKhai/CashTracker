package com.proger.cashtracker.ui.elements.spinner;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.proger.cashtracker.R;
import com.proger.cashtracker.models.Account;

public class SpinnerAdapter extends ArrayAdapter<Account> {
    private final LayoutInflater inflater;

    public SpinnerAdapter(Context context, Account[] objects, LayoutInflater inflater) {
        super(context, R.layout.item_spinner_account, objects);
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

    @SuppressLint({"SetTextI18n", "DefaultLocale"})
    public View getCustomView(int position, ViewGroup parent) {
        Account account = getItem(position);
        View row = inflater.inflate(R.layout.item_spinner_account, parent, false);

        TextView labelNameAccount = (TextView) row.findViewById(R.id.tvNameAccount);
        labelNameAccount.setText(account.getName());

        if(account.getCurrency() != null) {
            TextView labelBalance = (TextView) row.findViewById(R.id.tvBalance);
            labelBalance.setText(labelBalance.getTag().toString() + " " + String.format("%.2f", account.getBalance()) + " " + account.getCurrency().getCurrencyCode());
        }

        ImageView icon = (ImageView) row.findViewById(R.id.ivIcon);
        icon.setImageResource(getContext().getResources().getIdentifier(account.getImage(), "drawable", getContext().getPackageName()));
        return row;
    }
}
