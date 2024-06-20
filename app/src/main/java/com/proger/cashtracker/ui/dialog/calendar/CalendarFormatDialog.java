package com.proger.cashtracker.ui.dialog.calendar;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.core.util.Pair;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.datepicker.MaterialDatePicker;
import com.proger.cashtracker.R;
import com.proger.cashtracker.utils.DateHelper;

import java.util.Date;

public class CalendarFormatDialog extends DialogFragment {
    private CalendarFormatViewModel viewModel;
    private Pair<Date, Date> range;
    private Mode mode;
    private ICloseCalendarDialogInvoker closeDialog;

    public CalendarFormatDialog() { }

    private CalendarFormatDialog(Mode mode, ICloseCalendarDialogInvoker onCloseDialog, Pair<Date, Date> range) {
        this.mode = mode;
        this.closeDialog = onCloseDialog;
        this.range = range;
    }

    public static CalendarFormatDialog newInstance(Mode mode, ICloseCalendarDialogInvoker onCloseDialog, Pair<Date, Date> range) {
        return new CalendarFormatDialog(mode, onCloseDialog, range);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_calendar_format, container, false);

        viewModel = new ViewModelProvider(this).get(CalendarFormatViewModel.class);
        if (viewModel.getModeCalendar() == null) viewModel.setModeCalendar(mode);
        if (viewModel.getCloseCalendar() == null) viewModel.setCloseCalendar(closeDialog);
        if (viewModel.getOutRange() == null) viewModel.setOutRange(range);

        installButtons(view);
        return view;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        viewModel.getCloseCalendar().onCloseDialog(viewModel.getModeCalendar(), viewModel.getOutRange());
    }

    private void installButtons(View view) {
        String _day = DateHelper.Companion.makeDayMonthYearFormat(new Date());

        Date date = (viewModel.getModeCalendar() == Mode.ALL_TIME) ? new Date() : viewModel.getOutRange().first;
        String weekDates = DateHelper.Companion.makeWeekFormat(date);
        String _month = DateHelper.Companion.makeMonthYearFormat(date);
        String _year = DateHelper.Companion.makeYearFormat(date);

        Button day = view.findViewById(R.id.btnDialogInterval1Day);
        day.setText(day.getTag().toString() + ":\n" + _day);

        Button week = view.findViewById(R.id.btnDialogInterval1Week);
        week.setText(week.getTag().toString() + ":\n" + weekDates);

        Button month = view.findViewById(R.id.btnDialogInterval1Month);
        month.setText(month.getTag().toString() + ":\n" + _month);

        Button year = view.findViewById(R.id.btnDialogInterval1Year);
        year.setText(year.getTag().toString() + ":\n" + _year);

        Button allTime = view.findViewById(R.id.btnDialogAllTime);
        Button chooseDay = view.findViewById(R.id.btnDialogChooseDay);
        Button dateRange = view.findViewById(R.id.btnDialogDateRange);

        //выбор кнопки которая на данный момент была выбрана
        Drawable selected = getResources().getDrawable(R.color.selected_btn);
        switch (viewModel.getModeCalendar()) {
            case TODAY:
                day.setBackground(selected);
                break;
            case WEEK:
                week.setBackground(selected);
                break;
            case MONTH:
                month.setBackground(selected);
                break;
            case YEAR:
                year.setBackground(selected);
                break;
            case CHOOSE_DAY:
                chooseDay.setBackground(selected);
                break;
            case DATE_RANGE:
                dateRange.setBackground(selected);
                break;
            case ALL_TIME:
                allTime.setBackground(selected);
                break;
        }

        //Установка обработчиков на кнопки
        day.setOnClickListener(v -> {
            viewModel.setModeCalendar(Mode.TODAY);
            viewModel.setOutRange(DateHelper.Companion.makeDayRange(new Date()));
            dismiss();
        });
        week.setOnClickListener(v -> {
            viewModel.setModeCalendar(Mode.WEEK);
            viewModel.setOutRange(DateHelper.Companion.makeWeekRange(date));
            dismiss();
        });
        month.setOnClickListener(v -> {
            viewModel.setModeCalendar(Mode.MONTH);
            viewModel.setOutRange(DateHelper.Companion.makeMonthRange(date));
            dismiss();
        });
        year.setOnClickListener(v -> {
            viewModel.setModeCalendar(Mode.YEAR);
            viewModel.setOutRange(DateHelper.Companion.makeYearRange(date));
            dismiss();
        });

        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        allTime.setOnClickListener(v -> {
            viewModel.setModeCalendar(Mode.ALL_TIME);
            viewModel.setOutRange(new Pair<>(new Date(0), DateHelper.Companion.getStartNextDay()));
            dismiss();
        });
        chooseDay.setOnClickListener(v -> {
            MaterialDatePicker materialDatePicker = MaterialDatePicker.Builder.datePicker().build();
            materialDatePicker.addOnPositiveButtonClickListener(selection -> {
                if (selection != null) {
                    viewModel.setModeCalendar(Mode.CHOOSE_DAY);
                    viewModel.setOutRange(DateHelper.Companion.makeDayRange(new Date((Long) selection)));
                }
                viewModel.getCloseCalendar().onCloseDialog(viewModel.getModeCalendar(), viewModel.getOutRange());
            });
            materialDatePicker.show(fragmentManager, materialDatePicker.toString());
            dismiss();
        });

        dateRange.setOnClickListener(v -> {
            MaterialDatePicker.Builder<Pair<Long, Long>> builder = MaterialDatePicker.Builder.dateRangePicker();
            MaterialDatePicker<Pair<Long, Long>> datePicker = builder.build();
            datePicker.addOnPositiveButtonClickListener(selection -> {
                if (selection.first != null && selection.second != null) {
                    Pair<Date, Date> selectedDates = new Pair<>(new Date(selection.first),
                            new Date(selection.second));

                    viewModel.setOutRange(new Pair<>(
                            DateHelper.Companion.getStartDay(selectedDates.first),
                            DateHelper.Companion.getFinishDay(selectedDates.second))
                    );
                    viewModel.setModeCalendar(Mode.DATE_RANGE);
                }
                viewModel.getCloseCalendar().onCloseDialog(viewModel.getModeCalendar(), viewModel.getOutRange());
            });
            datePicker.show(fragmentManager, datePicker.toString());
            dismiss();
        });
    }

    public enum Mode {
        TODAY, WEEK, MONTH, YEAR,
        CHOOSE_DAY, DATE_RANGE, ALL_TIME
    }
}