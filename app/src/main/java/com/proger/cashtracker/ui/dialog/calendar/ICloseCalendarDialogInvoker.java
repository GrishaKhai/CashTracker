package com.proger.cashtracker.ui.dialog.calendar;

import androidx.core.util.Pair;

import java.util.Date;

public interface ICloseCalendarDialogInvoker {
    void onCloseDialog(CalendarFormatDialog.Mode mode, Pair<Date, Date> outRange);
}
