package com.proger.cashtracker.ui.dialog.calculator;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import com.proger.cashtracker.databinding.DialogCalculatorBinding;
import com.proger.cashtracker.ui.dialog.calculator.CalculatorHelper.Operations;


public class CalculatorDialog extends DialogFragment {
    private CalculatorContract calculatorContract;
    private CalculatorViewModel viewModel;
    private DialogCalculatorBinding binding;

    public CalculatorDialog() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DialogCalculatorBinding.inflate(inflater);
        initPositionDialog();
        installListeners();

        calculatorContract = new ViewModelProvider(requireActivity()).get(CalculatorContract.class);
        @SuppressLint("DefaultLocale") String value = String.format("%.2f", calculatorContract.getCalculatorValue()).replace(',', '.');
        viewModel = new CalculatorViewModel(value);

        viewModel.stateCalculator.observe(this, v -> {
            switch (v){
                case Calculate:
                    binding.btnCalculate.setText("=");
                    break;
                case Pause:
                    binding.btnCalculate.setText("OK");
                    break;
                case Confirm:
                    calculatorContract.setCalculatorValue(viewModel.getResult());
                    this.dismiss();
                    break;
            }
        });
        viewModel.stateCalculation.observe(this, v -> binding.tvTotalValue.setText(v));

        return binding.getRoot();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        return dialog;
    }

    private void installListeners() {
        binding.btnInputZeroNumber.setOnClickListener(v -> viewModel.enterNumberClick(0));
        binding.btnInputOneNumber.setOnClickListener(v -> viewModel.enterNumberClick(1));
        binding.btnInputTwoNumber.setOnClickListener(v -> viewModel.enterNumberClick(2));
        binding.btnInputThreeNumber.setOnClickListener(v -> viewModel.enterNumberClick(3));
        binding.btnInputFourNumber.setOnClickListener(v -> viewModel.enterNumberClick(4));
        binding.btnInputFiveNumber.setOnClickListener(v -> viewModel.enterNumberClick(5));
        binding.btnInputSixNumber.setOnClickListener(v -> viewModel.enterNumberClick(6));
        binding.btnInputSevenNumber.setOnClickListener(v -> viewModel.enterNumberClick(7));
        binding.btnInputEightNumber.setOnClickListener(v -> viewModel.enterNumberClick(8));
        binding.btnInputNineNumber.setOnClickListener(v -> viewModel.enterNumberClick(9));
        binding.btnInputDot.setOnClickListener(v -> viewModel.enterDotClick());

        binding.ivBackspace.setOnClickListener(v -> viewModel.eraseClick());

        binding.tvTotalValue.setOnClickListener(v -> viewModel.doInvert());

        binding.btnSumOperation.setOnClickListener(v -> viewModel.doOperation(Operations.Sum));
        binding.btnDifferenceOperation.setOnClickListener(v -> viewModel.doOperation(Operations.Difference));
        binding.btnDivisionOperation.setOnClickListener(v -> viewModel.doOperation(Operations.Division));
        binding.btnMultiplicationOperation.setOnClickListener(v -> viewModel.doOperation(Operations.Multiplication));

        binding.btnCalculate.setOnClickListener(v -> viewModel.doCalculate());
    }
    private void initPositionDialog(){
        Window window = getDialog().getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.gravity = Gravity.BOTTOM;
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.horizontalMargin = 0f;
        window.setAttributes(params);
    }
}