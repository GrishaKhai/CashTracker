package com.proger.cashtracker.ui.dialog.calendar;

import androidx.core.util.Pair;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.Date;

public class DateIntervalViewModel extends ViewModel {
    private final MutableLiveData<Pair<Date, Date>> outRange = new MutableLiveData<>();
    public LiveData<Pair<Date, Date>> getOutRangeLiveData() { return outRange; }
    public Pair<Date, Date> getOutRange() { return outRange.getValue(); }
    public void setOutRange(Pair<Date, Date> outRange) { this.outRange.setValue(outRange); }


    private final MutableLiveData<CalendarFormatDialog.Mode> modeCalendar = new MutableLiveData<>();
    public LiveData<CalendarFormatDialog.Mode> getModeCalendarLiveDate() { return modeCalendar; }
    public CalendarFormatDialog.Mode getModeCalendar() { return modeCalendar.getValue(); }
    public void setModeCalendar(CalendarFormatDialog.Mode modeCalendar) { this.modeCalendar.setValue(modeCalendar); }


    private final MutableLiveData<Date> selectedDate = new MutableLiveData<>();
    public Date getSelectedDate() { return selectedDate.getValue(); }
    public void setSelectedDate(Date selectedDate) { this.selectedDate.setValue(selectedDate); }
}