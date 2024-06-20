package com.proger.cashtracker.ui.dialog.calendar;

import android.annotation.SuppressLint;
import android.os.Bundle;
import androidx.core.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import com.proger.cashtracker.R;
import com.proger.cashtracker.databinding.FragmentDateIntervalBinding;
import com.proger.cashtracker.utils.DateHelper;

import java.util.Calendar;
import java.util.Date;

@SuppressLint("SimpleDateFormat")
public class DateIntervalFragment extends Fragment {
    private FragmentDateIntervalBinding binding;
    private DateIntervalViewModel viewModel;
    private CalendarContract calendarViewModel;

    public DateIntervalFragment() {}
    public static DateIntervalFragment newInstance() {
        DateIntervalFragment fragment = new DateIntervalFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentDateIntervalBinding.inflate(inflater, container, false);
        calendarViewModel = new ViewModelProvider(getActivity()).get(CalendarContract.class);
        viewModel = new ViewModelProvider(this).get(DateIntervalViewModel.class);

        if(viewModel.getModeCalendar() == null) viewModel.setModeCalendar(CalendarFormatDialog.Mode.TODAY);
        if(viewModel.getSelectedDate() == null) viewModel.setSelectedDate(new Date());
        if(viewModel.getOutRange() == null) viewModel.setOutRange(DateHelper.Companion.makeDayRange(new Date()));

        binding.tvDateView.setText(getActualDateFormat(viewModel.getModeCalendar(), viewModel.getOutRange()));

        setListeners();

        viewModel.getOutRangeLiveData().observe(getViewLifecycleOwner(), it -> {
            if(it == null) calendarViewModel.setValue(null, null);//todo delete here line
            else calendarViewModel.setValue(it.first, it.second);
        });
        viewModel.getModeCalendarLiveDate().observe(getViewLifecycleOwner(), it -> {
            boolean isEnable = true;
            int visiblity = View.VISIBLE;
            if(it == CalendarFormatDialog.Mode.ALL_TIME || it == CalendarFormatDialog.Mode.DATE_RANGE){
                isEnable = false;
                visiblity = View.INVISIBLE;
            }
            binding.btnNext.setEnabled(isEnable);
            binding.btnPrevious.setEnabled(isEnable);
            binding.btnNext.setVisibility(visiblity);
            binding.btnPrevious.setVisibility(visiblity);
        });

        return binding.getRoot();
    }

    private void setListeners() {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        //обработчик запуска диалога
        binding.tvDateView.setOnClickListener(v -> {
            CalendarFormatDialog dialogFragment = CalendarFormatDialog.newInstance(
                    viewModel.getModeCalendar(),
                    this::onCloseDialog,
                    viewModel.getOutRange());
            dialogFragment.show(fragmentManager, "MyDialog");
        });
        binding.btnPrevious.setOnClickListener(v -> setListenersForButtonNextPreview(false));
        binding.btnNext.setOnClickListener(v -> setListenersForButtonNextPreview(true));
    }


    private void onCloseDialog(CalendarFormatDialog.Mode mode, Pair<Date, Date> outRange) {
        String text = getActualDateFormat(mode, outRange);
        binding.tvDateView.setText(text);

        if(mode == CalendarFormatDialog.Mode.CHOOSE_DAY)
            viewModel.setSelectedDate(outRange.first);
        else if(mode == CalendarFormatDialog.Mode.TODAY)
            viewModel.setSelectedDate(new Date());

        viewModel.setModeCalendar(mode);
        viewModel.setOutRange(outRange);
    }

    private String getActualDateFormat(CalendarFormatDialog.Mode mode, Pair<Date, Date> dateArg){
        String text = "";
        if (mode == CalendarFormatDialog.Mode.ALL_TIME){//пользователь выбрал за все время
            text = getString(R.string.all_time);
        } else {//пользователь выбрал: сегодня, определенная дата, неделя, месяц, год, интервал дат
            Date date = dateArg.first;
            switch (mode) {
                case CHOOSE_DAY:
                case TODAY:
                    text = DateHelper.Companion.makeDayMonthYearFormat(date);
                    break;
                case WEEK:
                    text = DateHelper.Companion.makeWeekFormat(date);
                    break;
                case MONTH:
                    text = DateHelper.Companion.makeMonthYearFormat(date);
                    break;
                case YEAR:
                    text = DateHelper.Companion.makeYearFormat(date);
                    break;
                case DATE_RANGE:
                    text = DateHelper.Companion.makeRangeFormat(dateArg.first, dateArg.second);
                    break;
            }
        }
        return text;
    }

    private void setListenersForButtonNextPreview(boolean isPlus){
        Calendar currentDate = Calendar.getInstance();
        currentDate.setTime((Date) viewModel.getSelectedDate());
        switch (viewModel.getModeCalendar()) {
            case TODAY:
            case CHOOSE_DAY:
                currentDate.add(Calendar.DAY_OF_YEAR, isPlus ? 1 : -1);
                viewModel.setOutRange(DateHelper.Companion.makeDayRange(currentDate.getTime()));
                break;
            case WEEK:
                currentDate.add(Calendar.DAY_OF_YEAR, isPlus ? 7: -7);
                viewModel.setOutRange(DateHelper.Companion.makeWeekRange(currentDate.getTime()));
                break;
            case MONTH:
                currentDate.add(Calendar.MONTH, isPlus ? 1: -1);
                viewModel.setOutRange(DateHelper.Companion.makeMonthRange(currentDate.getTime()));
                break;
            case YEAR:
                currentDate.add(Calendar.YEAR, isPlus ? 1: -1);
                viewModel.setOutRange(DateHelper.Companion.makeYearRange(currentDate.getTime()));
                break;
        }
        viewModel.setSelectedDate(currentDate.getTime());
        String text = getActualDateFormat(viewModel.getModeCalendar(), viewModel.getOutRange());
        binding.tvDateView.setText(text);
    }
}