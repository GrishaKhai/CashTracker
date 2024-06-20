package com.proger.cashtracker.ui.dialog.calendar;

import androidx.core.util.Pair;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.Date;

public class CalendarContract extends ViewModel {
    private final MutableLiveData<Pair<Date, Date>> _range = new MutableLiveData<>();
    public LiveData<Pair<Date, Date>> range = _range;
    public void setValue(Date first, Date second) { _range.setValue(new Pair<>(first, second)); }
}
