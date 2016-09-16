package com.gps.www.bike_pool;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.format.DateFormat;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Calendar;

/**
 * Created by Balaji on 3/18/2016.
 */


public class TimePickDialog extends DialogFragment
        implements TimePickerDialog.OnTimeSetListener {
TextView tc;
    Calendar t;
    public void setTextClock(TextView te,Calendar tim)
    {
        tc=te;
        t=tim;
    }
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current time as the default values for the picker
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        // Create a new instance of TimePickerDialog and return it
        return new TimePickerDialog(getActivity(), this, hour, minute,
                DateFormat.is24HourFormat(getActivity()));
    }

    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        // Do something with the time chosen by the user


        t.set(Calendar.HOUR_OF_DAY,hourOfDay);
        t.set(Calendar.MINUTE,minute);


       tc.setText(String.format("%tI:%<tM", t.getTime()) + " " + String.format("%tp", t.getTime()).toUpperCase());


    }
}
