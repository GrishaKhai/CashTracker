package com.proger.cashtracker.ui.dialog.calendar;

import androidx.core.util.Pair;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.Date;

public class CalendarFormatViewModel extends ViewModel {

    private final MutableLiveData<Pair<Date, Date>> outRange = new MutableLiveData<>();
    public Pair<Date, Date> getOutRange() { return outRange.getValue(); }
    public void setOutRange(Pair<Date, Date> outRange) { this.outRange.setValue(outRange); }


    private final MutableLiveData<CalendarFormatDialog.Mode> modeCalendar = new MutableLiveData<>();
    public CalendarFormatDialog.Mode getModeCalendar() { return modeCalendar.getValue(); }
    public void setModeCalendar(CalendarFormatDialog.Mode modeCalendar) { this.modeCalendar.setValue(modeCalendar); }


    private final MutableLiveData<ICloseCalendarDialogInvoker> closeCalendar = new MutableLiveData<>();
    public ICloseCalendarDialogInvoker getCloseCalendar() { return closeCalendar.getValue(); }
    public void setCloseCalendar(ICloseCalendarDialogInvoker closeCalendar) { this.closeCalendar.setValue(closeCalendar); }
}
